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

package com.moblino.countynews.common;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static final Locale locale = new Locale("en_US");

    public static Date fixDate(String d) {
        Date date = null;
        String[] formats = {"EEE, dd MMM yyyy HH:mm:ss zzz",
                "EEE, dd MMM yyyy HH:mm:ss", "yyyy-MMM-dd HH:mm:ss zzz"};
        boolean done = false;
        for (String format : formats) {
            if (!done) {
                try {
                    DateFormat formatter = new SimpleDateFormat(format,
                            locale);
                    date = formatter.parse(d);
                    done = true;
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }
        }
        return date;
    }


    public static String readInputStream(InputStream stream, @Nullable String encoding) throws IOException {
        BufferedReader reader;
        if (encoding != null) {
            reader = new BufferedReader(new InputStreamReader(stream, encoding));
        } else {
            reader = new BufferedReader(new InputStreamReader(stream));
        }

        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

}
