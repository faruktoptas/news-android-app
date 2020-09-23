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

/**
 * Created by ftoptas on 22/01/18.
 * Main Activity
 */

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseMvvmActivity
import com.moblino.countrynews.customviews.CardQuestionManager
import com.moblino.countrynews.data.firebase.FirebaseManager
import com.moblino.countrynews.data.firebase.RemoteConfigWrapper
import com.moblino.countrynews.ext.goToPlayStore
import com.moblino.countrynews.ext.observeNotNull
import com.moblino.countrynews.ext.observeTrue
import com.moblino.countrynews.features.editlist.EditFeedsActivity
import com.moblino.countrynews.features.main.chrome.ChromeTabObservable
import com.moblino.countrynews.features.saved.SavedNewsActivity
import com.moblino.countrynews.features.search.SearchActivity
import com.moblino.countrynews.features.settings.SettingsActivity
import com.moblino.countrynews.features.webview.HeadingsActivity
import com.moblino.countrynews.model.Category
import com.moblino.countrynews.model.FeedItem
import com.moblino.countynews.common.model.RssItem
import com.moblino.countrynews.util.Constants
import com.moblino.countrynews.util.Constants.Companion.EXTRA_CURRENT_CATEGORY
import com.moblino.countrynews.util.Constants.Companion.EXTRA_SEARCH_ITEM
import com.moblino.countrynews.util.Constants.Companion.NAV_ITEM_EDIT
import com.moblino.countrynews.util.Constants.Companion.NAV_ITEM_FAVOURITES
import com.moblino.countrynews.util.Constants.Companion.NAV_ITEM_FIRST_PAGES
import com.moblino.countrynews.util.Constants.Companion.NAV_ITEM_RATE
import com.moblino.countrynews.util.Constants.Companion.NAV_ITEM_SETTINGS
import com.moblino.countrynews.util.Constants.Companion.NAV_ITEM_SHARE
import com.moblino.countrynews.util.NewsShortcuts
import com.moblino.countrynews.util.UIUtils
import kotlinx.android.synthetic.main.activity_main.*
import me.toptas.fancyshowcase.FancyShowCaseView
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseMvvmActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        MainActivityFragment.OnNewsClickListener {

    private val viewModel: MainViewModel by viewModel()
    private val chromeObservable = ChromeTabObservable(this)

    private lateinit var adapterViewPager: MainViewPagerAdapterLegacy
    private var menuItemListType: MenuItem? = null

    override fun layoutRes() = R.layout.activity_main

    override fun initViews(savedInstanceState: Bundle?) {
        bindViewModel(viewModel)
        viewModel.initialize()

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        tablayout.tabMode = TabLayout.MODE_SCROLLABLE
        adapterViewPager = MainViewPagerAdapterLegacy(supportFragmentManager)
        main_viewpager.adapter = adapterViewPager
        setSupportActionBar(toolbar)

        val shortCutTab = intent.getIntExtra(EXTRA_CURRENT_CATEGORY, -1)
        RemoteConfigWrapper.getInstance().init(this)


        viewModel.setup(shortCutTab)

        lifecycle.addObserver(chromeObservable)

        viewModel.navigationItemsLive.observeNotNull(this) {
            setupNavigationDrawer(it.second, it.first)
        }

        viewModel.resetViewPagerLive.observeTrue(this) { resetViewPager() }

        viewModel.viewPagerItemsLive.observeNotNull(this) { setupViewPager(it) }

        viewModel.titleLive.observeNotNull(this) { title = it }

        viewModel.showWhatsNewLive.observeTrue(this) { showWhatsNew() }

        viewModel.showShowCaseLive.observeTrue(this) { showShowCase() }

        viewModel.goToTabLive.observeNotNull(this) { goToTab(it) }

    }

    //region View implementations
    /**
     * Navigation drawer items
     */
    private fun setupNavigationDrawer(categories: List<Category>, current: Int) {
        val menu = nav_view.menu
        menu.clear()
        if (getPreferenceWrapper().readCountry() == "TR" && !getPreferenceWrapper().readHeadingUrl().isNullOrEmpty()) {
            val itemHeading = menu.add(3, NAV_ITEM_FIRST_PAGES, 0, "Manşetler")
            UIUtils.setNavMenuIcon(itemHeading, NAV_ITEM_FIRST_PAGES)
        }
        for (category in categories) {
            val menuItem = menu.add(0, category.id, 0, category.title)
            UIUtils.setNavMenuIcon(menuItem, category.id)
            menuItem.isCheckable = true
            if (category.id == current) {
                menuItem.isChecked = true
            }
        }

        val itemEdit = menu.add(1, NAV_ITEM_EDIT, 0, R.string.title_edit_list)
        UIUtils.setNavMenuIcon(itemEdit, NAV_ITEM_EDIT)
        val itemFavourites = menu.add(1, NAV_ITEM_FAVOURITES, 0, R.string.title_favourites)
        UIUtils.setNavMenuIcon(itemFavourites, NAV_ITEM_FAVOURITES)

        val itemSettings = menu.add(2, NAV_ITEM_SETTINGS, 0, R.string.title_settings)
        UIUtils.setNavMenuIcon(itemSettings, NAV_ITEM_SETTINGS)
        val itemAbout = menu.add(2, NAV_ITEM_RATE, 0, R.string.title_rate)
        UIUtils.setNavMenuIcon(itemAbout, NAV_ITEM_RATE)
        val itemShare = menu.add(2, NAV_ITEM_SHARE, 0, R.string.title_share_app)
        UIUtils.setNavMenuIcon(itemShare, NAV_ITEM_SHARE)

        NewsShortcuts.setShortcuts(this, categories)
    }

    /**
     * ViewPager items
     */
    private fun setupViewPager(feeds: List<FeedItem>) {
        feeds.forEachIndexed { index, feedItemModel ->
            val cardQuestion = if (index == 0) CardQuestionManager.getInstance().nextCard(this) else null
            adapterViewPager.addFrag(
                    MainActivityFragment.newInstance(feedItemModel, cardQuestion), feedItemModel.title)
        }

        main_viewpager.adapter = adapterViewPager
        tablayout.setupWithViewPager(main_viewpager)
    }

    private fun resetViewPager() {
        adapterViewPager.clearFragments()
        try {
            supportFragmentManager.fragments.clear()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun goToTab(tabIndex: Int) {
        main_viewpager.currentItem = tabIndex
    }

    private fun showWhatsNew() {
        /*val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.title_whatsnew)
                .setPositiveButton(R.string.text_ok, null)
                .setMessage(R.string.text_whats_new)
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(this, R.color.colorAccent))*/

    }

    private fun showShowCase() {
        main_viewpager.post {
            if (!isFinishing) {
                toolbar?.findViewById<View>(R.id.item_layout_type)?.let {
                    FancyShowCaseView.Builder(this@MainActivity)
                            .showOnce("item_layout_type")
                            .fitSystemWindows(true)
                            .enableAutoTextPosition()
                            .focusOn(it)
                            .title(getString(R.string.text_show_case_item_type))
                            .delay(1000)
                            .build()
                            .show()
                }
            }
        }
    }

    //endregion

    //region Activity methods

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menuItemListType = menu.getItem(1)
        setMenuItemIcon()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> drawer_layout.openDrawer(GravityCompat.START)
            R.id.item_search -> {
                showSearchActivity()
            }
            R.id.item_layout_type -> {
                getPreferenceWrapper().writeStaggeredLayout(!getPreferenceWrapper().readStaggered())
                setMenuItemIcon()
                viewModel.refresh(main_viewpager.currentItem)

                FirebaseManager.getInstance().setUserPropertyListingType(!getPreferenceWrapper().readStaggered())
            }
        }
        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (val id = item.itemId) {
            NAV_ITEM_FIRST_PAGES -> {
                startActivity(Intent(this, HeadingsActivity::class.java))
                FirebaseManager.getInstance().logHeadlines()
            }
            NAV_ITEM_EDIT -> {
                val intent = Intent(this, EditFeedsActivity::class.java)
                startActivityForResult(intent, Constants.REQ_CODE_LIST_CHANGED)
            }
            NAV_ITEM_SETTINGS -> startActivityForResult(Intent(this,
                    SettingsActivity::class.java), Constants.REQ_CODE_SETTINGS_CHANGED)

            NAV_ITEM_SHARE -> {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + " - " + Constants.PLAY_STORE_URL)
                sendIntent.type = "text/plain"
                startActivity(Intent.createChooser(sendIntent, getString(R.string.title_share_app)))
                FirebaseManager.getInstance().logShare()
            }
            NAV_ITEM_RATE -> {
                goToPlayStore()
                FirebaseManager.getInstance().logRate()
            }
            NAV_ITEM_FAVOURITES -> startActivityForResult(Intent(this,
                    SavedNewsActivity::class.java), Constants.REQ_CODE_LIST_CHANGED)
            else -> {
                viewModel.changeCategory(id)
                FirebaseManager.getInstance().logCategoryChange(item.title.toString())
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Constants.REQ_CODE_SETTINGS_CHANGED -> {
                val refresh = data?.getBooleanExtra(SettingsActivity.EXTRA_SETTINGS_CHANGED, false)
                if (refresh == true) {
                    viewModel.changeCategory(0)
                }
            }
            Constants.REQ_CODE_LIST_CHANGED -> if (resultCode == Activity.RESULT_OK) {
                viewModel.refresh(main_viewpager.currentItem)
            }
            Constants.REQ_CODE_SEARCH -> if (resultCode == Activity.RESULT_OK) {
                val feedItemModel = data?.getSerializableExtra(EXTRA_SEARCH_ITEM) as FeedItem
                viewModel.selectSearchedItem(feedItemModel)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (FancyShowCaseView.isVisible(this)) {
                FancyShowCaseView.hideCurrent(this)
            }
            super.onBackPressed()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val categoryId = intent.getIntExtra(EXTRA_CURRENT_CATEGORY, -1)
        if (categoryId > -1) {
            viewModel.changeCategory(categoryId)
        }
    }

    //endregion

    //region Other

    override fun onNewsClicked(model: RssItem, rssUrl: String, position: Int) {
        val cardQuestion = (adapterViewPager.getItem(main_viewpager.currentItem) as MainActivityFragment).cardQuestion
        chromeObservable.openNewsDetail(model, rssUrl, position, cardQuestion)
    }

    private fun setMenuItemIcon() {
        if (getPreferenceWrapper().readStaggered()) {
            menuItemListType?.setIcon(R.drawable.ic_list_lienar)
        } else {
            menuItemListType?.setIcon(R.drawable.ic_list_staggered)
        }
    }

    private fun showSearchActivity() {
        val searchView = toolbar.findViewById<ActionMenuItemView>(R.id.item_search)
        val point = IntArray(2)
        searchView.getLocationOnScreen(point)
        point[0] = point[0] + searchView.width / 2

        val intent = Intent(this, SearchActivity::class.java)
        if (point.size > 1) {
            intent.putExtra(SearchActivity.KEY_POINT, point)
        }
        startActivityForResult(intent, Constants.REQ_CODE_SEARCH)
        overridePendingTransition(0, 0)
    }
    //endregion

}