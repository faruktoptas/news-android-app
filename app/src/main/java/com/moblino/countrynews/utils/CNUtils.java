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

import android.support.annotation.NonNull;
import android.util.Log;

import com.moblino.countrynews.BuildConfig;
import com.moblino.countrynews.models.Category;
import com.moblino.countrynews.models.LoadImageState;

/**
 * Created by faruktoptas on 30/10/15.
 */
public class CNUtils {
    private static final java.lang.String TAG = "asd";

    public static boolean arrayContains(String[] list, String search){
        for (String item: list){
            if (search.equals(item) || search.toLowerCase().equals(item.toLowerCase())){
                return true;
            }
        }
        return false;
    }


    public static void logi(String tag, String msg){
        if (BuildConfig.DEBUG){
            Log.i(tag, msg);
        }
    }

    public static void logi(String msg){
        logi(TAG, msg);
    }

    @NonNull
    public static LoadImageState getLoadImages(String s) {
        if (s.equals("0")){
            return LoadImageState.ALWAYS;
        }else if (s.equals("1")){
            return LoadImageState.WIFI;
        }else if (s.equals("2")){
            return LoadImageState.NEVER;
        }
        return LoadImageState.WIFI;
    }
}
