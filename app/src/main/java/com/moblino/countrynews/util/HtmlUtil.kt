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

package com.moblino.countrynews.util

import android.text.Html

object HtmlUtil {


    @JvmStatic
    fun parseHtml(html: String?): String? {
        var detail = html
        if (detail != null) {
            detail = detail.replace("<img .*?</img>".toRegex(), "")
            detail = detail.replace("<img .*?/>".toRegex(), "")
            detail = detail.replace("<IMG .*?/>".toRegex(), "")
            detail = detail.replace("<a .*?a/>".toRegex(), "")
            return Html.fromHtml(detail).toString()
        }
        return null
    }

    @JvmStatic
    fun fixDescription(description: String?): String? {
        var fixed = ""
        if (description != null) {
            fixed = description
                    .replace("&lt;", "<")
                    .replace("/&gt;", ">")
                    .replace("<img.+?>".toRegex(), "")
                    .replace("<IMG.+?>".toRegex(), "")
                    .trim { it <= ' ' }
        }
        return fixed
    }

    @JvmStatic
    fun getImageSourceFromDescription(description: String): String? {
        if (description.contains("img") && description.contains("src")) {
            val parts = description.split("src=\"".toRegex()).toTypedArray()
            if (parts.size >= 2 && parts[1].isNotEmpty()) {
                var src = parts[1].substring(0, parts[1].indexOf("\""))
                val srcParts = src.split("http".toRegex()).toTypedArray() // milliyet bozuk link icerdigi icin bu kod milliyet icin yazildi
                if (srcParts.size > 2) {
                    src = "http" + srcParts[2]
                }
                return src
            }
        }
        return null
    }

    @JvmStatic
    fun fixImageUrl(string: String): String? {
        return if (string.startsWith("//")) {
            "https:$string"
        } else string
    }

    @JvmStatic
    fun optimizeImage(imageUrl: String?, rssLink: String?): String? {
        if (imageUrl != null) {
            if (imageUrl.startsWith("file://")) {
                val replacement: String = if (rssLink != null && rssLink.contains("://")) {
                    rssLink.substring(0, rssLink.indexOf("://") + 3)
                } else {
                    "http://"
                }
                return imageUrl.replace("file://", replacement)
            }
            if (imageUrl.contains("http")) {
                return imageUrl
            }
        }
        return null
    }
}