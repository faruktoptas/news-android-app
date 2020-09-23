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
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.moblino.countrynews.NewsApplication.Companion.instance
import com.moblino.countrynews.R
import com.moblino.countrynews.customviews.CardQuestionManager
import com.moblino.countrynews.data.localdb.FavouritePersistenceManager
import com.moblino.countrynews.ext.asVisibility
import com.moblino.countrynews.ext.observeNotNull
import com.moblino.countrynews.ext.toWrapperList
import com.moblino.countrynews.model.CardQuestion
import com.moblino.countrynews.model.FeedItem
import com.moblino.countynews.common.model.RssItem
import com.moblino.countrynews.model.RssItemWrapper
import com.moblino.countrynews.ext.show
import com.moblino.countrynews.util.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.layout_empty_state.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.collections.ArrayList


class MainActivityFragment : Fragment(), OnNewsItemClickListener, OnRefreshListener {

    private val viewModel: NewsListViewModel by viewModel()
    private val pref: PreferenceWrapper by inject()

    private var rssUrl: String? = null
    private var itemList: ArrayList<RssItem>? = null
    private lateinit var rvAdapter: NewsListAdapter
    private var mOnNewsClickListener: OnNewsClickListener? = null
    var cardQuestion: CardQuestion? = null
    private var mFavouritePersistenceManager: FavouritePersistenceManager? = null
    private var mFeedItem: FeedItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mFeedItem = arguments?.getSerializable(EX_MODEL) as FeedItem?
        cardQuestion = arguments?.getSerializable(EX_QUESTION) as CardQuestion?
        viewModel.feedItem = mFeedItem
        if (mFeedItem != null) {
            rssUrl = mFeedItem!!.feedUrl
            viewModel.rssUrl = mFeedItem!!.feedUrl
            viewModel.encoding = mFeedItem!!.encoding
            viewModel.title = mFeedItem!!.title
        } else {
            showToast(R.string.error_json_webview_url)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swRefresh.setOnRefreshListener(this)
        swRefresh.setColorSchemeResources(R.color.colorAccent)
        mFavouritePersistenceManager = instance.favouritePersistenceManager
        btn_retry.setOnClickListener { viewModel.fetchRss(false) }
        setRecycleView()
        viewModel.fetchRss()

        viewModel.itemsLive.observeNotNull(viewLifecycleOwner, { items ->
            val arrayList = ArrayList<RssItemWrapper>()
            arrayList.addAll(items.toWrapperList(requireContext()))
            if (cardQuestion != null && arrayList.isNotEmpty()) arrayList.add(0, RssItemWrapper(card = cardQuestion))
            rvAdapter.items = arrayList
            updateList()
        })

        viewModel.showProgressLive.asVisibility(viewLifecycleOwner, pbLoading)
        viewModel.showRefresherLive.asVisibility(viewLifecycleOwner, swRefresh)
        viewModel.showEmptyLive.asVisibility(viewLifecycleOwner, rl_empty_state)
        viewModel.showEmptyLive.observeNotNull(viewLifecycleOwner, { swRefresh.show(!it) })
        viewModel.refresherIsRefreshing.observeNotNull(viewLifecycleOwner, { swRefresh.isRefreshing = it })
    }

    private fun setRecycleView() {
        itemList = ArrayList()
        if (pref.readStaggered()) {
            rvRssItems.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        } else {
            rvRssItems.layoutManager = LinearLayoutManager(activity)
        }
        rvAdapter = NewsListAdapter(requireContext(), pref).apply {
            onItemClickListener = object : OnNewsItemClickListener {
                override fun onItemSelected(item: RssItem, position: Int) {
                    mOnNewsClickListener?.onNewsClicked(item, rssUrl!!, position)
                }

                override fun onFavouriteClicked(view: View, rssItem: RssItem, position: Int, isWhite: Boolean) {
                    this@MainActivityFragment.onFavouriteClicked(view, rssItem, position, isWhite)
                }
            }
            cardRowActionListener = object : CardRowActionListener {
                override fun onActionItemPositive(card: CardQuestion) {
                    CardQuestionManager.getInstance().executeAction(activity, cardQuestion)
                    card.hideForever()
                    cardQuestion = null
                    rvAdapter.removeFirstItem()
                }

                override fun onActionItemNegative(card: CardQuestion) {
                    card.hideForever()
                    cardQuestion = null
                    rvAdapter.removeFirstItem()
                }
            }
        }
        rvRssItems.adapter = rvAdapter
    }

    private fun updateList() {
        rvAdapter.notifyDataSetChanged()
        /*if (itemList!!.size == 0) { // TODO: Move vm
            rl_empty_state.show()
            swRefresh.hide()
        } else {
            rl_empty_state.hide()
            swRefresh.show()
        }*/
    }

    override fun onItemSelected(model: RssItem, position: Int) {
        mOnNewsClickListener?.onNewsClicked(model, rssUrl!!, position)
    }

    override fun onFavouriteClicked(view: View, rssItem: RssItem, positon: Int, isWhite: Boolean) {
        val imageView = view.findViewById<View>(R.id.ivFav) as ImageView
        viewModel.favoriteClicked(rssItem) { added ->
            if (added) {
                imageView.setImageResource(if (isWhite) R.drawable.ic_favorite_black_24dp else R.drawable.ic_favorite_white_24dp)
                UIUtils.showSaveSnackBar(activity, rvRssItems, true, true)
            } else {
                imageView.setImageResource(if (isWhite) R.drawable.ic_favorite_border_black_24dp else R.drawable.ic_favorite_border_white_24dp)
                UIUtils.showSaveSnackBar(activity, rvRssItems, false, true)
            }
        }
    }

    override fun onRefresh() {
        viewModel.fetchRss(readFromCache = false, showProgress = false)
    }

    private fun showToast(toastMsgId: Int) {
        Toast.makeText(activity, toastMsgId, Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNewsClickListener) {
            mOnNewsClickListener = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        mOnNewsClickListener = null
    }

    interface OnNewsClickListener {
        fun onNewsClicked(model: RssItem, rssUrl: String, position: Int)
    }

    companion object {
        private const val TAG = "MainActivityFragment"
        private const val EX_MODEL = "EXTRA_MODEL"
        private const val EX_QUESTION = "EXTRA_QUESTION"
        fun newInstance(feedItem: FeedItem?, cardQuestion: CardQuestion?): MainActivityFragment {
            val mainActivityFragment = MainActivityFragment()
            val bundle = Bundle()
            bundle.putSerializable(EX_MODEL, feedItem)
            bundle.putSerializable(EX_QUESTION, cardQuestion)
            mainActivityFragment.arguments = bundle
            return mainActivityFragment
        }
    }
}