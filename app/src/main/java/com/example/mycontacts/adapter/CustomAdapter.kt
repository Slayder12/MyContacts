package com.example.mycontacts.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycontacts.R
import com.example.mycontacts.models.ContactModel

class CustomAdapter(
    private val contactModelList: MutableList<ContactModel>,
) : RecyclerView.Adapter<CustomAdapter.ItemViewHolder>() {

    interface OnItemClickListener {
        fun onCallClick(item: ContactModel, position: Int)
        fun onMassageClick(item: ContactModel, position: Int)
    }

    private var onItemClickListener: OnItemClickListener? = null

    class ItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val nameTV: TextView = itemView.findViewById(R.id.nameTV)
        val phoneTV: TextView = itemView.findViewById(R.id.phoneNumberTV)
        val messageIV: ImageView = itemView.findViewById(R.id.messageIV)
        val callIV: ImageView = itemView.findViewById(R.id.callIV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = contactModelList[position]

        holder.nameTV.text = item.name
        holder.phoneTV.text = (item.phone)

        holder.callIV.setOnClickListener{
            if (onItemClickListener != null){
                onItemClickListener!!.onCallClick(item, position)
            }
        }
        holder.messageIV.setOnClickListener{
            if (onItemClickListener != null){
                onItemClickListener!!.onMassageClick(item, position)
            }
        }
    }

    override fun getItemCount() = contactModelList.size

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener){
        this.onItemClickListener = onItemClickListener
    }

}