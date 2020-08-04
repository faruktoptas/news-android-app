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
import com.moblino.countrynews.data.ResourceRepository
import com.moblino.countrynews.features.editlist.EditFeedsViewModel
import com.moblino.countrynews.data.AppCache
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class EditFeedsViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var viewModel: EditFeedsViewModel

    private val prefRepo = Mockito.mock(PrefRepository::class.java)
    private val logRepo = Mockito.mock(LoggerRepository::class.java)
    private val res: ResourceRepository = mock()
    private val appCache = AppCache().apply { currentFeedList.addAll(MockData.FEED_ITEMS) }

    @Before
    fun setup() {
        viewModel = EditFeedsViewModel(prefRepo, logRepo, appCache)
        viewModel.res = res
    }

    @Test
    fun getListWithEmptyExcluded() {
        whenever(prefRepo.readCheckString()).thenReturn("")
        viewModel.getList()

        assert(viewModel.excluded.isEmpty())

        viewModel.checkedChanged(MockData.FEED_ITEMS[0])
        assert(viewModel.excluded[0] == MockData.FEED_ITEMS[0].feedId)
    }

    @Test
    fun getListWithSingleExcluded() {
        whenever(prefRepo.readCheckString()).thenReturn("1")
        viewModel.getList()

        assert(viewModel.excluded.size == 1)
        assert(viewModel.excluded[0] == 1)
    }

    @Test
    fun getListWithMultipleExcluded() {
        whenever(prefRepo.readCheckString()).thenReturn("1,2")
        viewModel.getList()

        assert(viewModel.excluded.size == 2)
        assert(viewModel.excluded[0] == 1 && viewModel.excluded[1] == 2)
    }

    @Test
    fun addItemToExcludedList() {
        whenever(prefRepo.readCheckString()).thenReturn("1,2")
        viewModel.getList()

        assert(viewModel.excluded.size == 2)
        assert(viewModel.excluded[0] == 1 && viewModel.excluded[1] == 2)

        viewModel.checkedChanged(MockData.FEED_ITEMS[0])
        assert(viewModel.excluded[2] == MockData.FEED_ITEMS[0].feedId)
    }

    @Test
    fun removeItemFromExcludedList() {
        whenever(prefRepo.readCheckString()).thenReturn("1,2")
        viewModel.getList()

        assert(viewModel.excluded.size == 2)
        assert(viewModel.excluded[0] == 1 && viewModel.excluded[1] == 2)

        viewModel.checkedChanged(MockData.FEED_ITEMS[1])
        assert(viewModel.excluded.size == 1)
        assert(viewModel.excluded[0] == 2)

        assert(viewModel.isChecked(1))
        assert(!viewModel.isChecked(2))
    }


    @Test
    fun emptyCheckedItemSave() {
        whenever(prefRepo.readCheckString()).thenReturn("")
        whenever(res.getString(any())).thenReturn("At least one")
        viewModel.getList()
        viewModel.checkedChanged(MockData.FEED_ITEMS[0])
        viewModel.checkedChanged(MockData.FEED_ITEMS[1])
        viewModel.checkedChanged(MockData.FEED_ITEMS[2])

        viewModel.save()
        Assert.assertEquals(viewModel.toastLive.observedValue()?.isNotEmpty(), true)
    }

    @Test
    fun addAndSaveList() {
        whenever(prefRepo.readCheckString()).thenReturn("1")
        viewModel.getList()
        viewModel.checkedChanged(MockData.FEED_ITEMS[0])

        viewModel.save()
        verify(prefRepo).writeCheckString("1,0")
    }

    @Test
    fun moveItemAndSaveList() {
        viewModel.getList()
        viewModel.onItemMove(0, 1)

        viewModel.save()
        verify(prefRepo).writeListOrder("1,0,2")
    }


}