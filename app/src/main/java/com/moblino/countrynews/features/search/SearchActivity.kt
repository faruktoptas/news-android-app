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

package com.moblino.countrynews.features.search

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseMvvmActivity
import com.moblino.countrynews.ext.observeNotNull
import com.moblino.countrynews.models.FeedItem
import com.moblino.countrynews.utils.CNUtils
import com.moblino.countrynews.utils.Constants
import com.moblino.countrynews.utils.PreferenceWrapper
import kotlinx.android.synthetic.main.activity_search.lvSearch
import kotlinx.android.synthetic.main.activity_search.rlContainer
import kotlinx.android.synthetic.main.activity_search.searchView
import org.koin.android.viewmodel.ext.android.viewModel


/**
 * Created by faruktoptas on 21/08/16.
 */
class SearchActivity : BaseMvvmActivity() {

    private var mKeyboardOpened = false
    private var mPoint = IntArray(2)

    private val viewModel: SearchViewModel by viewModel()

    override fun layoutRes() = R.layout.activity_search

    override fun initViews(savedInstanceState: Bundle?) {
        bindViewModel(viewModel)
        val adapter = SearchAdapter()
        lvSearch.adapter = adapter
        adapter.itemClickListener = { feedItem -> finishMe(feedItem) }

        viewModel.doSearch()
        viewModel.filteredItemsLive.observeNotNull(this) {
            adapter.items = it
        }

        rlContainer.setOnClickListener { dismiss() }

        rlContainer.setKeyboardListener { visible ->
            if (visible) {
                mKeyboardOpened = true
            } else {
                if (mKeyboardOpened) dismiss()
            }
        }

        searchView.onActionViewExpanded()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.doSearch(newText)
                return false
            }
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mPoint = intent.getIntArrayExtra(KEY_POINT)
            doEnterAnim()
        }

    }

    private fun dismiss() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            doExitAnim()
        } else {
            ActivityCompat.finishAfterTransition(this)
        }
    }

    private fun finishMe(feedItem: FeedItem?) {
        if (feedItem != null) {
            val checkString = PreferenceWrapper.getInstance().readCheckString(feedItem.categoryId)
            var excluded = arrayOf<String>()
            if (checkString != null) {
                if (checkString.isNotEmpty()) {
                    excluded = if (checkString.contains(",")) {
                        checkString.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    } else {
                        arrayOf(checkString)
                    }
                }
            }

            if (excluded.isNotEmpty() && CNUtils.arrayContains(excluded, "" + feedItem.feedId!!)) {
                Toast.makeText(this@SearchActivity, R.string.err_search_not_valid, Toast.LENGTH_SHORT).show()
            } else {
                val data = Intent()
                data.putExtra(Constants.EXTRA_SEARCH_ITEM, feedItem)
                setResult(Activity.RESULT_OK, data)
            }

        }
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        return true
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)

    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            viewModel.doSearch(query)
        }
    }

    override fun onBackPressed() {
        dismiss()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun doEnterAnim() {
        // Fade in a background scrim as this is a floating window. We could have used a
        // translucent window background but this approach allows us to turn off window animation &
        // overlap the fade with the reveal animation – making it feel snappier.

        rlContainer.alpha = 0f
        rlContainer.animate()
                .alpha(1f)
                .setDuration(300L)
                .start()

        // Next perform the circular reveal on the search panel
        //        View view = findViewById(android.R.id.action_bar);
        if (searchView != null) {
            if (mPoint!!.size < 2) {
                mPoint[0] = searchView.right
                mPoint[1] = searchView.top

            }
            // We use a view tree observer to set this up once the view is measured & laid out
            searchView.viewTreeObserver.addOnPreDrawListener(
                    object : ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            searchView.viewTreeObserver.removeOnPreDrawListener(this)
                            // As the height will change once the initial suggestions are delivered by the
                            // loader, we can't use the search panels height to calculate the final radius
                            // so we fall back to it's parent to be safe
                            //                            final ViewGroup searchPanelParent = (ViewGroup) mSearchView.getParent();
                            val revealRadius = Math.hypot(
                                    mPoint!![0].toDouble(), searchView.height.toDouble()).toInt()
                            // Center the animation on the top right of the panel i.e. near to the
                            // search button which launched this screen.
                            val show = ViewAnimationUtils.createCircularReveal(searchView,
                                    mPoint!![0], mPoint!![1], 0f, revealRadius.toFloat())
                            show.duration = 500L
                            show.interpolator = AnimationUtils.loadInterpolator(this@SearchActivity,
                                    android.R.interpolator.fast_out_slow_in)
                            show.start()
                            return false
                        }
                    })
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun doExitAnim() {
        val searchPanel = searchView
        // Center the animation on the top right of the panel i.e. near to the search button which
        // launched this screen. The starting radius therefore is the diagonal distance from the top
        // right to the bottom left
        val revealRadius = Math.hypot(searchPanel!!.width.toDouble(), searchPanel.height.toDouble()).toInt()
        // Animating the radius to 0 produces the contracting effect
        val shrink = ViewAnimationUtils.createCircularReveal(searchPanel,
                mPoint!![0], mPoint!![1], revealRadius.toFloat(), 0f)
        shrink.duration = 200L
        shrink.interpolator = AnimationUtils.loadInterpolator(this@SearchActivity,
                android.R.interpolator.fast_out_slow_in)
        shrink.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                searchPanel.visibility = View.INVISIBLE
                ActivityCompat.finishAfterTransition(this@SearchActivity)
                overridePendingTransition(0, 0)
            }
        })
        shrink.start()

        // We also animate out the translucent background at the same time.
        rlContainer.animate()
                .alpha(0f)
                .setDuration(200L)
                .start()
    }

    companion object {

        const val KEY_POINT = "POINT"
    }
}
