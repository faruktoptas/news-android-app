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

package com.moblino.countrynews.utils

import android.util.TypedValue
import android.widget.TextView

/**
 * Created by ftoptas on 06/0index/18.
 */
class FontSizeHelper {

    private val listTitleSizes = listOf(15f, 17f, 19f, 21f)
    private val listPubdateSizes = listOf(11f, 12f, 13f, 14f)
    private val detailTitleSizes = listOf(20f, 22f, 26f, 28f)
    private val detailPubdateSizes = listOf(12f, 14f, 17f, 20f)
    private val detailContentSizes = listOf(18f, 20f, 24f, 28f)
    var index = 1

    init {
        update(PreferenceWrapper.getInstance().readFontSize())
    }

    fun update(i: Int) {
        index = i
    }

    fun listTitle(textView: TextView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, listTitleSizes[index])
    }

    fun listPubdate(textView: TextView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, listPubdateSizes[index])
    }

    fun detailTitle(textView: TextView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, detailTitleSizes[index])
    }

    fun detailPubDate(textView: TextView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, detailPubdateSizes[index])
    }

    fun detailContent(textView: TextView) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, detailContentSizes[index])
    }


}