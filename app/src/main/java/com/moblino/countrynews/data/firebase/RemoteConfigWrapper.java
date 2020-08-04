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
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.moblino.countrynews.BuildConfig;
import com.moblino.countrynews.ext.ContextKt;
import com.moblino.countrynews.util.PreferenceWrapper;

import java.util.HashMap;

/**
 * Created by faruktoptas on 21/08/16.
 */
public class RemoteConfigWrapper {

    public static final String USER_PROP_READABILITY = "ReadabilityEnabled";
    public static final String VALUE_LATEST_VERSION = "latest_version";
    public static final String VALUE_EXCLUDED_NEWS = "excluded_news";
    private static final String VALUE_AD_STATE = "ad_state";
    private static final String VALUE_HEADING_URL = "heading_url";

    private static RemoteConfigWrapper ourInstance = new RemoteConfigWrapper();

    public static RemoteConfigWrapper getInstance() {
        return ourInstance;
    }

    private RemoteConfigWrapper() {
    }

    public void init(final Context context) {
        final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put(VALUE_LATEST_VERSION, ContextKt.getAppVersion(context.getApplicationContext()));
        defaults.put(VALUE_AD_STATE, "0");
        mFirebaseRemoteConfig.setDefaults(defaults);
        mFirebaseRemoteConfig.fetch(0)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mFirebaseRemoteConfig.activateFetched();
                        String latestVersion = mFirebaseRemoteConfig.getString(VALUE_LATEST_VERSION);
                        String excludedNews = mFirebaseRemoteConfig.getString(VALUE_EXCLUDED_NEWS);
                        String adState = mFirebaseRemoteConfig.getString(VALUE_AD_STATE);
                        String headingUrl = mFirebaseRemoteConfig.getString(VALUE_HEADING_URL);

                        PreferenceWrapper.getInstance().writeRemoteExcludedNews(excludedNews);
                        if (latestVersion != null && latestVersion.length() > 0) {
                            PreferenceWrapper.getInstance().writeString(PreferenceWrapper.KEY_NEW_VERSION, latestVersion);
                            Log.v("asd", latestVersion);
                        }
                        PreferenceWrapper.getInstance().writeHeadingUrl(headingUrl);

                        /*if (excludedNews.length() > 0){
                            listener.onRemoteConfigChanged();
                        }*/
                    }
                });
    }

    public boolean isNewVersionAvailable(Context context) {
        String latestVersion = PreferenceWrapper.getInstance().readString(PreferenceWrapper.KEY_NEW_VERSION);
        String currentVersion = ContextKt.getAppVersion(context);
        currentVersion = currentVersion.replace(".", "");
        String latestVersionWithoutDot = latestVersion.replace(".", "");
        if (latestVersionWithoutDot.length() > 0) {
            int current = Integer.parseInt(currentVersion);
            int latest = Integer.parseInt(latestVersionWithoutDot);
            return latest > current;
        }
        return false;
    }

}
