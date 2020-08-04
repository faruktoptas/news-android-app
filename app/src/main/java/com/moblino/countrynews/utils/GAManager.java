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

package com.moblino.countrynews.utils;

import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.moblino.countrynews.BuildConfig;
import com.moblino.countrynews.NewsApplication;

public class GAManager {

    public static final String ACTION_FAV = "AddToFavourite";
    public static final String ACTION_SORT = "Sort";
    public static final String ACTION_RSS_REQUEST = "RSSRequest";
    public static final String ACTION_APP_SHARE = "AppShare";
    public static final String ACTION_APP_RATE = "AppRate";

    public static void sendScreen(String screenName) {
        if (!BuildConfig.DEBUG) {
            /*NewsApplication.getInstance().getDefaultTracker().setScreenName(screenName);
            NewsApplication.getInstance().getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());*/
            // TODO: 16.07.2020 use firebase
        } else {
            Log.v("asd","GA sendScreen: " + screenName);
        }
    }

    public static void sendEvent(String actionName) {
        if (!BuildConfig.DEBUG) {
            //NewsApplication.getInstance().getDefaultTracker().send(new HitBuilders.EventBuilder("Action", actionName).build());
            // TODO: 16.07.2020 use firebase
        } else {
            Log.v("asd", "GA send click event: " + actionName);
        }
    }
}
