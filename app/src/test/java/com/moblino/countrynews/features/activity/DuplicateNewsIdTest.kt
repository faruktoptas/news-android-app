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

import com.google.gson.Gson
import com.moblino.countrynews.model.FeedItem
import com.moblino.countrynews.util.Constants
import org.junit.Test
import java.io.File
import java.lang.AssertionError

class DuplicateNewsIdTest {

    private val gson = Gson()


    @Test
    fun findDuplicates() {
        val userDir = System.getProperty("user.dir", "./")
        val assetsFolder = "$userDir/src/main/assets/"

        val countries = File(assetsFolder)

        countries.list()
                .filter { it.length == 2 }
                .forEach { country ->
                    val sources = File("$assetsFolder/$country/")
                    sources.list()
                            .filter {
                                it != Constants.CATEGORY_FILE
                            }
                            .forEach {
                                val path = "$assetsFolder$country/$it"
                                val content = readFile(path)
                                val feeds = gson.fromJson<Array<FeedItem>>(content, Array<FeedItem>::class.java)
                                hasDuplicate(feeds)
                            }
                }
    }

    private fun hasDuplicate(feeds: Array<FeedItem>) {
        val ids = arrayListOf<Int>()
        feeds.forEach {
            if (!ids.contains(it.feedId)) {
                ids.add(it.feedId)
            } else {
                throw AssertionError("Duplicate Id. ${it.feedUrl} ${it.feedId}")
            }
        }
    }

    private fun readFile(fileName: String) = File(fileName).inputStream().readBytes().toString(Charsets.UTF_8)

}
