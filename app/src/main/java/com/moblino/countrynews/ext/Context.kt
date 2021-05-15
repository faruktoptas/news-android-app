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

package com.moblino.countrynews.ext

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import com.moblino.countrynews.R
import com.moblino.countrynews.util.DeviceInfo
import com.moblino.countrynews.util.PreferenceWrapper

fun Context.getAppVersion(): String {
    return try {
        packageManager.getPackageInfo(packageName, 0).versionName ?: ""
    } catch (e: PackageManager.NameNotFoundException) {
        ""
    }
}

fun Context.shareText(text: String?) {
    if (text.isNullOrEmpty()) return
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "text/plain"
    shareIntent.putExtra(Intent.EXTRA_TEXT, text)
    val chooserIntent = Intent.createChooser(shareIntent, getString(R.string.title_share))
    chooserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    startActivity(chooserIntent)
}

fun Context.goToPlayStore() {
    val appPackageName = packageName
    try {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
    } catch (e: ActivityNotFoundException) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
    }
}

fun Context.openEmailIntent() {
    try {
        val content = """
            ${DeviceInfo.getDeviceManufacturer()}
            ${DeviceInfo.getDeviceModel()} - ${DeviceInfo.getOSVersion()}
            GWL: ${if (PreferenceWrapper.getInstance().readGWLMode()) "1" else "0"}
            OG: ${if (PreferenceWrapper.getInstance().readShowDetailFirst()) "1" else "0"}"""
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", getString(R.string.contact_email), null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
        emailIntent.putExtra(Intent.EXTRA_TEXT, content)
        startActivity(Intent.createChooser(emailIntent, "Email"))
    } catch (e: ActivityNotFoundException) {
        goToPlayStore()
    }
}

fun Context.isNightMode(): Boolean {
    val currentNightMode = (resources.configuration.uiMode
            and Configuration.UI_MODE_NIGHT_MASK)
    when (currentNightMode) {
        Configuration.UI_MODE_NIGHT_NO ->                 // Night mode is not active, we're in day time
            return false
        Configuration.UI_MODE_NIGHT_YES ->                 // Night mode is active, we're at night!
            return true
        Configuration.UI_MODE_NIGHT_UNDEFINED ->                 // We don't know what mode we're in, assume notnight
            return false
    }
    return false
}