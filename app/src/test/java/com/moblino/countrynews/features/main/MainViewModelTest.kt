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
import com.moblino.countrynews.data.PrefRepository
import com.moblino.countrynews.features.activity.MockData
import com.moblino.countrynews.features.activity.observedValue
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Locale

class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val repo: MainRepository = mock()
    private val pref: PrefRepository = mock()
    private val logger: LoggerRepository = mock()
    private val cache = AppCache()
    private val vm = MainViewModel(repo, pref, cache, logger)

    @Before
    fun setup() {
        whenever(repo.categoriesByCountry(any())).thenReturn(MockData.CATEGORIES)
        whenever(pref.getListOrder()).thenReturn("")

        whenever(repo.getCountryFiles(any())).thenReturn(arrayOf("categories.json", "1.json", "0.json"))
        whenever(repo.feedsByCategory(any(), eq(0))).thenReturn(MockData.FEED_ITEMS)
        whenever(repo.feedsByCategory(any(), eq(1))).thenReturn(MockData.FEED_ITEMS2)
    }

    @Test
    fun testAppFirstStart() {
        whenever(pref.readAppVersion()).thenReturn(false)
        whenever(pref.readStartCount()).thenReturn(0)
        vm.initialize()
        verify(pref).writeStartCount(1)

        assertNotEquals(vm.showShowCaseLive.observedValue(), false)
        assertNotEquals(vm.showWhatsNewLive.observedValue(), false)
    }

    @Test
    fun testAppUpdated() {
        whenever(pref.readAppVersion()).thenReturn(true)
        vm.initialize()

        assertEquals(vm.showWhatsNewLive.observedValue(), true)
        assertNotEquals(vm.showShowCaseLive.observedValue(), false)
    }

    @Test
    fun testShowShowCase() {
        whenever(pref.readStartCount()).thenReturn(4)
        vm.initialize()

        assertNotEquals(vm.showWhatsNewLive.observedValue(), true)
        assertEquals(vm.showShowCaseLive.observedValue(), true)
    }

    @Test
    fun testSetupCountry() {
        whenever(pref.readCountry()).thenReturn("")
        whenever(repo.getCountryFiles(any())).thenReturn(arrayOf("categories.json", "0.json"))

        vm.setup(0)

        verify(pref).writeCountry(Locale.getDefault().country)
    }

    @Test
    fun testCategories() {
        vm.setup(0)

        assertEquals(cache.allFeeds.size, 5)
        assertEquals(cache.allFeeds.first().categoryId, 0)
        assertEquals(cache.allFeeds.first().category, "cat0")
        assertEquals(cache.allFeeds.last().categoryId, 1)
        assertEquals(cache.allFeeds.last().category, "cat1")

        assertEquals(vm.navigationItemsLive.observedValue()?.first, 0)
        assertEquals(vm.navigationItemsLive.observedValue()?.second?.size, 2)

        assertEquals(vm.titleLive.observedValue(), "cat0")
    }

    @Test
    fun testFeedItemListOrder() {
        whenever(pref.getListOrder()).thenReturn("2,0,1")
        vm.setup(0)

        assertEquals(vm.viewPagerItemsLive.observedValue()!![0], MockData.FEED_ITEMS[2])
        assertEquals(vm.viewPagerItemsLive.observedValue()!![1], MockData.FEED_ITEMS[0])
        assertEquals(vm.viewPagerItemsLive.observedValue()!![2], MockData.FEED_ITEMS[1])
    }

    @Test
    fun testFeedItemListOrderWithDifferentOrderSize() {
        whenever(pref.getListOrder()).thenReturn("1,0")
        vm.setup(0)

        assertEquals(vm.viewPagerItemsLive.observedValue()!![0], MockData.FEED_ITEMS[1])
        assertEquals(vm.viewPagerItemsLive.observedValue()!![1], MockData.FEED_ITEMS[0])
        assertEquals(vm.viewPagerItemsLive.observedValue()!![2], MockData.FEED_ITEMS[2])
    }

    @Test
    fun testFeedItemListWithExcludes() {
        whenever(pref.readCheckString()).thenReturn("1")
        vm.setup(0)

        assertEquals(vm.viewPagerItemsLive.observedValue()?.size, 2)
        assertEquals(vm.viewPagerItemsLive.observedValue()!![0], MockData.FEED_ITEMS[0])
        assertEquals(vm.viewPagerItemsLive.observedValue()!![1], MockData.FEED_ITEMS[2])
    }


    @Test
    fun testRefresh() {
        vm.refresh(2)

        assertEquals(vm.resetViewPagerLive.observedValue(), true)
        assertEquals(vm.goToTabLive.observedValue(), 2)
    }

    @Test
    fun testChangeCategory() {
        vm.changeCategory(1)

        assertEquals(vm.resetViewPagerLive.observedValue(), true)
        verify(pref).writeCategoryId(1)
    }

    @Test
    fun testSelectSearchedItem() {
        vm.setup(0)
        vm.selectSearchedItem(MockData.FEED_ITEMS[1])

        assertEquals(vm.goToTabLive.observedValue(), 1)
    }

    @Test
    fun testSelectSearchedItemWithDifferentCategory() {
        vm.setup(0)
        vm.selectSearchedItem(MockData.FEED_ITEMS2[1])

        assertEquals(vm.goToTabLive.observedValue(), 1)
        verify(pref).writeCategoryId(1)
    }

    @Test
    fun testLogNewsClick() {
        vm.setup(0)
        vm.logNewsClick(1)

        verify(logger).sendScreen(MockData.FEED_ITEMS[1].title)
    }
}