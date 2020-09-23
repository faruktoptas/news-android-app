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

package com.moblino.countrynews.features.activity

import com.moblino.countrynews.model.CardQuestion
import com.moblino.countynews.common.model.Category
import com.moblino.countynews.common.model.FeedItem
import com.moblino.countynews.common.model.RssItem

object MockData {
    val RSS_ITEMS_SINGLE = listOf(RssItem("title", "link", "image", "date", 0, "feedTitle", "desc"))
    val RSS_ITEMS_MULTIPLE = listOf(
            RssItem("title1", "link1", "image1", "date1", 1, "feedTitle", "desc"),
            RssItem("title2", "link2", "image2", "date2", 2, "feedTitle", "desc")
    )
    val CARD = CardQuestion(0, true)
    val FEED_ITEMS = listOf(
            FeedItem().apply {
                feedId = 0
                title = "feed0"
            },
            FeedItem().apply {
                feedId = 1
                title = "feed1"
            },
            FeedItem().apply {
                feedId = 2
                title = "feed2"
            }
    )

    val FEED_ITEMS2 = listOf(
            FeedItem().apply {
                feedId = 3
                title = "feed3"
            },
            FeedItem().apply {
                feedId = 4
                title = "feed4"
            }
    )

    val CATEGORIES = listOf(
            Category(0, "cat0"),
            Category(1, "cat1"),
            Category(2, "cat2")
    )
}