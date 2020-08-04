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

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.moblino.countrynews.ext.ExtensionsKt;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by faruktoptas on 30/10/15.
 */
public class MOUtils {

    @Nullable
    public static String readFromAssets(AssetManager assetManager, String fileName) {
        String ret = null;
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(assetManager.open(fileName)));
            String mLine;
            StringBuilder builder = new StringBuilder();
            while ((mLine = reader.readLine()) != null) {
                builder.append(mLine);
            }
            ret = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static boolean checkGooglePlayAvailable(Context context, Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int result = googleApiAvailability.isGooglePlayServicesAvailable(context);
        if (result != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(result)) {
                googleApiAvailability.getErrorDialog(activity, result, 0).show();
            }
        }

        return result == ConnectionResult.SUCCESS;
    }

    public static String getDeviceManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getDeviceModel() {
        return Build.MODEL;

    }

    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public static Object[] sortList(@NotNull List<String> files) {
        Collections.sort(files, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                int first = ExtensionsKt.toNumberOrZero(s.substring(0, s.indexOf(".")));
                int second = ExtensionsKt.toNumberOrZero(t1.substring(0, t1.indexOf(".")));

                return first - second;
            }
        });
        return files.toArray();
    }
}
