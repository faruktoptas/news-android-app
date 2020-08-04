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

import androidx.lifecycle.MutableLiveData
import com.moblino.countrynews.base.BaseViewModel
import com.moblino.countrynews.data.SingleLiveEvent
import com.moblino.countrynews.data.AppCache
import com.moblino.countrynews.data.LoggerRepository
import com.moblino.countrynews.data.PrefRepository
import com.moblino.countrynews.ext.postTrue
import com.moblino.countrynews.models.Category
import com.moblino.countrynews.models.FeedItem
import com.moblino.countrynews.utils.Constants
import com.moblino.countrynews.utils.MOUtils
import com.moblino.countrynews.utils.UpdateChecker
import java.util.ArrayList
import java.util.Locale

class MainViewModel(private val repo: MainRepository,
                    private val pref: PrefRepository,
                    private val appCache: AppCache,
                    private val updateChecker: UpdateChecker,
                    private val logger: LoggerRepository) : BaseViewModel() {

    private val currentlyViewedFeeds = ArrayList<FeedItem>()
    private val defaultLang = "US"
    private var selectedCountry = ""
    private var currentCategoryId = -1
    private val activeCategories = ArrayList<Category>()

    val navigationItemsLive = MutableLiveData<Pair<Int, List<Category>>>()
    val resetViewPagerLive = SingleLiveEvent<Boolean>()
    val viewPagerItemsLive = SingleLiveEvent<List<FeedItem>>()
    val titleLive = MutableLiveData<String>()
    val showShowCaseLive = SingleLiveEvent<Boolean>()
    val showWhatsNewLive = SingleLiveEvent<Boolean>()
    val goToTabLive = SingleLiveEvent<Int>()

    fun initialize() {
        pref.writeStartCount(pref.readStartCount() + 1)
        showWhatsNewDialog()
    }

    fun setup(categoryId: Int) {
        setupCurrentCountry()

        val categories = repo.categoriesByCountry(selectedCountry)

        // A workaround not to use lambda. Because code coverage fails if kotlin sort used
        val files = MOUtils.sortList(repo.getCountryFiles(selectedCountry).toList()).map { it as String }

        appCache.allFeeds.clear()
        activeCategories.clear()
        files.filter { it != Constants.CATEGORY_FILE }
                .forEach {
                    val id = Integer.parseInt(it.substring(0, it.indexOf(".")))
                    activeCategories.add(categories[id])

                    val feedList = repo.feedsByCategory(selectedCountry, id)
                    feedList.forEach { feed ->
                        feed.category = categories[id].title
                        feed.categoryId = id
                    }
                    appCache.allFeeds.addAll(feedList)
                }

        currentCategoryId = if (categoryId > -1) categoryId else pref.readCategoryId()
        navigationItemsLive.postValue(Pair(currentCategoryId, activeCategories))
        setupViewPager(currentCategoryId)
    }

    private fun setupViewPager(categoryId: Int) {
        currentCategoryId = categoryId
        repo.setCurrentCategory(categoryId)

        val excludedString = pref.readCheckString().orEmpty()
        val excludedItems = excludedString.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()


        val feedItems = repo.feedsByCategory(selectedCountry, categoryId)
        val listOrder = pref.getListOrder()
        var orders: Array<String>? = null
        if (listOrder.isNotEmpty()) {
            orders = listOrder.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }

        appCache.currentFeedList.clear()
        if (orders != null) {
            for (order in orders) {
                feedItems
                        .filter { Integer.parseInt(order) == it.feedId }
                        .forEach { appCache.currentFeedList.add(it) }
            }
            if (orders.size < feedItems.size && orders.isNotEmpty()) {
                for (i in feedItems.indices) {
                    val feedItem = feedItems[i]
                    if (!appCache.currentFeedList.contains(feedItem)) {
                        if (appCache.currentFeedList.size > i) {
                            appCache.currentFeedList.add(i, feedItem)
                        } else {
                            appCache.currentFeedList.add(feedItem)
                        }
                    }
                }
            }
        } else {
            appCache.currentFeedList.addAll(feedItems)
        }
        currentlyViewedFeeds.clear()
        appCache.currentFeedList.filter { !excludedItems.contains(it.feedId.toString()) }
                .apply {
                    currentlyViewedFeeds.addAll(this)
                    resetViewPagerLive.postValue(true)
                    viewPagerItemsLive.postValue(this)
                    refreshTitle()
                }
    }

    private fun refreshTitle() {
        titleLive.postValue(activeCategories.firstOrNull { it.id == currentCategoryId }?.title)
    }

    private fun showWhatsNewDialog() {
        val show = updateChecker.isUpdated(pref.readAppVersion()) { pref.writeNewVersion() }
        if (show) {
            showWhatsNewLive.postTrue()
        } else if (pref.readStartCount() > 3) {
            showShowCaseLive.postTrue()
        }
    }

    private fun setupCurrentCountry() {
        selectedCountry = pref.readCountry()
        if (selectedCountry == "") {
            selectedCountry = Locale.getDefault().country
            pref.writeCountry(selectedCountry)
        }

        val countries = repo.countryList()
        if (!countries.contains(selectedCountry)) {
            selectedCountry = defaultLang
            pref.writeCountry(selectedCountry)
        }
    }

    fun refresh(currentTabIndex: Int) {
        resetViewPagerLive.postTrue()
        setup(currentCategoryId)
        goToTabLive.postValue(currentTabIndex)
    }

    fun changeCategory(categoryId: Int) {
        resetViewPagerLive.postTrue()
        setup(categoryId)
        pref.writeCategoryId(categoryId)
    }

    fun selectSearchedItem(feedItem: FeedItem) {
        var index = -1
        if (feedItem.categoryId != currentCategoryId) {
            changeCategory(feedItem.categoryId)
        }
        currentlyViewedFeeds.forEachIndexed { i, feed ->
            if (feedItem.title == feed.title) {
                index = i
            }
        }
        if (index > -1) {
            goToTabLive.postValue(index)
        }
    }

}