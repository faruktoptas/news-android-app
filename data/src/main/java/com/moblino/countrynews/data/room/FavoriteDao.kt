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

package com.moblino.countrynews.data.room

import androidx.room.*
import com.moblino.countynews.common.model.RssItem

@Dao
interface FavoriteDao {

    @Query("Select * from fav order by id desc")
    fun getAll(): List<RssItem>

    @Insert
    fun add(favorite: RssItem)

    @Delete
    fun delete(favorite: RssItem)

    @Query("DELETE FROM fav WHERE link = :url")
    fun deleteByUrl(url: String)

}