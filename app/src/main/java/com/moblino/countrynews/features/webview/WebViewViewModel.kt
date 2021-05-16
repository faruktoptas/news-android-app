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

package com.moblino.countrynews.features.webview

import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseViewModel
import com.moblino.countrynews.data.ConfigRepository
import com.moblino.countrynews.data.PrefRepository
import com.moblino.countrynews.util.SingleLiveEvent
import com.moblino.countrynews.ext.postTrue
import com.moblino.countrynews.util.Constants

class WebViewViewModel(private val configRepo: ConfigRepository, pref: PrefRepository) : BaseViewModel() {

    val loadUrlLive = SingleLiveEvent<String>()
    var gwlEnabled = pref.readGWLMode()

    fun shouldLoadImages() = configRepo.shouldLoadImages()

    fun load(url: String?) {
        url?.let {
            val fullUrl = if (gwlEnabled) {
                Constants.GOOGLE_WEB_LIGHT_URL + it
            } else {
                it
            }
            loadUrlLive.postValue(fullUrl)
        } ?: showError()
    }

    private fun showError() {
        toastLive.postValue(res.getString(R.string.error_json_webview_url))
        finishLive.postTrue()
    }
}