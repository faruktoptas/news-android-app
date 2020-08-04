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

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.moblino.countrynews.data.LoggerRepository
import com.moblino.countrynews.data.PrefRepository
import com.moblino.countrynews.data.firebase.FirebaseManager
import com.moblino.countrynews.features.detail.DetailRepository
import com.moblino.countrynews.features.detail.DetailViewModel
import com.nhaarman.mockitokotlin2.anyVararg
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

// TODO: assertEquals
class DetailViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: DetailViewModel

    private val prefRepo = Mockito.mock(PrefRepository::class.java)
    private val logRepo = Mockito.mock(LoggerRepository::class.java)
    private val detailRepo: DetailRepository = mock()

    @Before
    fun setup() {
        whenever(detailRepo.getFavorites()).thenReturn(MockData.RSS_ITEMS_SINGLE)
        whenever(detailRepo.getRssItemList(anyVararg())).thenReturn(MockData.RSS_ITEMS_MULTIPLE)
    }

    @Test
    fun testLogFireBase() {
        viewModel = DetailViewModel(detailRepo, logRepo, prefRepo)
        verify(logRepo).sendFireBaseEvent(FirebaseManager.EVENT_NEWS_DETAIL)
    }

    @Test
    fun testShowFancy() {
        whenever(prefRepo.readDetailShowCase()).thenReturn(false)
        viewModel = DetailViewModel(detailRepo, logRepo, prefRepo)
        assert(viewModel.showFancy.observedValue() == true)
    }

    @Test
    fun testNotShowFancy() {
        whenever(prefRepo.readDetailShowCase()).thenReturn(true)
        viewModel = DetailViewModel(detailRepo, logRepo, prefRepo)
        assert(viewModel.showFancy.observedValue() != true)
    }

    @Test
    fun testReadFromFavorites() {
        viewModel = DetailViewModel(detailRepo, logRepo, prefRepo)

        viewModel.getFeedList(null, 3, null)
        assert(viewModel.feedListLive.observedValue() == MockData.RSS_ITEMS_SINGLE)
        assert(viewModel.position == 3)
    }

    @Test
    fun testReadFromCache() {
        viewModel = DetailViewModel(detailRepo, logRepo, prefRepo)

        viewModel.getFeedList("url", 3, null)
        assert(viewModel.feedListLive.observedValue() == MockData.RSS_ITEMS_MULTIPLE)
        assert(viewModel.position == 3)
    }

    @Test
    fun testReadFromCacheWithCard() {
        viewModel = DetailViewModel(detailRepo, logRepo, prefRepo)

        viewModel.getFeedList("url", 3, MockData.CARD)
        assert(viewModel.feedListLive.observedValue() == MockData.RSS_ITEMS_MULTIPLE)
        assert(viewModel.position == 2)
    }

    @Test
    fun testFinish() {
        viewModel = DetailViewModel(detailRepo, logRepo, prefRepo)
        viewModel.finish()

        assert(viewModel.finishLive.observedValue() == false)
    }

    @Test
    fun testFinishWithResult() {
        viewModel = DetailViewModel(detailRepo, logRepo, prefRepo)
        viewModel.favoriteListChanged = true
        viewModel.finish()

        assert(viewModel.finishLive.observedValue() == true)
    }
}