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

package com.moblino.countrynews.data.firebase;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by ftoptas on 29/12/17.
 */

public class FirebaseManager {

    private static final String USER_PROPERTY_LINEAR_LISTING = "linear_listing";
    private static final String USER_PROPERTY_GWL = "gwl";
    private static final String USER_PROPERTY_DISABLE_SUMMARY = "disable_summary";
    private static final String USER_PROPERTY_NIGHT_MODE = "night_mode";

    private static final String EVENT_NEWS_BROWSE = "news_browse";
    public static final String EVENT_NEWS_DETAIL = "news_detail";
    public static final String EVENT_HEADLINES = "headlines";
    public static final String EVENT_SORT = "sort";
    public static final String EVENT_FAVOURITE = "favourite";
    private static final String EVENT_SHARE = "share";
    private static final String EVENT_RATE = "rate";
    private static final String EVENT_CATEGORY_CHANGE = "category_change";
    private static final String PARAM_CATEGORY = "category";

    private final static FirebaseManager MANAGER = new FirebaseManager();
    private FirebaseAnalytics mAnalytics;

    private FirebaseManager() {
    }

    public static FirebaseManager getInstance() {
        return MANAGER;
    }

    public void init(Context context) {
        mAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void logCategoryChange(String category) {
        Bundle params = new Bundle();
        params.putString(PARAM_CATEGORY, category);
        mAnalytics.logEvent(EVENT_CATEGORY_CHANGE, params);
        Log.v("asd", "category: " + category);
    }

    public void logNewsBrowse() {
        logEvent(EVENT_NEWS_BROWSE);
    }

    public void logSort() {
        logEvent(EVENT_SORT);
    }

    public void logFavourite() {
        logEvent(EVENT_FAVOURITE);
    }

    public void logShare() {
        logEvent(EVENT_SHARE);
    }

    public void logRate() {
        logEvent(EVENT_RATE);
    }

    public void logHeadlines() {
        logEvent(EVENT_HEADLINES);
    }

    public void logEvent(String event) {
        mAnalytics.logEvent(event, null);
        Log.i("dsa", event);
    }

    public void setUserPropertyListingType(boolean isLinear) {
        mAnalytics.setUserProperty(USER_PROPERTY_LINEAR_LISTING, String.valueOf(isLinear));
    }

    public void setUserPropertyGWL(boolean isEnabled) {
        mAnalytics.setUserProperty(USER_PROPERTY_GWL, String.valueOf(isEnabled));
    }

    public void setUserPropertyDisableSummary(boolean isDisabled) {
        mAnalytics.setUserProperty(USER_PROPERTY_DISABLE_SUMMARY, String.valueOf(isDisabled));
    }

    public void setUserPropertyNightMode(int mode) {
        String modeString = "off";
        switch (mode) {
            case AppCompatDelegate.MODE_NIGHT_AUTO:
                modeString = "auto";
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                modeString = "off";
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                modeString = "on";
                break;

        }
        mAnalytics.setUserProperty(USER_PROPERTY_NIGHT_MODE, modeString);
    }
}
