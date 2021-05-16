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

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.moblino.countrynews.R
import com.moblino.countrynews.data.firebase.FirebaseManager
import com.moblino.countrynews.data.firebase.RemoteConfigHelper
import com.moblino.countynews.common.ext.goToPlayStore
import com.moblino.countrynews.features.editlist.EditFeedsActivity
import com.moblino.countynews.common.PreferenceWrapper
import com.moblino.countynews.common.model.CardQuestion

/**
 * Created by faruktoptas on 30/07/16.
 */
class CardQuestionHelper(private val remoteConfigHelper: RemoteConfigHelper, private val pref: PreferenceWrapper) {


    fun nextCard(context: Context): CardQuestion? {
        val newVersion = createNewVersionCard(context)
        val rateMe = createRateMeCard(context)
        val sortFeeds = createEditListCard(context)
        val darkMode = createDarkModeCard(context)
        /*if (askForRss != null) {
            return askForRss;
        } else*/return newVersion ?: (rateMe ?: sortFeeds)
    }

    private fun createRateMeCard(context: Context): CardQuestion? {
        return if (!pref.readBoolean(ID_RATE_ME) && pref.readStartCount() == 3) {
            CardQuestion(
                    ID_RATE_ME,
                    context.getString(R.string.card_question_rate_me),
                    R.drawable.ic_star_white_48dp,
                    context.getString(R.string.card_question_yes),
                    context.getString(R.string.card_question_no))
        } else null
    }

    private fun createEditListCard(context: Context): CardQuestion? {
        return if (!pref.readBoolean(ID_SORT_FEEDS) && pref.readStartCount() == 4) {
            CardQuestion(
                    ID_SORT_FEEDS,
                    context.getString(R.string.card_question_sort_feeds),
                    R.drawable.ic_rss_feed_white_48dp,
                    context.getString(R.string.card_question_yes),
                    context.getString(R.string.card_question_no))
        } else null
    }

    private fun createDarkModeCard(context: Context): CardQuestion? {
        return if (!pref.readBoolean(ID_DARK_MODE)
                && pref.readNightMode() == 1 && pref.readStartCount() > 5) {
            CardQuestion(ID_DARK_MODE,
                    context.getString(R.string.card_question_dark_mode),
                    R.drawable.ic_dark_mode_48,
                    context.getString(R.string.card_question_yes),
                    context.getString(R.string.card_question_no))
        } else null
    }

    private fun createNewVersionCard(context: Context): CardQuestion? {
        return if (remoteConfigHelper.isNewVersionAvailable(context)) {
            CardQuestion(ID_NEW_VERSION,
                    context.getString(R.string.card_question_new_version),
                    R.drawable.ic_update,
                    context.getString(R.string.card_question_yes),
                    context.getString(R.string.card_question_no))
        } else null
    }

    fun hideQuestion(questionId: String?) {
        pref.writeBoolean(questionId, true)
    }

    fun executeAction(activity: Activity?, cardQuestion: CardQuestion?) {
        if (activity == null || cardQuestion == null) {
            return
        }
        when (cardQuestion.questionId) {
            ID_RATE_ME, ID_NEW_VERSION -> activity.goToPlayStore()
            ID_SORT_FEEDS -> {
                val intentSort = Intent(activity, EditFeedsActivity::class.java)
                activity.startActivityForResult(intentSort, Constants.REQ_CODE_LIST_CHANGED)
            }
            ID_DARK_MODE -> {
                pref.writeNightMode()
                hideQuestion(ID_DARK_MODE)
                activity.recreate()
                FirebaseManager.getInstance().setUserPropertyNightMode(pref.readNightMode())
            }
        }
    }

    companion object {
        const val ID_RATE_ME = "q_rate_me"
        const val ID_SORT_FEEDS = "q_sort_feeds"
        private const val ID_NEW_VERSION = "q_new_version"
        private const val ID_DARK_MODE = "q_dark_mode"
    }
}