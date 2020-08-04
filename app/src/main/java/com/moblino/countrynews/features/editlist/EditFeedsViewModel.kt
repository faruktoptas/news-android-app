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

package com.moblino.countrynews.features.editlist

import android.arch.lifecycle.MutableLiveData
import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseViewModel
import com.moblino.countrynews.data.SingleLiveEvent
import com.moblino.countrynews.data.LoggerRepository
import com.moblino.countrynews.data.PrefRepository
import com.moblino.countrynews.data.firebase.FirebaseManager
import com.moblino.countrynews.ext.moveItems
import com.moblino.countrynews.data.AppCache
import com.moblino.countrynews.ext.isTrue
import com.moblino.countrynews.models.FeedItem
import com.moblino.countrynews.ext.unite
import com.moblino.countrynews.utils.GAManager

class EditFeedsViewModel(private val pref: PrefRepository,
                         private val logger: LoggerRepository,
                         private val appCache: AppCache) : BaseViewModel() {

    private lateinit var feedList: MutableList<FeedItem>
    val excluded = arrayListOf<Int>()

    val feedListLive = MutableLiveData<MutableList<FeedItem>>()
    val finishAfterSave = SingleLiveEvent<Boolean>()

    fun getList() {
        feedList = appCache.currentFeedList
        feedListLive.postValue(feedList)

        val excludedString = pref.readCheckString()

        fun addExcludedIfPossible(idStr: String) {
            idStr.toIntOrNull()?.let { excluded.add(it) }
        }

        if (excludedString?.contains(",").isTrue()) {
            excludedString?.split(",")?.forEach {
                addExcludedIfPossible(it)
            }
        } else if (!excludedString.isNullOrEmpty()) {
            addExcludedIfPossible(excludedString)
        }

    }

    fun save() {
        // saving list order
        pref.writeListOrder(feedList.map { it.feedId }.unite())

        if (feedList.size == excluded.size) {
            toastLive.postValue(res.getString(R.string.error_choose_one))
            return
        }

        // saving excluded
        pref.writeCheckString(excluded.unite())

        finishAfterSave.postValue(true)
    }


    fun isChecked(feedId: Int): Boolean {
        return !excluded.contains(feedId)
    }

    fun checkedChanged(item: FeedItem?) {
        item?.apply {
            if (excluded.contains(feedId)) {
                excluded.remove(feedId)
            } else {
                excluded.add(feedId)
            }
        }
    }

    fun onItemMove(old: Int, new: Int) {
        feedList.moveItems(old, new)
        logger.sendFireBaseEvent(FirebaseManager.EVENT_SORT)
        logger.sendScreen(GAManager.ACTION_SORT)
    }

}