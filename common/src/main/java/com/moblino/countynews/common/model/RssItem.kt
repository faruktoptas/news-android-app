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

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Favourites")
data class RssItem(
        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,
        var title: String? = "",
        var link: String = "",
        var image: String? = "",
        var pubDate: String = "",
        var feedId: Int = -1,
        var feedTitle: String? = "",
        var description: String? = ""
) : Serializable