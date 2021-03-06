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

package com.moblino.countynews.common.model


data class AppCache(val allFeeds: ArrayList<FeedItem> = arrayListOf(),
                    val currentFeedList: ArrayList<FeedItem> = arrayListOf(),
                    val responseList: HashMap<String, RssResponse.Success> = hashMapOf(),
                    val favoriteList: ArrayList<RssItem> = arrayListOf()
) {

    fun isFavorite(url: String): Int {
        for (i in favoriteList.indices) {
            if (favoriteList[i].link == url) {
                return i
            }
        }
        return -1
    }
}