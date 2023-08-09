package ru.practicum.sprint12koh7

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.sprint12koh7.databinding.VItemBinding

data class Item(
    val id: String,
    val name: String,
    val price: Double,
    val count: Int,
)

class ItemViewHolder(parenView: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parenView.context)
        .inflate(R.layout.v_item, parenView, false)
) {

    val binding = VItemBinding.bind(itemView)

    fun bind(item: Item) {
        binding.name.text = "${item.name} - ${String.format("%.02f", item.price)} Р"
        binding.count.text = item.count.toString()
    }

    fun setOnPlusListener(action: () -> Unit) {
        binding.plus.setOnClickListener {
            action()
        }
    }

    fun setOnMinusListener(listener: OnMinusButtonClickListener) {
        binding.minus.setOnClickListener {
            listener.action()
        }
    }

}

fun interface OnMinusButtonClickListener {
    fun action()
}