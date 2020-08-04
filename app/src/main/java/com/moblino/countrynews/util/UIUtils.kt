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

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.moblino.countrynews.R
import com.moblino.countrynews.features.saved.SavedNewsActivity

/**
 * Created by ftoptas on 26/02/17.
 */
object UIUtils {
    fun showSaveSnackBar(context: Context?, view: View?, saved: Boolean,
                         showAction: Boolean) {
        if (context == null || view == null) {
            return
        }
        val textRes = if (saved) R.string.text_fav_added else R.string.text_fav_removed
        var snackBar = Snackbar.make(view, textRes, Snackbar.LENGTH_SHORT)
        if (showAction) {
            snackBar = snackBar.setAction(R.string.title_saved_list) { context.startActivity(Intent(context, SavedNewsActivity::class.java)) }
                    .setActionTextColor(ContextCompat.getColor(context, R.color.colorAccent))
        }
        snackBar.show()
    }

    fun setNavMenuIcon(menuItem: MenuItem, categoryId: Int) {
        when (categoryId) {
            Constants.NAV_ITEM_EDIT -> menuItem.setIcon(R.drawable.nav_ic_edit_list)
            Constants.NAV_ITEM_SETTINGS -> menuItem.setIcon(R.drawable.nav_ic_settings)
            Constants.NAV_ITEM_SHARE -> menuItem.setIcon(R.drawable.nav_ic_share)
            Constants.NAV_ITEM_RATE -> menuItem.setIcon(R.drawable.nav_ic_rate)
            Constants.NAV_ITEM_FAVOURITES -> menuItem.setIcon(R.drawable.ic_favorite_black_24dp)
            Constants.NAV_ITEM_FIRST_PAGES -> menuItem.setIcon(R.drawable.nav_ic_headings)
            0 -> menuItem.setIcon(R.drawable.nav_ic_latest)
            1 -> menuItem.setIcon(R.drawable.nav_ic_sport)
            2 -> menuItem.setIcon(R.drawable.nav_ic_magazine)
            3 -> menuItem.setIcon(R.drawable.nav_ic_today)
            4 -> menuItem.setIcon(R.drawable.nav_ic_economy)
            7 -> menuItem.setIcon(R.drawable.nav_ic_world)
            8 -> menuItem.setIcon(R.drawable.nav_ic_local)
            5 -> menuItem.setIcon(R.drawable.nav_ic_politics)
            13 -> menuItem.setIcon(R.drawable.ic_android_black_24dp)
            16 -> menuItem.setIcon(R.drawable.nav_ic_photo)
            17 -> menuItem.setIcon(R.drawable.nav_ic_health)
            18 -> menuItem.setIcon(R.drawable.nav_ic_fun)
            19 -> menuItem.setIcon(R.drawable.nav_ic_video)
        }
    }
}