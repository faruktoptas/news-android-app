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

package com.moblino.countrynews

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import com.moblino.countrynews.data.dbModule
import com.moblino.countynews.common.model.AppCache
import com.moblino.countrynews.data.firebase.FirebaseManager
import com.moblino.countrynews.di.appModule
import com.moblino.countrynews.util.FontSizeHelper
import com.moblino.countynews.common.PreferenceWrapper
import com.moblino.countynews.common.commonModule
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin


class NewsApplication : MultiDexApplication() {
    val appCache: AppCache by inject()

    lateinit var fontSizeHelper: FontSizeHelper
        private set

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(appModule, commonModule, dbModule))

        instance = this
        PreferenceWrapper.getInstance().init(applicationContext)
        FirebaseManager.getInstance().init(applicationContext)

        // TODO: inject with koin
        val client = OkHttpClient()
        val picasso = Picasso.Builder(this)
                .downloader(OkHttp3Downloader(client))
                .build()
        Picasso.setSingletonInstance(picasso)
        fontSizeHelper = FontSizeHelper()
        AppCompatDelegate.setDefaultNightMode(PreferenceWrapper.getInstance().readNightMode())
    }

    companion object {

        // TODO: remove
        @JvmStatic
        @get:Synchronized
        lateinit var instance: NewsApplication
            private set
    }
}
