package ru.practicum.sprint12koh7

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.random.Random

class Cart(
    val context: Context
) {

    var items: List<Item> = emptyList()

    var onCartChangeObserver: OnCartChangeObserver? = null

    fun init() {
        val sharedPreferences = context.getSharedPreferences("CART", Context.MODE_PRIVATE)
        val gson = Gson()

        val json: String? = sharedPreferences.getString("ITEMS", null)
        items = if (!json.isNullOrEmpty()) {
            gson.fromJson(json, object : TypeToken<List<Item>>() {}.type)
        } else {
            (1..5).map {
                Item(
                    id = "id_$it",
                    name = "Товар №$it",
                    price = Random.nextDouble(100.0),
                    count = 1,
                )
            }
        }


    }

    fun increaseCount(item: Item) {
        val index = items.indexOfFirst { it.id == item.id }
        val updatedItem = item.copy(
            count = item.count + 1
        )
        items = items.toMutableList().apply {
            set(index, updatedItem)
        }
        save()
    }

    fun decreaseCount(item: Item) {
        val index = items.indexOfFirst { it.id == item.id }
        val updatedItem = item.copy(
            count = item.count - 1
        )
        if (updatedItem.count < 1) {
            items = items.toMutableList().apply {
                removeAt(index)
            }
        } else {
            items = items.toMutableList().apply {
                set(index, updatedItem)
            }
        }
        save()
    }

    fun clear(){
        items = emptyList()
        save()
    }

    private fun save() {
        val sharedPreferences = context.getSharedPreferences("CART", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = gson.toJson(items)
        sharedPreferences
            .edit()
            .putString("ITEMS", json)
            .apply()
        onCartChangeObserver?.onCartChange(items)
    }

    interface OnCartChangeObserver {
        fun onCartChange(items: List<Item>)
    }
}