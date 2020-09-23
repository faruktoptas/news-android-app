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

import com.moblino.countrynews.data.localdb.FavouritePersistenceManager
import com.moblino.countrynews.data.AppCache
import com.moblino.countynews.common.model.RssItem

interface SavedNewsRepository {

    fun removeFromDb(link: String)

    fun removeFromMemory(link: String): Int

    fun getSavedByUrl(link: String): List<RssItem>

    fun add(item: RssItem)
}

class SavedNewsRepositoryImpl(private val appCache: AppCache,
                              private val pm: FavouritePersistenceManager) : SavedNewsRepository {

    override fun removeFromDb(link: String) {
        pm.deleteByUrl(link)
    }

    override fun removeFromMemory(link: String): Int {
        val pos = AppCache.isFavorite(link)
        if (pos > -1) {
            appCache.favoriteList.removeAt(pos)
        }
        return pos
    }

    override fun getSavedByUrl(link: String): List<RssItem> = pm.getFavouritesByUrl(link)

    override fun add(item: RssItem) {
        pm.create(item)
    }
}

