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

import com.moblino.countynews.common.model.AppCache
import com.moblino.countynews.common.model.RssItem

interface DetailRepository {

    fun getFavorites(): List<RssItem>

    fun getRssItemList(url: String): List<RssItem>?
}

class DetailRepositoryImpl(private val appCache: AppCache) : DetailRepository {

    override fun getFavorites(): List<RssItem> = appCache.favoriteList.reversed()

    override fun getRssItemList(url: String) = appCache.responseList[url]?.items

}