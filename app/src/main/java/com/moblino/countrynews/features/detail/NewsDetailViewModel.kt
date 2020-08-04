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
import com.moblino.countrynews.data.SingleLiveEvent
import com.moblino.countrynews.data.ConfigRepository
import com.moblino.countrynews.ext.postTrue
import com.moblino.countrynews.models.RssItem
import com.moblino.countrynews.utils.Utils

class NewsDetailViewModel(private val configRepo: ConfigRepository) : BaseViewModel() {

    val loadImageLive = SingleLiveEvent<Boolean>()
    val hideImageLive = MutableLiveData<Boolean>()
    val titleLive = MutableLiveData<String>()
    val timeLive = MutableLiveData<String?>()
    val detailLive = MutableLiveData<String>()

    fun setupContent(rssItem: RssItem?) {
        rssItem?.let {
            if (configRepo.shouldLoadImages() && it.image != null) {
                loadImageLive.postTrue()
            } else {
                hideImageLive.postTrue()
            }
            titleLive.postValue(it.title)

            val date = configRepo.convertDate(it.pubDate)
            timeLive.postValue(date)
            detailLive.postValue(Utils.parseHtml(it.description))
        }
    }

}