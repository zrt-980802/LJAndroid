package com.example.myapplication.RecyclerView

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Data.CardItem
import com.example.myapplication.Listener.OnLoadMoreListener
import com.example.myapplication.R
import com.squareup.picasso.Picasso

class CardAdapter(private val cardItems: MutableList<CardItem>,
                  private val loadMoreListener: OnLoadMoreListener,
                  private val onDeleteItem: (Int) -> Unit) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {
    var isDeleteEnabled = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return CardViewHolder(view, loadMoreListener,onDeleteItem)
    }
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val cardItem = cardItems[position]
        // 使用 Picasso 加载图片
        Picasso.get().load(cardItem.photoResId).into(holder.imageView)
        holder.textViewName.text = cardItem.name
        holder.textViewTitle.text = cardItem.title
        holder.textViewEducation.text = cardItem.education

        // 设置点击事件
        holder.imageView.setOnClickListener {
            val url = cardItem.linkUrl // 假设你的数据模型中有一个链接字段
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            holder.itemView.context.startActivity(intent)
        }

        holder.bind(cardItems[position])
    }
    override fun getItemCount(): Int = cardItems.size

    inner class CardViewHolder(itemView: View,loadMoreListener: OnLoadMoreListener, private val onDeleteItem: (Int) -> Unit)
        : RecyclerView.ViewHolder(itemView) {
        fun bind(cardItem: CardItem) {
            itemView.setOnLongClickListener {
                // 显示删除确认对话框
                showDeleteConfirmationDialog(adapterPosition)
                true
            }
        }
        private fun showDeleteConfirmationDialog(position: Int) {
            val builder = AlertDialog.Builder(itemView.context)
            builder.setTitle("删除卡片")
            builder.setMessage("确定要删除这张卡片吗？")
            builder.setPositiveButton("删除") { dialog, which ->
                onDeleteItem(position)
                dialog.dismiss()
            }
            builder.setNegativeButton("取消") { dialog, which ->
                dialog.cancel()
            }
            // 设置删除按钮为红色
            val b = builder.create()
            val d = b.getButton(AlertDialog.BUTTON_POSITIVE)
            if (d != null) {
                val deleteButtonRed = itemView.context.resources.getColor(R.color.delete_button_red)
                d.setTextColor(deleteButtonRed)
            }
            b.show()
        }
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        val textViewEducation: TextView = itemView.findViewById(R.id.textViewEducation) // 添加对 education TextView 的引用
        init {
            itemView.setOnClickListener {
                if (adapterPosition == itemCount - 1) {
                    loadMoreListener.onLoadMore()
                }
            }
        }
        init {
            itemView.setOnLongClickListener {
                isDeleteEnabled = true
                true // 返回 true 表示已经处理了长按事件
            }
        }
    }
    fun deleteItem(position: Int) {
        cardItems.removeAt(position)
        notifyItemRemoved(position)
    }
}