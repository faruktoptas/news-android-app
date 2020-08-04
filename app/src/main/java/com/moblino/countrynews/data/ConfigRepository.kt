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

package com.moblino.countrynews.data

import android.content.Context
import com.moblino.countrynews.utils.Utils

interface ConfigRepository {

    fun shouldLoadImages(): Boolean

    fun convertDate(date: String?): String?

}

class ConfigRepositoryImpl(private val context: Context) : ConfigRepository {

    override fun shouldLoadImages() = Utils.isLoadImagesEnabled(context)

    override fun convertDate(date: String?): String? {
        return Utils.convertDate(context, date) // TODO: check languages
    }
}