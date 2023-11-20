package com.egpayawal.common.base

import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Lifecycle
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BasePagingAdapter<T : Any, VH : BasePagingAdapter.BaseViewHolder<T>>(
    diffUtil: DiffUtil.ItemCallback<T>
) : PagingDataAdapter<T, VH>(diffUtil) {

    open class BaseViewHolder<T>(open val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        var itemPosition: Int = -1

        open fun bind(item: T) {
            binding.setVariable(BR.item, item)
            binding.executePendingBindings()
        }
    }

    open fun updateItems(lifeCycle: Lifecycle, data: PagingData<T>) {
        submitData(lifeCycle, data)
    }
}
