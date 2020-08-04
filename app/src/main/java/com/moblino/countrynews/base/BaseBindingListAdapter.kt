/*
 *  Copyright (c) 2020. Faruk Toptaş
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.moblino.countrynews.base

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseBindingListAdapter<T, in DB : ViewDataBinding> : RecyclerView.Adapter<BaseBindingListAdapter<T, DB>.BaseViewHolder>() {

    var items: List<T>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var itemClickListener: ((T) -> Unit)? = null

    abstract fun layoutResource(): Int

    abstract fun bindingVariableId(): Int


    open fun bind(holder: BaseViewHolder, item: T?, position: Int) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<DB>(layoutInflater, layoutResource(), parent, false)

        return BaseViewHolder(binding, bindingVariableId())
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        items?.let {
            val item = it[position]
            holder.bind(item)
            bind(holder, item, position)
            holder.itemView.setOnClickListener {
                itemClickListener?.invoke(item)
            }
            bind(holder, item, position)
        }
    }


    override fun getItemCount() = items?.size ?: 0


    inner class BaseViewHolder(val binding: ViewDataBinding, private val variableId: Int) : RecyclerView.ViewHolder(binding.root) {

        private val views = hashMapOf<Int, View>()

        fun <T : View> getItemById(@IdRes id: Int): T {
            if (!views.containsKey(id)) {
                views[id] = itemView.findViewById<T>(id)
            }
            return views[id] as T
        }

        fun bind(item: T?) {
            binding.setVariable(variableId, item)
            binding.executePendingBindings()
        }
    }
}