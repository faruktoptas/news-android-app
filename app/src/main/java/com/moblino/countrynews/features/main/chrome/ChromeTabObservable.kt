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

package com.moblino.countrynews.features.main.chrome

import android.app.Activity
import android.app.PendingIntent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.browser.customtabs.*
import androidx.core.content.ContextCompat
import com.moblino.countrynews.R
import com.moblino.countrynews.data.firebase.FirebaseManager
import com.moblino.countrynews.features.detail.DetailActivity
import com.moblino.countrynews.features.settings.SettingsActivity
import com.moblino.countrynews.features.webview.WebViewActivity
import com.moblino.countynews.common.model.CardQuestion
import com.moblino.countynews.common.model.RssItem
import com.moblino.countrynews.util.Constants
import com.moblino.countynews.common.PreferenceWrapper
import com.moblino.countrynews.util.ShareReceiver


class ChromeTabObservable(private val activity: Activity) : LifecycleObserver, ServiceConnectionCallback {

    private var customTabsClient: CustomTabsClient? = null
    private var customTabsServiceConnection: CustomTabsServiceConnection? = null
    private var customTabsSession: CustomTabsSession? = null
    private val pref = PreferenceWrapper.getInstance()

    fun openNewsDetail(model: RssItem, rssUrl: String?, position: Int, cardQuestion: CardQuestion?) {
        if (pref.readShowDetailFirst()) {
            val intent = Intent(activity, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_RSS_URL, rssUrl)
            intent.putExtra(DetailActivity.EXTRA_POSITION, position)
            intent.putExtra(DetailActivity.EXTRA_RSS_ITEM_MODEL, model)
            if (cardQuestion != null) {
                intent.putExtra(DetailActivity.EXTRA_CARD_QUESTION, cardQuestion)
            }
            activity.startActivityForResult(intent, Constants.REQ_CODE_LIST_CHANGED)
            activity.overridePendingTransition(0, 0)
        } else {
            startChromeTabs(model)
        }
    }

    fun startChromeTabs(model: RssItem) {
        val readabilityMode: Boolean = pref.readGWLMode()
        val url = if (readabilityMode) Constants.GOOGLE_WEB_LIGHT_URL + model.link else model.link
        val newsUri = Uri.parse(url)
        val shareIntent = Intent(activity, ShareReceiver::class.java)
        shareIntent.putExtra(Intent.EXTRA_TEXT, model.title + " - " + model.link)
        val sharePI = PendingIntent.getBroadcast(activity, 0, shareIntent, 0)
        val icon = BitmapFactory.decodeResource(activity.resources, R.drawable.ic_action_share)
        val settingsIntent = Intent(activity, SettingsActivity::class.java)
        val settingsPI = PendingIntent.getActivity(activity, Constants.REQ_CODE_SETTINGS_CHANGED, settingsIntent, 0)
        if (customTabsClient != null) {
            customTabsSession?.mayLaunchUrl(newsUri, null, null)
            try {
                val builder = CustomTabsIntent.Builder(getSession())
                builder.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                builder.setActionButton(icon, activity.getString(R.string.title_share), sharePI)
                builder.addMenuItem(activity.getString(R.string.title_gwl_mode), settingsPI)
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(activity, newsUri)
            } catch (e: Exception) {
                e.printStackTrace()
                startWebViewActivity(model)
            }
        } else {
            startWebViewActivity(model)
        }
        FirebaseManager.getInstance().logNewsBrowse()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        bindCustomTabService()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        unBindCustomTabService()
    }

    private fun startWebViewActivity(model: RssItem) {
        val intent = Intent(activity, WebViewActivity::class.java)
        intent.putExtra(WebViewActivity.EXTRA_SUBTITLE, model.title)
        intent.putExtra(WebViewActivity.EXTRA_URL, model.link)
        activity.startActivity(intent)
    }

    private fun bindCustomTabService() {
        if (customTabsServiceConnection == null) {
            customTabsServiceConnection = ServiceConnection(this)
        }
        CustomTabsClient.bindCustomTabsService(activity, Constants.CUSTOM_TAB_PACKAGE_NAME, customTabsServiceConnection!!)
    }

    private fun unBindCustomTabService() {
        if (customTabsServiceConnection != null) {
            activity.unbindService(customTabsServiceConnection!!)
            customTabsClient = null
        }
    }

    private fun getSession(): CustomTabsSession? {
        if (customTabsClient == null) {
            customTabsSession = null
        } else if (customTabsSession == null) {
            customTabsSession = customTabsClient?.newSession(CustomTabsCallback())
        }
        return customTabsSession
    }

    override fun onServiceConnected(client: CustomTabsClient?) {
        customTabsClient = client
    }

    override fun onServiceDisconnected() {
        customTabsClient = null
    }
}