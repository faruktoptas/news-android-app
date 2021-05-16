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

import com.moblino.countrynews.data.room.FavoriteDao
import com.moblino.countynews.common.ext.withIoDispatcher
import com.moblino.countynews.common.model.RssItem

interface SavedNewsRepository {

    suspend fun getAll(): List<RssItem>

    suspend fun removeFromDb(link: String)

    suspend fun add(item: RssItem)
}

class SavedNewsRepositoryImpl(private val dao: FavoriteDao) : SavedNewsRepository {

    override suspend fun getAll() = withIoDispatcher { dao.getAll() }

    override suspend fun removeFromDb(link: String) = withIoDispatcher { dao.deleteByUrl(link) }

    override suspend fun add(item: RssItem) = withIoDispatcher { dao.add(item) }
}

