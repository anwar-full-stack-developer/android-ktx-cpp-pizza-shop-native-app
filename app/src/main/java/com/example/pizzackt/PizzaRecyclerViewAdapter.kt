package com.example.pizzackt

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.pizzackt.data.DataPizza
import com.example.pizzackt.databinding.PizzaListItemBinding
import com.example.pizzackt.placeholder.PlaceholderContent

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 */
class PizzaRecyclerViewAdapter(
    private var values: List<DataPizza> = ArrayList()
) : RecyclerView.Adapter<PizzaRecyclerViewAdapter.ViewHolder>() {
    
    private var listener: OnClickListener? = null

    // A function to bind the onclickListener.
    fun setOnClickListener(listener: OnClickListener) {
        this.listener = listener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, model: DataPizza)
        fun onLongClick(position: Int, model: DataPizza)
        fun onClickToEdit(position: Int, model: DataPizza)
        fun onClickToDelete(position: Int, model: DataPizza)
    }

    fun submitList(d: List<DataPizza>) {
//        values.clear()
//        values.addAll(d)
        values = d
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            PizzaListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        val cpi = PlaceholderContent.createPlaceholderItem(position, item)
        holder.idView.text = cpi.id
        holder.contentView.text = cpi.content
        holder.btnEditView.setOnClickListener({
            if (listener != null) {
                listener!!.onClickToEdit(position, item )
            }
        })
        holder.btnDeleteView.setOnClickListener({
            if (listener != null) {
                listener!!.onClickToDelete(position, item )
            }
        })
        holder.itemView.setOnClickListener {
            if (listener != null) {
                listener!!.onClick(position, item )
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: PizzaListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content
        val btnEditView: TextView = binding.buttonEdit
        val btnDeleteView: TextView = binding.buttonDelete


        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}