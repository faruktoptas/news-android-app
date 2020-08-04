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

package com.moblino.countrynews.features.webview

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebViewClient
import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseMvvmActivity
import kotlinx.android.synthetic.main.activity_headings.webView

class HeadingsActivity : BaseMvvmActivity() {

    override fun layoutRes() = R.layout.activity_headings

    @SuppressLint("SetJavaScriptEnabled")
    override fun initViews(savedInstanceState: Bundle?) {

        webView.settings.apply {
            loadWithOverviewMode = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportMultipleWindows(true)
            allowFileAccess = false
            javaScriptEnabled = true
            domStorageEnabled = true
        }

        webView.webViewClient = WebViewClient()

        val url = getPreferenceWrapper().readHeadingUrl()
        if (url.isNullOrEmpty()) {
            finish()
        } else {
            webView.loadUrl(url)
        }
    }

}