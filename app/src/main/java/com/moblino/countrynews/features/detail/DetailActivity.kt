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

import android.app.Activity
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import android.view.View
import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseMvvmActivity
import com.moblino.countrynews.features.main.chrome.ChromeTabObservable
import com.moblino.countrynews.ext.observeNotNull
import com.moblino.countrynews.ext.observeTrue
import com.moblino.countrynews.model.CardQuestion
import com.moblino.countrynews.model.RssItem
import com.moblino.countrynews.ext.show
import com.moblino.countrynews.util.AnimUtils
import com.moblino.countrynews.util.Constants
import kotlinx.android.synthetic.main.activity_news_detail.rlBlackOverlay
import kotlinx.android.synthetic.main.layout_bottom_sheet.detailViewPager
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.listener.OnViewInflateListener
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by faruktoptas on 11/10/16.
 */

class DetailActivity : BaseMvvmActivity(), NewsDetailFragment.OnNewsDetailListener {

    private val viewModel: DetailViewModel by viewModel()
    private lateinit var behaviour: BottomSheetBehavior<View>
    private val chromeObservable = ChromeTabObservable(this)

    override fun layoutRes() = R.layout.activity_news_detail

    override fun initViews(savedInstanceState: Bundle?) {

        val cardQuestion = intent.getSerializableExtra(EXTRA_CARD_QUESTION) as CardQuestion?

        viewModel.getFeedList(intent.getStringExtra(EXTRA_RSS_URL),
                intent.getIntExtra(EXTRA_POSITION, 0),
                cardQuestion)

        lifecycle.addObserver(chromeObservable)
        viewModel.feedListLive.observeNotNull(this) {
            val adapter = NewsDetailViewPagerAdapter(supportFragmentManager, it, cardQuestion)
            detailViewPager.adapter = adapter
            detailViewPager.currentItem = viewModel.position
        }

        viewModel.finishLive.observeNotNull(this) {
            behaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        viewModel.showFancy.observeTrue(this) { showSwipeFancy() }

        setupBottomSheet()
    }

    private fun setupBottomSheet() {
        behaviour = BottomSheetBehavior.from(detailViewPager)
        behaviour.peekHeight = 0
        behaviour.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(view: View, i: Int) {
                if (i == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (viewModel.favoriteListChanged) {
                        setResult(Activity.RESULT_OK)
                    }
                    finish()
                    overridePendingTransition(0, 0)
                } else if (i == BottomSheetBehavior.STATE_EXPANDED) {
                    rlBlackOverlay.show()
                }
            }

            override fun onSlide(view: View, v: Float) {
                rlBlackOverlay.alpha = v
            }
        })
        detailViewPager.post { behaviour.state = BottomSheetBehavior.STATE_EXPANDED }
    }

    override fun goToSource(rssItem: RssItem?) {
        chromeObservable.startChromeTabs(rssItem!!)
    }

    override fun onFavouriteChanged() {
        viewModel.favoriteListChanged = true
    }

    override fun onBackPressed() {
        if (FancyShowCaseView.isVisible(this)) {
            FancyShowCaseView.hideCurrent(this)
        } else {
            viewModel.finish()
        }
    }

    private fun showSwipeFancy() {
        FancyShowCaseView.Builder(this@DetailActivity)
                .title(getString(R.string.text_swipe_1))
                .showOnce(Constants.FANCY_DETAIL_SWIPE)
                .closeOnTouch(false)
                .delay(500)
                .customView(R.layout.layout_detail_swipe_show_case, object : OnViewInflateListener {
                    override fun onViewInflated(view: View) {
                        AnimUtils.shake(view.findViewById(R.id.ivSwipe), isFinishing)
                    }
                })
                .build()
                .show()
    }

    companion object {
        const val EXTRA_RSS_URL = "extra_rss_url";
        const val EXTRA_POSITION = "extra_position";
        const val EXTRA_CARD_QUESTION = "extra_card_question";
        const val EXTRA_RSS_ITEM_MODEL = "extra_rss_item_model";
    }
}
