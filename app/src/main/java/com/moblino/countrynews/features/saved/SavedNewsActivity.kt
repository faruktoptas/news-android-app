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

package com.moblino.countrynews.features.saved

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.view.View
import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseMvvmActivity
import com.moblino.countrynews.features.main.chrome.ChromeTabObservable
import com.moblino.countrynews.ext.observeNotNull
import com.moblino.countrynews.ext.observeTrue
import com.moblino.countrynews.ext.toWrapperList
import com.moblino.countrynews.features.main.NewsListAdapter
import com.moblino.countrynews.features.main.OnNewsItemClickListener
import com.moblino.countrynews.features.settings.SettingsActivity
import com.moblino.countrynews.model.RssItem
import com.moblino.countrynews.ext.show
import com.moblino.countrynews.util.Constants
import com.moblino.countrynews.util.PreferenceWrapper
import com.moblino.countrynews.util.UIUtils
import kotlinx.android.synthetic.main.activity_saved.recyclerView
import kotlinx.android.synthetic.main.activity_saved.rl_no_items
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class SavedNewsActivity : BaseMvvmActivity(), OnNewsItemClickListener {

    private lateinit var adapter: NewsListAdapter
    private val viewModel: SavedNewsViewModel by viewModel()
    private val prefWrapper: PreferenceWrapper by inject()
    private val chromeObservable = ChromeTabObservable(this)

    override fun layoutRes() = R.layout.activity_saved

    override fun initViews(savedInstanceState: Bundle?) {
        bindViewModel(viewModel)

        setupToolbar()
        observe()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getItems()
    }

    private fun observe() {
        lifecycle.addObserver(chromeObservable)
        viewModel.itemsLive.observeNotNull(this, { rssItems ->
            adapter = NewsListAdapter(this, prefWrapper)
            adapter.items = rssItems.toWrapperList(this)
            adapter.onItemClickListener = this
            recyclerView.adapter = adapter
        })

        viewModel.isStaggeredLive.observeNotNull(this) {
            if (it) {
                recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            } else {
                recyclerView.layoutManager = LinearLayoutManager(this)
            }
        }

        viewModel.emptyListLive.observeTrue(this) { rl_no_items.show(it) }

        viewModel.titleLive.observeNotNull(this) { title = it }

        viewModel.notifyItemRemoved.observeNotNull(this) {
            Handler().postDelayed({ adapter.notifyItemRemoved(it) }, 100)
        }

        viewModel.showSnackBar.observeTrue(this) {
            UIUtils.showSaveSnackBar(this, recyclerView, false, false)
            val intent = Intent()
            intent.putExtra(SettingsActivity.EXTRA_SETTINGS_CHANGED, true)
            setResult(Activity.RESULT_OK, intent)
        }
    }

    override fun onItemSelected(model: RssItem, position: Int) {
        chromeObservable.openNewsDetail(model, null, position, null)
    }

    override fun onFavouriteClicked(view: View, rssItem: RssItem, position: Int, isWhite: Boolean) {
        viewModel.removeItem(rssItem)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQ_CODE_LIST_CHANGED) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.refresh()
            }
        }
    }

}