package com.example.pizzackt.placeholder

import android.util.Log
import com.example.pizzackt.data.DataPizza
import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing sample content for user interfaces
 *
 */
object PlaceholderContent {

    /**
     * An array of sample (placeholder) items.
     */
    var ITEMS: MutableList<DataPizza> = ArrayList()

    /**
     * A map of sample (placeholder) items, by ID.
     */
    var ITEM_MAP: MutableMap<String, DataPizza> = HashMap()

    private var nextPosition = 0;

//    private val COUNT = 25

    init {
        // Add some sample items.
//        for (i in 1..COUNT) {
//            addItem(createPlaceholderItem(i))
//        }
    }

    public fun itemToId(item: DataPizza) : String {
        var id  = item.id
        if (item.id.isNullOrEmpty())
            id = item._id
        return id
    }

    public fun clearAll() {
        nextPosition=0
        ITEM_MAP = HashMap()
        ITEMS = ArrayList()
    }

    public fun addItem(item: DataPizza) {
        val position = nextPosition
        val id = itemToId(item)
//        Log.d("Pizza", "Pizza id: $id")
        ITEMS.add(item)
        ITEM_MAP.put(id, item)

//        Log.d("Pizza", "Pizza addItem Position: $position")
//        Log.d("Pizza", "Pizza Data: " + item.toString())
        nextPosition++
    }

    public fun updateItem(position: Int, item: DataPizza) {
        val id = itemToId(item)
        ITEMS.removeAt(position)
        ITEMS.add(position, item)
        ITEM_MAP.put(id, item)
    }

    fun getItemData(position: Int) : DataPizza? {
        val item = ITEMS[position]
        val id = itemToId(item)
        return ITEM_MAP.get(id)
    }

    fun removeItem(position: Int) : Boolean{
        val item = ITEMS[position]
        val id = itemToId(item)
        ITEMS.removeAt(position);
        ITEM_MAP.remove(id)
        return true
    }

    public fun createPlaceholderItem(position: Int, item: DataPizza): PlaceholderItem {
        val id = itemToId(item)
        return PlaceholderItem((position).toString(), id, makeContent(position, item), makeDetails(position, item))
    }

    private fun makeContent(position: Int, item: DataPizza): String {
        val builder = StringBuilder()
        builder.append(item?.title)
        builder.append("\n")
        builder.append("Price: $"). append(item?.price)
        return builder.toString()
    }


    private fun makeDetails(position: Int, item: DataPizza): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    /**
     * A placeholder item representing a piece of content.
     */
    data class PlaceholderItem(val id: String, val itemId: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}