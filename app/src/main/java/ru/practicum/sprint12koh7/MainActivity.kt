package ru.practicum.sprint12koh7

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.Currency

class MainActivity : AppCompatActivity(), ItemsAdapter.OnItemButtonsClickListener,
    Cart.OnCartChangeObserver {

    private val cart: Cart = Cart(this)

    private lateinit var sumButton: MaterialButton

    private val itemsAdapter: ItemsAdapter = ItemsAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cart.init()
        cart.onCartChangeObserver = this
        setContentView(R.layout.activity_main)
        findViewById<RecyclerView>(R.id.items).apply {
            adapter = itemsAdapter
        }
        itemsAdapter.updateItems(cart.items)

        sumButton = findViewById(R.id.sum_btn)
        findViewById<MaterialButton>(R.id.clear_btn).apply {
            setOnClickListener {
                cart.clear()
            }
        }
        showItemsSum(cart.items)
    }

    private fun showItemsSum(items: List<Item>) {
        val totalSum = items
            .map {
                it.price * it.count
            }
            .sum()
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 2
        format.setCurrency(Currency.getInstance("RUB"))

        val totalSumText = format.format(totalSum)
        sumButton.text = "Оформить. Сумма $totalSumText"
    }

    override fun onPlusClick(item: Item) {
        Log.d("MainActivity", "onPlusClick $item")
        cart.increaseCount(item)
    }

    override fun onMinusClick(item: Item) {
        Log.d("MainActivity", "onMinusClick $item")
        cart.decreaseCount(item)
    }

    override fun onCartChange(items: List<Item>) {
        itemsAdapter.updateItems(items)
        showItemsSum(items)
    }
}


class ItemsAdapter(
    val onItemButtonsClickListener: OnItemButtonsClickListener
) : RecyclerView.Adapter<ItemViewHolder>() {

    private var items: List<Item> = emptyList()

    fun updateItems(newItems: List<Item>) {
        val oldItems = items
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldItems.size
            }

            override fun getNewListSize(): Int {
                return newItems.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition].id == newItems[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }

        })
        items = newItems

        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.setOnPlusListener { onItemButtonsClickListener.onPlusClick(item) }
        holder.setOnMinusListener { onItemButtonsClickListener.onMinusClick(item) }
    }

    interface OnItemButtonsClickListener {
        fun onPlusClick(item: Item)
        fun onMinusClick(item: Item)
    }

}