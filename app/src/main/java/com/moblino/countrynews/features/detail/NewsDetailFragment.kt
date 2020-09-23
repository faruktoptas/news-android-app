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

package com.moblino.countrynews.features.detail

import androidx.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.moblino.countrynews.NewsApplication
import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseFragment
import com.moblino.countrynews.data.AppCache
import com.moblino.countrynews.ext.*
import com.moblino.countrynews.features.main.NewsListViewModel
import com.moblino.countrynews.ext.hide
import com.moblino.countrynews.util.TemporaryUtil
import com.moblino.countynews.common.model.RssItem
import com.moblino.countrynews.util.UIUtils
import kotlinx.android.synthetic.main.fragment_news_detail.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by faruktoptas on 11/10/16.
 */
class NewsDetailFragment : BaseFragment() {

    private val viewModel: NewsDetailViewModel by viewModel()
    private val newsViewModel: NewsListViewModel by viewModel()
    private var rssItem: RssItem? = null
    private var mListener: OnNewsDetailListener? = null
    private var mIsNightMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rssItem = arguments?.getSerializable(EXTRA_RSS_ITEM) as RssItem?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mIsNightMode = requireActivity().isNightMode()
        btnShowSource.setOnClickListener {
            mListener?.goToSource(rssItem) // TODO: move vm
        }
        btnShare.setOnClickListener { requireContext().shareText(rssItem?.link) }
        btnFav.setOnClickListener { addToFavourites() }
        imageView.setOnClickListener {
            rssItem?.image?.let { PhotoActivity.start(requireActivity(), it) }
        }
        setUpContent()
        observe()
    }

    private fun observe() {
        viewModel.loadImageLive.observeTrue(viewLifecycleOwner, {
            imageView.loadUrlWithResult(rssItem!!.image) { loaded ->
                if (isAdded) {
                    progress_loading.hide()
                    if (!loaded) {
                        imageView.hide()
                    }
                }
            }
        })
        viewModel.hideImageLive.observeTrue(viewLifecycleOwner, {
            imageView.hide()
            rlImageContainer.hide()
            spaceContainer.hide()
        })
        viewModel.titleLive.observeNotNull(viewLifecycleOwner, { tv_title.text = it })
        viewModel.timeLive.observe(viewLifecycleOwner, Observer { textOrHide(tv_time, it) })
        viewModel.detailLive.observe(viewLifecycleOwner, Observer { textOrHide(tv_detail, it) })
    }


    private fun addToFavourites() {
        rssItem?.let {
            newsViewModel.favoriteClicked(it) { added ->
                if (added) {
                    ivFav.setImageResource(if (mIsNightMode) R.drawable.ic_favorite_white_24dp else R.drawable.ic_favorite_black_24dp)
                    UIUtils.showSaveSnackBar(activity, tv_title, true, true)
                } else {
                    ivFav.setImageResource(if (mIsNightMode) R.drawable.ic_favorite_border_white_24dp else R.drawable.ic_favorite_border_black_24dp)
                    UIUtils.showSaveSnackBar(activity, tv_title, false, true)
                }
                mListener?.onFavouriteChanged()
            }
        }

    }


    private fun setUpContent() {
        viewModel.setupContent(rssItem)
        if (!isAdded) return
        rssItem?.let {
            if (TemporaryUtil.isFavorite(it.link) > -1) {
                ivFav.setImageResource(if (mIsNightMode) R.drawable.ic_favorite_white_24dp else R.drawable.ic_favorite_black_24dp)
            } else {
                ivFav.setImageResource(if (mIsNightMode) R.drawable.ic_favorite_border_white_24dp else R.drawable.ic_favorite_border_black_24dp)
            }

            val fontHelper = NewsApplication.instance.fontSizeHelper
            fontHelper.detailTitle(tv_title)
            fontHelper.detailPubDate(tv_time)
            fontHelper.detailContent(tv_detail)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNewsDetailListener) {
            mListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnNewsDetailListener {
        fun goToSource(rssItem: RssItem?)
        fun onFavouriteChanged()
    }

    companion object {
        const val EXTRA_RSS_ITEM = "extra_rss_item"

        @JvmStatic
        fun newInstance(rssItem: RssItem?): NewsDetailFragment {
            val bundle = Bundle()
            bundle.putSerializable(EXTRA_RSS_ITEM, rssItem)
            val newsDetailFragment = NewsDetailFragment()
            newsDetailFragment.arguments = bundle
            return newsDetailFragment
        }
    }
}