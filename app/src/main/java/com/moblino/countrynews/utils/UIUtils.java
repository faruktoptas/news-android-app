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

import android.content.Context;
import android.content.Intent;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import android.view.View;

import com.moblino.countrynews.R;
import com.moblino.countrynews.features.saved.SavedNewsActivity;

/**
 * Created by ftoptas on 26/02/17.
 */

public class UIUtils {

    public static void showSaveSnackBar(final Context context, View view, boolean saved,
                                        boolean showAction) {
        if (context == null || view == null) {
            return;
        }
        int textRes = saved ? R.string.text_fav_added : R.string.text_fav_removed;
        Snackbar snackbar = Snackbar.make(view, textRes, Snackbar.LENGTH_SHORT);
        if (showAction) {
            snackbar = snackbar.setAction(R.string.title_saved_list, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, SavedNewsActivity.class));
                }
            })
                    .setActionTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        }
        snackbar.show();

    }
}
