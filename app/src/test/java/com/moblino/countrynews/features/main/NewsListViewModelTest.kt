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

package com.moblino.countrynews.features.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.moblino.countrynews.data.AppCache
import com.moblino.countrynews.data.LoggerRepository
import com.moblino.countrynews.data.firebase.FirebaseManager
import com.moblino.countrynews.features.Mock
import com.moblino.countrynews.features.saved.SavedNewsRepository
import com.moblino.countrynews.models.RssItem
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NewsListViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val rssRepo: RssRepository = mock()
    private val repo: SavedNewsRepository = mock()
    private val logger: LoggerRepository = mock()
    private val cache = AppCache()
    private val vm = NewsListViewModel(cache, rssRepo, repo, logger)

    @Before
    fun setup() {

    }

    @Test
    fun testFavoriteClicked() {
        vm.favoriteClicked(Mock.RSS_ITEM) { added ->
            assertEquals(true, added)
        }

        val captor = argumentCaptor<RssItem>()
        verify(repo).add(captor.capture())
        val item = captor.firstValue
        assertEquals("title", item.title)

        verify(logger).logEvent(FirebaseManager.EVENT_FAVOURITE)
    }
}