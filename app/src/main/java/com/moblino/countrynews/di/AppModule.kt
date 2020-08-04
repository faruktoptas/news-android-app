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

package com.moblino.countrynews.di

import com.moblino.countrynews.BuildConfig
import com.moblino.countrynews.NewsApplication
import com.moblino.countrynews.data.*
import com.moblino.countrynews.features.detail.DetailRepository
import com.moblino.countrynews.features.detail.DetailRepositoryImpl
import com.moblino.countrynews.features.detail.DetailViewModel
import com.moblino.countrynews.features.detail.NewsDetailViewModel
import com.moblino.countrynews.features.editlist.EditFeedsViewModel
import com.moblino.countrynews.features.main.*
import com.moblino.countrynews.features.saved.SavedNewsRepository
import com.moblino.countrynews.features.saved.SavedNewsRepositoryImpl
import com.moblino.countrynews.features.saved.SavedNewsViewModel
import com.moblino.countrynews.features.search.SearchViewModel
import com.moblino.countrynews.features.webview.WebViewViewModel
import com.moblino.countrynews.utils.PreferenceWrapper
import com.moblino.countrynews.utils.UpdateChecker
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {


    single { androidContext() as NewsApplication }

    single { AppCache() }
    single { PreferenceWrapper.getInstance() }

    single<OkHttpClient> {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        }
        builder.build()
    }

    single { UpdateChecker(androidContext()) }

    single<DetailRepository> { DetailRepositoryImpl(get()) }
    single<LoggerRepository> { LoggerRepositoryImpl() }
    single<PrefRepository> { PrefRepositoryImpl() }
    single<SavedNewsRepository> { SavedNewsRepositoryImpl(get(), get<NewsApplication>().favouritePersistenceManager) }
    single<MainRepository> { MainRepositoryImpl(androidContext().assets) }
    single<ConfigRepository> { ConfigRepositoryImpl(androidContext()) }
    single<RssRepository> { RssRepositoryImpl(get()) }

    viewModel { DetailViewModel(get(), get(), get()) }
    viewModel { EditFeedsViewModel(get(), get(), get()) }
    viewModel { SavedNewsViewModel(get(), get(), get(), get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { MainViewModel(get(), get(), get(), get(), get()) }
    viewModel { NewsListViewModel(get(), get(), get(), get()) }
    viewModel { NewsDetailViewModel(get()) }
    viewModel { WebViewViewModel(get(), get()) }
}