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

package com.moblino.countrynews.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

import com.moblino.countrynews.NewsApplication;
import com.moblino.countrynews.ext.ContextKt;
import com.moblino.countrynews.features.main.ExcludeMerger;


public class PreferenceWrapper {

    private static final String KEY_CATEGORY_ID = "category_id";
    private static final String KEY_READABILITY = "pref_readability";
    public static final String KEY_NIGHT_MODE = "pref_night_mode";
    public static final String KEY_FONT_SIZE = "pref_font_size";
    private static final String KEY_LOAD_IMAGES = "pref_load_images";
    private static final String KEY_COUNTRY = "pref_country";
    private static final String KEY_START_COUNT = "pref_start_count";
    private static final String KEY_APP_VERSION = "pref_app_version";
    private static final String KEY_LIST_ORDER = "pref_order";
    public static final String KEY_NEW_VERSION = "pref_new_version";
    private static final String KEY_STAGGERED_LAYOUT = "pref_staggered_layout";
    private static final String KEY_SHOW_DETAIL_FIRST = "pref_show_detail_first";
    private static final String KEY_REMOTE_EXCLUDED = "pref_remote_excluded";
    private static final String KEY_LAST_AD_SHOWN_DATE = "LAST_AD_SHOWN_DATE";
    private static final String KEY_NEWS_DETAIL_SHOWCASE = "NEWS_DETAIL_SHOWCASE";
    private static final String KEY_HEADING = "HEADING";


    // TODO: 16.05.2021 remove static instance
    private static final PreferenceWrapper instance = new PreferenceWrapper();
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    public static PreferenceWrapper getInstance() {
        return instance;
    }

    public void init(Context context) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mContext = context;
    }

    private PreferenceWrapper() {

    }

    public int readCategoryId() {
        return mSharedPreferences.getInt(KEY_CATEGORY_ID, 0);
    }

    public void writeCategoryId(int categoryId) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(KEY_CATEGORY_ID, categoryId);
        editor.apply();
    }

    @NonNull
    public String readCheckString() {
        int categoryId = NewsApplication.getInstance().getCurrentCategoryId();
        return readCheckString(categoryId);
    }

    public String readCheckString(int categoryId) {
        String local = mSharedPreferences.getString(readCountry() + "_" + categoryId, "");
        String remote = readRemoteExcluded();
        return ExcludeMerger.mergeLocalAndRemotes(categoryId, local == null ? "" : local, remote);
    }

    public void writeCheckString(String checkString) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(readCountry() + "_" + NewsApplication.getInstance().getCurrentCategoryId(), checkString);
        editor.apply();
    }

    public void writeCountry(String country) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_COUNTRY, country);
        editor.apply();
    }

    public void writeStartCount(int count) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(KEY_START_COUNT, count);
        editor.apply();
    }

    public boolean readGWLMode() {
        return mSharedPreferences.getBoolean(KEY_READABILITY, false);
    }

    public String readLoadImages() {
        return mSharedPreferences.getString(KEY_LOAD_IMAGES, "0");
    }

    @NonNull
    public String readCountry() {
        return mSharedPreferences.getString(KEY_COUNTRY, "");
    }

    public int readStartCount() {
        return mSharedPreferences.getInt(KEY_START_COUNT, 0);
    }

    public void writeAppVersion() {
        String appVersion = ContextKt.getAppVersion(mContext);
        if (appVersion != null) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(KEY_APP_VERSION, appVersion);
            editor.apply();
        }
    }

    public String readAppVersion() {
        return mSharedPreferences.getString(KEY_APP_VERSION, null);
    }


    @NonNull
    public String readListOrder() {
        return mSharedPreferences.getString(KEY_LIST_ORDER + "_" + readCountry() + "_" + NewsApplication.getInstance().getCurrentCategoryId(), "");
    }

    public void writeListOrder(String order) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_LIST_ORDER + "_" + readCountry() + "_" + NewsApplication.getInstance().getCurrentCategoryId(), order);
        editor.commit();
    }

    public void writeBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean readBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public void writeString(String key, String value) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String readString(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public void writeStaggeredLayout(boolean isStaggered) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(KEY_STAGGERED_LAYOUT, isStaggered);
        editor.commit();
    }

    public boolean readStaggered() {
        return mSharedPreferences.getBoolean(KEY_STAGGERED_LAYOUT, true);
    }

    public boolean readShowDetailFirst() {
        return mSharedPreferences.getBoolean(KEY_SHOW_DETAIL_FIRST, true);
    }

    public void writeRemoteExcludedNews(String excluded) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_REMOTE_EXCLUDED, excluded);
        editor.commit();
    }

    private String readRemoteExcluded() {
        return mSharedPreferences.getString(KEY_REMOTE_EXCLUDED, "");
    }


    public boolean readDetailShowcase() {
        return mSharedPreferences.getBoolean(KEY_NEWS_DETAIL_SHOWCASE, false);
    }

    public int readNightMode() {
        return Integer.valueOf(mSharedPreferences.getString(KEY_NIGHT_MODE, "1"));
    }

    public void writeNightMode() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_NIGHT_MODE, "2");
        editor.apply();
    }

    public int readFontSize() {
        return Integer.valueOf(mSharedPreferences.getString(KEY_FONT_SIZE, "1"));
    }

    public void writeHeadingUrl(String url) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(KEY_HEADING, url);
        editor.apply();
    }

    public String readHeadingUrl() {
        return mSharedPreferences.getString(KEY_HEADING, "");
    }

}
