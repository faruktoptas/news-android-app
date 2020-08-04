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

package com.moblino.countrynews.features.editlist

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.MotionEvent
import android.widget.CheckBox
import android.widget.ImageView
import com.moblino.countrynews.BR
import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseBindingListAdapter
import com.moblino.countrynews.databinding.RowEditFeedsBinding
import com.moblino.countrynews.models.FeedItem

class SortableAdapter(private val vm: EditFeedsViewModel) : BaseBindingListAdapter<FeedItem, RowEditFeedsBinding>() {

    private lateinit var dragStartListener: (vh: RecyclerView.ViewHolder) -> Unit

    var isDragging = false

    override fun layoutResource() = R.layout.row_edit_feeds

    override fun bindingVariableId() = BR.feed

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(holder: BaseViewHolder, item: FeedItem?, position: Int) {
        super.bind(holder, item, position)
        holder.getItemById<ImageView>(R.id.ivDrag).setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                isDragging = true
                dragStartListener.invoke(holder)
            }
            true
        }

        holder.getItemById<CheckBox>(R.id.checkTitle).let {
            it.setOnCheckedChangeListener(null)
            it.isChecked = vm.isChecked(item?.feedId ?: -1)
            it.setOnCheckedChangeListener { _, _ ->
                vm.checkedChanged(item)
            }
        }


        holder.itemView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) isDragging = false
            true
        }


    }

    fun enableDragDrop(rv: RecyclerView, onItemMove: (Int, Int) -> Unit) {
        val helper = RecyclerViewDragHelper(this, onItemMove)
        val dragListener = { vh: RecyclerView.ViewHolder ->
            helper.startDrag(vh)
        }

        this.dragStartListener = dragListener
        rv.adapter = this
        helper.attachToRecyclerView(rv)
    }

}