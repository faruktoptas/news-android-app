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

package com.moblino.countrynews.customviews;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.moblino.countrynews.R;
import com.moblino.countrynews.data.firebase.FirebaseManager;
import com.moblino.countrynews.data.firebase.RemoteConfigHelper;
import com.moblino.countrynews.ext.ContextKt;
import com.moblino.countrynews.features.editlist.EditFeedsActivity;
import com.moblino.countrynews.model.CardQuestion;
import com.moblino.countrynews.util.Constants;
import com.moblino.countrynews.util.PreferenceWrapper;

/**
 * Created by faruktoptas on 30/07/16.
 */
public class CardQuestionManager {
    public static final String ID_RATE_ME = "q_rate_me";
    public static final String ID_SORT_FEEDS = "q_sort_feeds";
    private static final String ID_NEW_VERSION = "q_new_version";
    private static final String ID_DARK_MODE = "q_dark_mode";

    private final RemoteConfigHelper remoteConfigHelper;


    public CardQuestionManager(RemoteConfigHelper remoteConfigHelper) {
        this.remoteConfigHelper = remoteConfigHelper;
    }

    @Nullable
    public CardQuestion nextCard(Context context) {
        CardQuestion newVersion = createNewVersionCard(context);
        CardQuestion rateMe = createRateMeCard(context);
        CardQuestion sortFeeds = createEditListCard(context);
        CardQuestion darkMode = createDarkModeCard(context);
        /*if (askForRss != null) {
            return askForRss;
        } else*/
        if (newVersion != null) {
            return newVersion;
        } else if (rateMe != null) {
            return rateMe;
        } else if (sortFeeds != null) {
            return sortFeeds;
        } /*else if (darkMode != null) {
            return darkMode; todo add dark mode card
        } */ else {
            return null;
        }
    }


    public CardQuestion createRateMeCard(Context context) {
        if (!PreferenceWrapper.getInstance().readBoolean(ID_RATE_ME) && PreferenceWrapper.getInstance().readStartCount() == 3) {
            return new CardQuestion(ID_RATE_ME, 0, false, context.getString(R.string.card_question_rate_me), R.drawable.ic_star_white_48dp,
                    context.getString(R.string.card_question_yes), context.getString(R.string.card_question_no));
        }
        return null;
    }

    public CardQuestion createEditListCard(Context context) {
        if (!PreferenceWrapper.getInstance().readBoolean(ID_SORT_FEEDS) && PreferenceWrapper.getInstance().readStartCount() == 4) {
            return new CardQuestion(ID_SORT_FEEDS, 0, false, context.getString(R.string.card_question_sort_feeds), R.drawable.ic_rss_feed_white_48dp,
                    context.getString(R.string.card_question_yes), context.getString(R.string.card_question_no));
        }
        return null;
    }

    public CardQuestion createDarkModeCard(Context context) {
        if (!PreferenceWrapper.getInstance().readBoolean(ID_DARK_MODE)
                && PreferenceWrapper.getInstance().readNightMode() == 1
                && PreferenceWrapper.getInstance().readStartCount() > 5) {
            return new CardQuestion(ID_DARK_MODE, 0, false, context.getString(R.string.card_question_dark_mode), R.drawable.ic_dark_mode_48,
                    context.getString(R.string.card_question_yes), context.getString(R.string.card_question_no));
        }
        return null;
    }

    public CardQuestion createNewVersionCard(Context context) {
        if (remoteConfigHelper.isNewVersionAvailable(context)) {
            return new CardQuestion(ID_NEW_VERSION, 0, false, context.getString(R.string.card_question_new_version), R.drawable.ic_update,
                    context.getString(R.string.card_question_yes), context.getString(R.string.card_question_no));
        }
        return null;

    }

    public void hideQuestion(String questionId) {
        PreferenceWrapper.getInstance().writeBoolean(questionId, true);
    }

    public void executeAction(Activity activity, CardQuestion cardQuestion) {
        if (activity == null || cardQuestion == null) {
            return;
        }
        switch (cardQuestion.getQuestionId()) {
            case ID_RATE_ME:
            case ID_NEW_VERSION:
                ContextKt.goToPlayStore(activity);
                break;
            case ID_SORT_FEEDS:
                Intent intentSort = new Intent(activity, EditFeedsActivity.class);
                activity.startActivityForResult(intentSort, Constants.REQ_CODE_LIST_CHANGED);
                break;
            case ID_DARK_MODE:
                PreferenceWrapper.getInstance().writeNightMode();
                hideQuestion(ID_DARK_MODE);
                activity.recreate();
                FirebaseManager.getInstance().setUserPropertyNightMode(PreferenceWrapper.getInstance().readNightMode());
                break;
        }
    }
}
