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
import com.moblino.countrynews.data.AppCache
import com.moblino.countrynews.data.LoggerRepository
import com.moblino.countrynews.data.repository.RssRepository
import com.moblino.countrynews.data.firebase.FirebaseManager
import com.moblino.countrynews.ext.isTrue
import com.moblino.countrynews.ext.postFalse
import com.moblino.countrynews.ext.postTrue
import com.moblino.countrynews.features.saved.SavedNewsRepository
import com.moblino.countynews.common.model.FeedItem
import com.moblino.countynews.common.model.RssItem
import com.moblino.countynews.common.model.RssRequest
import com.moblino.countynews.common.model.RssResponse
import kotlinx.coroutines.launch


class NewsListViewModel(private val cache: AppCache,
                        private val rssRepo: RssRepository,
                        private val savedRepo: SavedNewsRepository,
                        private val loggerRepo: LoggerRepository) : BaseViewModel() {

    var title: String? = ""
    var rssUrl: String = ""
    var encoding: String? = ""
    var feedItem: FeedItem? = null

    val showEmptyLive = MutableLiveData<Boolean>()
    val showRefresherLive = MutableLiveData<Boolean>()
    val refresherIsRefreshing = MutableLiveData<Boolean>()
    val showProgressLive = MutableLiveData<Boolean>()
    val itemsLive = MutableLiveData<List<RssItem>>()

    fun fetchRss(readFromCache: Boolean = true, showProgress: Boolean = true) {
        val hasCachedResponse = cache.responseList.containsKey(rssUrl)
        if (!hasCachedResponse || !readFromCache) {
            showEmptyLive.postValue(false)
            showProgressLive.postValue(showProgress)
            uiScope.launch {
                when (val response = rssRepo.fetchRss(RssRequest(rssUrl, encoding))) {
                    is RssResponse.Success -> {
                        val newsList = response.items
                        itemsLive.postValue(newsList)
                        cache.responseList[rssUrl] = response
                    }
                    is RssResponse.Fail -> {
                        // TODO: Different messages for different errors
                        showEmptyLive.postTrue()
                    }
                }
                refresherIsRefreshing.postFalse()
                showProgressLive.postFalse()
            }
        } else {
            val cached = cache.responseList[rssUrl]
            if (cached?.items?.isNotEmpty().isTrue()) {
                itemsLive.postValue(cached?.items)
            } else {
                showEmptyLive.postTrue()
            }
        }
    }

    fun favoriteClicked(rssItem: RssItem, cb: (Boolean) -> Unit) {
        val rssLink = rssItem.link
        val favoritePosition = cache.favoriteList.indexOfFirst { it.link == rssLink }
        if (favoritePosition > -1) {
            cache.favoriteList.removeAt(favoritePosition)
            val list = savedRepo.getSavedByUrl(rssLink)
            if (list.isNotEmpty()) {
                savedRepo.removeFromDb(rssLink)
            }
            cb(false)
        } else {
            rssItem.feedTitle = title
            cache.favoriteList.add(rssItem)
            savedRepo.add(RssItem(rssItem.title,
                    rssItem.link,
                    rssItem.image,
                    rssItem.pubDate,
                    feedItem?.feedId ?: -1,
                    title,
                    rssItem.description))

            loggerRepo.logEvent(FirebaseManager.EVENT_FAVOURITE)
            cb(true)
        }
    }

}