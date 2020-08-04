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

package com.moblino.countrynews.base

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.moblino.countrynews.data.ResourceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

open class BaseViewModel : ViewModel() {

    private val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)

    val finishLive = MutableLiveData<Boolean>()
    val toastLive = MutableLiveData<String>()

    lateinit var res: ResourceRepository

    override fun onCleared() {
        job.cancel()
        super.onCleared()
    }
}