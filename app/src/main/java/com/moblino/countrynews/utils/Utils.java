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

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;

import com.moblino.countrynews.NewsApplication;
import com.moblino.countrynews.R;
import com.moblino.countrynews.models.LoadImageState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final Locale locale = new Locale("en_US");
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

    public static String getImgSrc(String str) {
        String startString = "src";
        String endString = "\n";
        if (!str.isEmpty() && str.contains(startString)) {
            int start = str.indexOf(startString);
            int end = str.indexOf(endString, start + startString.length() + 2);
            Log.v("asd", "start: " + start + " end: " + end + " str:--> " + str);
            return str.substring(start + startString.length() + 2, end - 4);
        }
        return "";
    }


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
//		if (!done) {
//			//date = new Date(manualFixDate(d).getTimeInMillis());
//		}
        return date;
    }

    public static String convertDate(Context context, String longDate) {
        Date pubdate = null;
        if (longDate != null) {
            pubdate = Utils.fixDate(longDate);
        }
        if (pubdate != null) {
            Date now = new Date(System.currentTimeMillis());
            return Utils.datediff(context, pubdate, now);
        } else if (longDate != null) {
            return longDate;
        }
        return longDate;
    }

    public static boolean isLoadImagesEnabled(Context context) {
        LoadImageState state = CNUtils.getLoadImages(PreferenceWrapper.getInstance().readLoadImages());
        switch (state) {
            case WIFI:
                return Connectivity.isConnectedWifi(context);
            case NEVER:
                return false;
            default:
                return true;
        }
    }

    public static Calendar manualFixDate(String date) {
        Calendar ret = null;
        if (date != null && date.length() >= 18) {
            String[] parts = date.split(" ");
            if (parts.length > 1) {
                String dateParts[] = parts[0].split("-");
                if (dateParts.length < 3) {
                    dateParts = parts[0].split(".");
                }
                String timeParts[] = parts[1].split(":");
                if (dateParts.length == 3 & timeParts.length == 3) {
                    Calendar c = Calendar.getInstance(locale);
                    c.set(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]), Integer.parseInt(dateParts[2]), Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]), Integer.parseInt(timeParts[2]));
                    ret = c;
                }
            }
        }
        return ret;
    }

    private static String datediff(Context context, Date dStart, Date dEnd) {
        long val = 0;
        String suff = "";
        if (dStart == null || dEnd == null) {
            return null;
        } else {
            long diff = dEnd.getTime() - dStart.getTime();
            long mins = diff / 60000;
            long hours = mins / 60;
            long days = hours / 24;

            if (days > 0) {
                val = days;
                suff = context.getResources().getString(R.string.date_days_ago, val);
            } else if (hours > 0) {
                val = hours;
                suff = context.getResources().getString(R.string.date_hours_ago, val);
            } else if (mins > 0) {
                val = mins;
                suff = context.getResources().getString(R.string.date_mins_ago, val);
            }
        }
        if (suff.isEmpty()) {
            return null;
        }
        return suff;
    }

    public static String getMd5(String input) throws NoSuchAlgorithmException {
        if (input != null) {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] digest = md.digest();
            return convertByteToHex(digest);
        }
        return null;

    }

    private static String convertByteToHex(byte[] byteData) {
        StringBuilder sb = new StringBuilder();
        for (byte aByteData : byteData) {
            sb.append(Integer.toString((aByteData & 0xff) + 0x100, 16)
                    .substring(1));
        }

        return sb.toString();
    }

    public static String getString(Context context, int id) {
        return context.getResources().getString(id);
    }

    public static String convertDate(Date date, Locale locale) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
        return format.format(date);
    }


    public static boolean isNetworkConnected(Context context) {
        if (context == null) {
            return true;
        }
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    public static int adWillBeShown() {
        int startCount = PreferenceWrapper.getInstance().readStartCount();
        // ad position
        int adPosition = new Random().nextInt(4) + 2;
        int adShowPossibility = new Random().nextInt(100);
        // show ad after second start
        if (startCount < 2) {
            adPosition = -1;
        } // %50 possibility to show ad
        if (adShowPossibility % 2 == 1) {
            adPosition = -1;
        }
        return adPosition;
    }

    public static int isFavourited(String url) {
        for (int i = 0; i < NewsApplication.getInstance().getAppCache().getFavoriteList().size(); i++) {
            if (NewsApplication.getInstance().getAppCache().getFavoriteList().get(i).getLink().equals(url)) {
                return i;
            }

        }
        return -1;
    }

    public static void goToPlayStore(Context context) {
        final String appPackageName = context.getPackageName();
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void setNavMenuIcon(MenuItem menuItem, int categoryId) {
        switch (categoryId) {
            case Constants.NAV_ITEM_EDIT:
                menuItem.setIcon(R.drawable.nav_ic_edit_list);
                break;
            case Constants.NAV_ITEM_SETTINGS:
                menuItem.setIcon(R.drawable.nav_ic_settings);
                break;
            case Constants.NAV_ITEM_SHARE:
                menuItem.setIcon(R.drawable.nav_ic_share);
                break;
            case Constants.NAV_ITEM_RATE:
                menuItem.setIcon(R.drawable.nav_ic_rate);
                break;
            case Constants.NAV_ITEM_FAVOURITES:
                menuItem.setIcon(R.drawable.ic_favorite_black_24dp);
                break;
            case Constants.NAV_ITEM_FIRST_PAGES:
                menuItem.setIcon(R.drawable.nav_ic_headings);
                break;
            case 0:
                menuItem.setIcon(R.drawable.nav_ic_latest);
                break;
            case 1:
                menuItem.setIcon(R.drawable.nav_ic_sport);
                break;
            case 2:
                menuItem.setIcon(R.drawable.nav_ic_magazine);
                break;
            case 3:
                menuItem.setIcon(R.drawable.nav_ic_today);
                break;
            case 4:
                menuItem.setIcon(R.drawable.nav_ic_economy);
                break;
            case 7:
                menuItem.setIcon(R.drawable.nav_ic_world);
                break;
            case 8:
                menuItem.setIcon(R.drawable.nav_ic_local);
                break;
            case 5:
                menuItem.setIcon(R.drawable.nav_ic_politics);
                break;
            case 13:
                menuItem.setIcon(R.drawable.ic_android_black_24dp);
                break;
            case 16:
                menuItem.setIcon(R.drawable.nav_ic_photo);
                break;
            case 17:
                menuItem.setIcon(R.drawable.nav_ic_health);
                break;
            case 18:
                menuItem.setIcon(R.drawable.nav_ic_fun);
                break;
            case 19:
                menuItem.setIcon(R.drawable.nav_ic_video);
                break;
        }
    }

    private static String formatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyy");
        return simpleDateFormat.format(date);
    }

    public static String todayString() {
        return formatDate(Calendar.getInstance().getTime());
    }

    public static String parseHtml(String detail) {
        if (detail != null) {
            detail = detail.replaceAll("<img .*?</img>", "");
            detail = detail.replaceAll("<img .*?/>", "");
            detail = detail.replaceAll("<IMG .*?/>", "");
            detail = detail.replaceAll("<a .*?a/>", "");
            return Html.fromHtml(detail).toString();
        }
        return null;
    }

    public static String fixImageUrl(String string) {
        if (string.startsWith("//")) {
            return "https:" + string;
        }
        return string;
    }

    public static String cleanDescription(String desc) {
        if (desc != null && desc.contains("&lt;img")) {
            int pos = desc.indexOf("/&gt;");
            if (pos > -1) {
                return desc.substring(pos + 5, desc.length());
            }
        }
        return desc;
    }

    public static String getImageSourceFromDescription(String description) {
        if (description.contains("img") && description.contains("src")) {
            String[] parts = description.split("src=\"");
            if (parts.length >= 2 && parts[1].length() > 0) {
                String src = parts[1].substring(0, parts[1].indexOf("\""));
                String[] srcParts = src.split("http"); // milliyet bozuk link icerdigi icin bu kod milliyet icin yazildi
                if (srcParts.length > 2) {
                    src = "http" + srcParts[2];
                }
                return src;
            }
        }
        return null;
    }

    public static String optimizeImage(String imageUrl, String rssLink) {
        if (imageUrl != null) {
            if (imageUrl.startsWith("file://")) {
                String replacement;
                if (rssLink != null && rssLink.contains("://")) {
                    replacement = rssLink.substring(0, rssLink.indexOf("://") + 3);
                } else {
                    replacement = "http://";
                }
                return imageUrl.replace("file://", replacement);
            }
            if (imageUrl.contains("http")) {
                return imageUrl;
            }
        }
        return null;
    }

    public static boolean isNightMode(Resources resources) {
        int currentNightMode = resources.getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're in day time
                return false;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're at night!
                return true;
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                // We don't know what mode we're in, assume notnight
                return false;
        }
        return false;
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

    public static String fixDescription(String description) {
        String fixed = "";
        if (description != null) {
            fixed = description
                    .replace("&lt;", "<")
                    .replace("/&gt;", ">")
                    .replaceAll("<img.+?>", "")
                    .replaceAll("<IMG.+?>", "")
                    .trim();
        }
        return fixed;
    }
}
