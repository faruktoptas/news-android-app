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

package com.moblino.countynews.common

/**
 * Merges local (SharedPreferences) and remote (Firebase Remote config) excludes
 */
object ExcludeMerger {

    /**
     * remote format {FEED_ID1}_{CATEGORY_ID1},{FEED_ID2}_{CATEGORY_ID2}...
     *
     */
    @JvmStatic
    fun mergeLocalAndRemotes(categoryId: Int, local: String, remote: String): String? {
        var excluded = local
        if (remote.isNotEmpty()) {
            val remoteList = remote.split(",".toRegex()).toTypedArray()
            // iterate remotes
            for (r in remoteList) {
                // split into FeedId and CategoryId
                val item = r.split("_".toRegex()).toTypedArray()
                if (item[1] == "$categoryId") {
                    excluded = excluded + (if (excluded.isNotEmpty()) "," else "") + item[0]
                }
            }
        }
        return excluded
    }

}