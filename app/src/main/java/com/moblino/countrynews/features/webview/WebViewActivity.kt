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

import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CheckBox
import android.widget.CompoundButton
import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseMvvmActivity
import com.moblino.countrynews.ext.*
import com.moblino.countrynews.utils.PreferenceWrapper
import kotlinx.android.synthetic.main.activity_webview.*
import org.koin.android.viewmodel.ext.android.viewModel

class WebViewActivity : BaseMvvmActivity(), CompoundButton.OnCheckedChangeListener {

    private var subtitle: String? = null
    private var loadUrl: String? = null

    private val viewModel: WebViewViewModel by viewModel()
    private val pref = PreferenceWrapper.getInstance()

    override fun layoutRes() = R.layout.activity_webview

    override fun initViews(savedInstanceState: Bundle?) {
        loadUrl = intent.getStringExtra(EXTRA_URL)
        subtitle = intent?.getStringExtra(EXTRA_SUBTITLE)

        setupToolbar()
        setupActionBar()

        webView.webViewClient = MyWebViewClient()
        observe()
        loadUrl()
    }

    private fun observe() {
        webView.settings.blockNetworkImage = viewModel.shouldLoadImages()
        viewModel.loadUrlLive.observeNotNull(this, { webView.loadUrl(it) })
        viewModel.finishLive.observeTrue(this, { finish() })
    }

    private fun loadUrl() {
        pbLoading.show()
        viewModel.load(loadUrl)
    }

    private fun setupActionBar() {
        if (subtitle != null) {
            supportActionBar?.let {
                it.setDisplayHomeAsUpEnabled(true)
                it.setDisplayShowTitleEnabled(false)
                it.setCustomView(R.layout.actionbar_webview)
                it.setDisplayShowCustomEnabled(true)
                if (it.customView != null) {
                    val actionBarView = it.customView
                    val switchCompat = actionBarView.findViewById<View>(R.id.swReadingMode) as CheckBox
                    switchCompat.setTextColor(ContextCompat.getColor(this, R.color.mainTextColor))
                    actionBarView.findViewById<View>(R.id.btnShare).setOnClickListener {
                        if (!loadUrl.isNullOrEmpty()) {
                            shareText("$subtitle - $loadUrl")
                        }
                    }
                    switchCompat.isChecked = pref.readGWLMode()
                    switchCompat.setOnCheckedChangeListener(this)
                }
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        viewModel.gwlEnabled = isChecked
        loadUrl()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    public override fun onStop() {
        webView.stopLoading()
        super.onStop()
    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            pbLoading.hide()
        }
    }

    companion object {
        const val EXTRA_URL = "EXTRA_URL"
        const val EXTRA_SUBTITLE = "EXTRA_SUBTITLE"
    }
}