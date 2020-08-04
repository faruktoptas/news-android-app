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

import androidx.lifecycle.MutableLiveData
import com.moblino.countrynews.base.BaseViewModel
import com.moblino.countrynews.data.LoggerRepository
import com.moblino.countrynews.data.PrefRepository
import com.moblino.countrynews.data.firebase.FirebaseManager
import com.moblino.countrynews.models.CardQuestion
import com.moblino.countrynews.models.RssItem

class DetailViewModel(private val repo: DetailRepository, logger: LoggerRepository, pref: PrefRepository) : BaseViewModel() {

    private var card: CardQuestion? = null
    var position = 0
    var favoriteListChanged = false

    val feedListLive = MutableLiveData<List<RssItem>>()
    val showFancy = MutableLiveData<Boolean>()

    init {
        logger.logEvent(FirebaseManager.EVENT_NEWS_DETAIL)

        if (!pref.readDetailShowCase()) {
            showFancy.postValue(true)
        }
    }

    fun getFeedList(rssUrl: String?, initialPosition: Int, cardQuestion: CardQuestion?) {
        position = initialPosition
        card = cardQuestion
        val feedList = if (rssUrl != null) {
            repo.getRssItemList(rssUrl)
        } else {
            repo.getFavorites()
        }

        if (cardQuestion != null) {
            position -= 1
        }
        feedListLive.postValue(feedList)
    }

    fun finish() {
        finishLive.postValue(favoriteListChanged)
    }

}