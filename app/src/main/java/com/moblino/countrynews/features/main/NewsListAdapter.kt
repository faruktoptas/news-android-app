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

package com.moblino.countrynews.features.main

import android.content.Context
import androidx.databinding.DataBindingUtil
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.moblino.countrynews.BR
import com.moblino.countrynews.NewsApplication
import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseBindingListAdapter
import com.moblino.countrynews.databinding.ItemRecyclerBinding
import com.moblino.countrynews.databinding.ItemRecyclerStaggeredBinding
import com.moblino.countrynews.databinding.LayoutActionCardBinding
import com.moblino.countrynews.ext.isTrue
import com.moblino.countrynews.models.RssItemWrapper
import com.moblino.countrynews.utils.PreferenceWrapper
import com.moblino.countrynews.utils.Utils

class NewsListAdapter(
        private val context: Context,
        private val pref: PreferenceWrapper) : BaseBindingListAdapter<RssItemWrapper, ItemRecyclerStaggeredBinding>() {

    var onItemClickListener: OnNewsItemClickListener? = null
    var cardRowActionListener: CardRowActionListener? = null
    private val fontHelper = NewsApplication.instance.fontSizeHelper
    private val isNightMode = Utils.isNightMode(context.resources)

    override fun layoutResource() = R.layout.item_recycler_staggered

    override fun bindingVariableId() = BR.rssItemStaggered

    override fun getItemViewType(position: Int): Int {
        return if (items?.get(position)?.card != null) {
            TYPE_ACTION
        } else TYPE_CONTENT
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        when (viewType) {
            TYPE_CONTENT -> {
                return if (pref.readStaggered()) {
                    BaseViewHolder(DataBindingUtil.inflate<ItemRecyclerStaggeredBinding>(
                            layoutInflater,
                            R.layout.item_recycler_staggered,
                            parent,
                            false
                    ), BR.rssItemStaggered)
                } else {
                    BaseViewHolder(DataBindingUtil.inflate<ItemRecyclerBinding>(
                            layoutInflater,
                            R.layout.item_recycler,
                            parent,
                            false), BR.rssItem)
                }
            }
            else -> {
                return BaseViewHolder(DataBindingUtil.inflate<LayoutActionCardBinding>(
                        layoutInflater,
                        R.layout.layout_action_card,
                        parent,
                        false
                ), BR.cardItem)
            }
        }
    }

    override fun bind(holder: BaseViewHolder, item: RssItemWrapper?, position: Int) {
        super.bind(holder, item, position)
        item?.let {
            when (val b = holder.binding) {
                is ItemRecyclerStaggeredBinding -> {
                    holder.itemView.setOnClickListener { _ ->
                        onItemClickListener?.onItemSelected(it.rssItem, position)
                    }
                    bindStaggered(b, it, position)
                }
                is ItemRecyclerBinding -> {
                    holder.itemView.setOnClickListener { _ ->
                        onItemClickListener?.onItemSelected(it.rssItem, position)
                    }
                    bindNormal(b, it, position)
                }
                is LayoutActionCardBinding -> bindCardQuestion(b, it)
            }
        }

    }

    private fun bindCardQuestion(binding: LayoutActionCardBinding, wrapper: RssItemWrapper) {
        binding.ivAction1.setImageResource(wrapper.card?.imageResId ?: 0)
        binding.btnAction1.setOnClickListener { cardRowActionListener?.onActionItemPositive(wrapper.card!!) }
        binding.btnAction2.setOnClickListener { cardRowActionListener?.onActionItemNegative(wrapper.card!!) }
        binding.ivClose.setOnClickListener { cardRowActionListener?.onActionItemNegative(wrapper.card!!) }
    }

    private fun bindNormal(binding: ItemRecyclerBinding, wrapper: RssItemWrapper, position: Int) {
        val showThumb = Utils.isLoadImagesEnabled(context)
        val isStaggered = false
        binding.rlFav.setOnClickListener { view -> onItemClickListener?.onFavouriteClicked(view, wrapper.rssItem, position, (isStaggered || !showThumb) && !isNightMode) }

        if (Utils.isFavourited(wrapper.rssItem?.link) > -1) {
            binding.ivFav.setImageResource(if ((isStaggered || !showThumb) && !isNightMode) R.drawable.ic_favorite_black_24dp else R.drawable.ic_favorite_white_24dp)
            binding.ivFav.tag = true
        } else {
            binding.ivFav.setImageResource(if ((isStaggered || !showThumb) && !isNightMode) R.drawable.ic_favorite_border_black_24dp else R.drawable.ic_favorite_border_white_24dp)
            binding.ivFav.tag = false
        }
        if (!showThumb) {
            binding.llTextContainer.setBackgroundColor(Color.TRANSPARENT)
            binding.tvTitle.setTextAppearance(context, R.style.ItemTitleStyleBlack)
        } else {
            binding.llTextContainer.setBackgroundColor(Color.parseColor("#aa000000")) //todo use image
            binding.tvTitle.setTextAppearance(context, R.style.ItemTitleStyleWhite)
        }

        fontHelper.listTitle(binding.tvTitle)
        fontHelper.listPubdate(binding.tvHoursAgo)
    }

    private fun bindStaggered(binding: ItemRecyclerStaggeredBinding, wrapper: RssItemWrapper, position: Int) {
        binding.rlFav.setOnClickListener { view -> onItemClickListener?.onFavouriteClicked(view, wrapper.rssItem, position, !isNightMode) }

        binding.llTextContainer.setBackgroundColor(Color.TRANSPARENT)
        binding.tvTitle.setTextAppearance(context, R.style.ItemTitleStyleBlack)

        fontHelper.listTitle(binding.tvTitle)
        fontHelper.listPubdate(binding.tvHoursAgo)
    }

    fun removeFirstItem() {
        if (items?.isNotEmpty().isTrue()) {
            items = items!!.subList(1, items!!.size - 1)
        }
    }

    companion object {
        const val TYPE_CONTENT = 1;
        const val TYPE_ACTION = 2;
    }
}