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

package com.moblino.countrynews.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.moblino.countynews.common.ext.getAppVersion
import com.moblino.countynews.common.BuildConfig
import com.moblino.countynews.common.PreferenceWrapper
import java.util.*

class RemoteConfigHelper {

    private val remoteConfig = FirebaseRemoteConfig.getInstance()
    private val pref = PreferenceWrapper.getInstance()

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 12 * 60 * 60
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
    }

    fun initialize(context: Context) {
        val defaults = HashMap<String, Any>()
        defaults[VALUE_LATEST_VERSION] = context.getAppVersion()

        remoteConfig.setDefaultsAsync(defaults)

        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                val latestVersion = remoteConfig.getString(VALUE_LATEST_VERSION)
                val excludedNews = remoteConfig.getString(VALUE_EXCLUDED_NEWS)
                val headingUrl = remoteConfig.getString(VALUE_HEADING_URL)
                Log.v("asd", "Remote config fetch successful $latestVersion $headingUrl $excludedNews")

                pref.writeRemoteExcludedNews(excludedNews)
                if (latestVersion.isNotEmpty()) {
                    pref.writeString(PreferenceWrapper.KEY_NEW_VERSION, latestVersion)
                }
                pref.writeHeadingUrl(headingUrl)
            }
        }
    }

    fun isNewVersionAvailable(context: Context): Boolean {
        val latestVersion = pref.readString(PreferenceWrapper.KEY_NEW_VERSION)
        var currentVersion = context.getAppVersion()
        currentVersion = currentVersion.replace(".", "")
        val latestVersionWithoutDot = latestVersion.replace(".", "")
        if (latestVersionWithoutDot.isNotEmpty()) {
            val current = currentVersion.toInt()
            val latest = latestVersionWithoutDot.toInt()
            return latest > current
        }
        return false
    }

    companion object {
        const val USER_PROP_READABILITY = "ReadabilityEnabled"
        const val VALUE_LATEST_VERSION = "latest_version"
        const val VALUE_EXCLUDED_NEWS = "excluded_news"
        private const val VALUE_HEADING_URL = "heading_url"
    }

}