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
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import androidx.annotation.DrawableRes;

import com.moblino.countrynews.R;
import com.moblino.countrynews.features.main.MainActivity;
import com.moblino.countrynews.model.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ftoptas on 15/12/16.
 */

public class NewsShortcuts {
    public static void setShortcuts(Context context, List<Category> categoryList) {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            if (categoryList.size() > 0) {
                List<ShortcutInfo> shortcutInfoList = new ArrayList<>();
                int counter = 0;
                for (Category category : categoryList) {
                    if (counter < 4) {
                        counter++;
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.putExtra(Constants.EXTRA_CURRENT_CATEGORY, category.getId());
                        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(context, "id" + category.getId())
                                .setShortLabel(category.getTitle())
                                .setLongLabel(category.getTitle())
                                .setIcon(Icon.createWithResource(context, getIconById(category.getId())))
                                .setIntent(intent)
                                .build();
                        shortcutInfoList.add(shortcutInfo);
                    }
                }
                ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
                Collections.reverse(shortcutInfoList);
                shortcutManager.removeAllDynamicShortcuts();
                shortcutManager.setDynamicShortcuts(shortcutInfoList);
            }
        }
    }

    @DrawableRes
    private static int getIconById(Integer id) {
        switch (id) {
            case 0:
                return R.drawable.ic_shortcut_latest;
            case 1:
                return R.drawable.ic_shortcut_sport;
            case 2:
                return R.drawable.ic_shortcut_ent;
            case 3:
                return R.drawable.ic_shortcut_gundem;
            case 4:
                return R.drawable.ic_shortcut_pie;
        }
        return R.drawable.ic_shortcut_latest;
    }


}
