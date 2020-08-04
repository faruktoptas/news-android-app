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

import androidx.lifecycle.MutableLiveData
import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseViewModel
import com.moblino.countrynews.data.SingleLiveEvent
import com.moblino.countrynews.data.LoggerRepository
import com.moblino.countrynews.data.PrefRepository
import com.moblino.countrynews.data.AppCache
import com.moblino.countrynews.models.RssItem

class SavedNewsViewModel(private val repo: SavedNewsRepository,
                         private val prefRepo: PrefRepository,
                         private val appCache: AppCache,
                         private val loggerRepository: LoggerRepository
) : BaseViewModel() {

    val itemsLive = MutableLiveData<List<RssItem>>()
    val emptyListLive = MutableLiveData<Boolean>()
    val isStaggeredLive = MutableLiveData<Boolean>()
    val titleLive = MutableLiveData<String>()
    val notifyItemRemoved = SingleLiveEvent<Int>()
    val showSnackBar = SingleLiveEvent<Boolean>()

    fun getItems() {
        isStaggeredLive.postValue(prefRepo.isStaggered())
        refresh()
    }

    fun refresh() {
        val items = appCache.favoriteList.reversed()
        itemsLive.postValue(items)
        emptyListLive.postValue(items.isEmpty())

        var title = res.getString(R.string.title_favourites)
        if (items.isNotEmpty()) {
            title += " (${items.size})"
        }
        titleLive.postValue(title)
    }

    fun goToItemDetail(model: RssItem, position: Int) {
        if (model.feedTitle != null) {
            loggerRepository.sendScreen(model.feedTitle)
        }
    }


    fun removeItem(item: RssItem) {
        repo.removeFromDb(item.link)
        val position = repo.removeFromMemory(item.link)
        notifyItemRemoved.postValue(position)
        showSnackBar.postValue(true)
        refresh()
    }

}