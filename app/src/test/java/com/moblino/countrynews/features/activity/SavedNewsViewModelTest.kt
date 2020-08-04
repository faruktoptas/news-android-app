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
import android.arch.lifecycle.LiveData
import com.moblino.countrynews.data.AppCache
import com.moblino.countrynews.data.LoggerRepository
import com.moblino.countrynews.data.PrefRepository
import com.moblino.countrynews.data.ResourceRepository
import com.moblino.countrynews.features.saved.SavedNewsRepository
import com.moblino.countrynews.features.saved.SavedNewsViewModel
import com.nhaarman.mockitokotlin2.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class SavedNewsViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: SavedNewsViewModel
    private val repo: SavedNewsRepository = Mockito.mock(SavedNewsRepository::class.java)
    private val prefRepo = Mockito.mock(PrefRepository::class.java)
    private val resRepo = Mockito.mock(ResourceRepository::class.java)
    private val logRepo = Mockito.mock(LoggerRepository::class.java)
    private val cache = AppCache()

    @Before
    fun setup() {
        viewModel = SavedNewsViewModel(repo, prefRepo, cache, logRepo)
        viewModel.res = resRepo
        Mockito.`when`(prefRepo.isStaggered()).thenReturn(true)
        Mockito.`when`(resRepo.getString(Mockito.anyInt())).thenReturn("Title")
        cache.favoriteList.addAll(MockData.RSS_ITEMS_SINGLE)
    }

    @After
    fun teardown() {
        cache.favoriteList.clear()
    }

    @Test
    fun testEmptySavedList() {
        cache.favoriteList.clear()
        viewModel.getItems()
        assert(viewModel.emptyListLive.observedValue() == true)
        assert(viewModel.titleLive.observedValue() == "Title")
    }

    @Test
    fun testNonEmptySavedList() {
        viewModel.getItems()
        assertEquals(1, viewModel.itemsLive.observedValue()?.size)
        assert(viewModel.emptyListLive.observedValue() == false)
        assert(viewModel.titleLive.observedValue() == "Title (1)")
    }

    @Test
    fun testItemRemovedFromSavedNews() {
        viewModel.removeItem(MockData.RSS_ITEMS_SINGLE[0])
        verify(repo).removeFromDb("link")
        verify(repo).removeFromMemory("link")
        assert(viewModel.notifyItemRemoved.observedValue() == 0)
        assert(viewModel.showSnackBar.observedValue() == true)
    }

    @Test
    fun testSavedNewsItemDetail() {
        viewModel.goToItemDetail(MockData.RSS_ITEMS_SINGLE[0], 0)
        verify(logRepo).sendScreen("feedTitle")
    }
}


fun <T> LiveData<T>.observedValue(): T? {
    observeForever { }
    return value
}