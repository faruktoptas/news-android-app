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

import com.moblino.countrynews.ext.ExtensionsKt;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by faruktoptas on 30/10/15.
 */
public class ListUtil {

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
