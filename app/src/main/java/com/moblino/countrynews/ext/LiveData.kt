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

package com.moblino.countrynews.ext

import android.arch.lifecycle.*
import android.view.View

fun LiveData<Boolean>.observeTrue(owner: LifecycleOwner, block: (Boolean) -> Unit) {
    observe(owner, Observer {
        block(it == true)
    })
}

fun <T> LiveData<T>.observeNotNull(owner: LifecycleOwner, block: (T) -> Unit) {
    observe(owner, Observer {
        it?.apply { block(this) }
    })
}

fun <T> MutableList<T>.moveItems(firstIndex: Int, secondIndex: Int) {
    if (size > 0 && firstIndex != secondIndex && firstIndex < size && secondIndex < size) {
        val first = get(firstIndex)
        val second = get(secondIndex)
        remove(first)
        remove(second)

        if (firstIndex < secondIndex) {
            add(firstIndex, second)
            add(secondIndex, first)
        } else {
            add(secondIndex, first)
            add(firstIndex, second)
        }
    }
}

fun MutableLiveData<Boolean>.postTrue() = postValue(true)

fun MutableLiveData<Boolean>.postFalse() = postValue(false)

fun LiveData<Boolean>.asVisibility(owner: LifecycleOwner, view: View) {
    observeNotNull(owner, { view.show(it) })
}

fun <T> LiveData<List<T>?>.emptyLive() = Transformations.map(this) {
    it == null || it.isEmpty()
}