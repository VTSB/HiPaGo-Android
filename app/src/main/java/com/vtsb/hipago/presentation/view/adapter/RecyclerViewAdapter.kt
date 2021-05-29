package com.vtsb.hipago.presentation.view.adapter

import androidx.recyclerview.widget.RecyclerView

abstract class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val listener: Listener = object: Listener {
//        override fun onItemChanged(position: Int) {
//            AndroidSchedulers.mainThread().scheduleDirect { notifyItemChanged(position) }
//        }
//        override fun onRangeInserted(positionStart: Int, itemCount: Int) {
//            AndroidSchedulers.mainThread().scheduleDirect { onRangeInsertedSync(positionStart, itemCount) }
//        }
//        override fun onRangeRemoved(positionStart: Int, itemCount: Int) {
//            AndroidSchedulers.mainThread().scheduleDirect { onRangeRemovedSync(positionStart, itemCount) }
//        }

        override fun onRangeInsertedSync(positionStart: Int, itemCount: Int) {
            notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onRemovedSync(position: Int) {
            notifyItemRemoved(position)
        }

        override fun onRangeRemovedSync(positionStart: Int, itemCount: Int) {
            notifyItemRangeRemoved(positionStart, itemCount)
        }
    }

    fun getListener(): Listener = listener

    interface Listener {
//        fun onItemChanged(position: Int)
//        fun onRangeInserted(positionStart: Int, itemCount: Int)
//        fun onRangeRemoved(positionStart: Int, itemCount: Int)
        fun onRangeInsertedSync(positionStart: Int, itemCount: Int)
        fun onRemovedSync(position: Int)
        fun onRangeRemovedSync(positionStart: Int, itemCount: Int)
    }

}