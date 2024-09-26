package com.example.myapplication.Helper

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.RecyclerView.CardAdapter

class SimpleItemTouchHelperCallback(adapter: CardAdapter) : ItemTouchHelper.Callback() {

    private val adapter: CardAdapter

    init {
        this.adapter = adapter
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false // 不支持拖拽排序
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
        // 不支持滑动删除
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false // 禁用长按拖拽
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            // 当用户手指离开屏幕时，检查是否需要删除项目
            if (adapter.isDeleteEnabled) {
                if (viewHolder != null) {
                    adapter.deleteItem(viewHolder.adapterPosition)
                }
                // 禁用删除模式
                adapter.isDeleteEnabled = false
            }
        }
    }
}