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

package com.moblino.countrynews.features.settings;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.moblino.countrynews.R;
import com.moblino.countrynews.base.BaseActivity;
import com.moblino.countynews.common.model.FeedItem;
import com.moblino.countrynews.parser.OkHttpRssRequest;
import com.moblino.countrynews.parser.ResponseStatus;
import com.moblino.countrynews.parser.RssResponseListener;
import com.moblino.countynews.common.model.RssItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ftoptas on 15/01/17.
 */

public class HealthCheckActivity extends BaseActivity implements RssResponseListener {

    TextView mTextViewTotal;

    TextView mTextViewFail;

    TextView mTextViewFailList;

    TextView mTextViewCountryList;

    private int mTotalCount;
    private int mSuccessCount = 0;
    private int mFailCount = 0;
    private String mFailList = "";
    private String mCountryList = "";
    private final List<FeedItem> mFullList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_health_check);
        mTextViewTotal = findViewById(R.id.tvTotal);
        mTextViewFail = findViewById(R.id.tvFail);
        mTextViewFailList = findViewById(R.id.tvFailList);
        mTextViewCountryList = findViewById(R.id.tvCountryList);

        fillList();
        fire();
    }

    private void fire() {
        mTotalCount = mFullList.size();
        for (int i = 0; i < mTotalCount; i++) {
            FeedItem feedItem = mFullList.get(i);
            OkHttpRssRequest request = new OkHttpRssRequest(this);
            //RssHttpRequest rssHttpRequest = new RssHttpRequest(this, feedItem.getFeedUrl());
            if (feedItem.getEncoding() != null) {
                //rssHttpRequest.setEncoding(feedItem.getEncoding());
                request.setMEncoding(feedItem.getEncoding());
            }
            //rssHttpRequest.execute(feedItem.getFeedUrl());*/

            request.execute(feedItem.getFeedUrl());
        }
        update();
    }

    private void fillList() {
        mFullList.clear();
        try {
            String[] countryList = getAssets().list("");

            for (String country : countryList) {
                if (country.length() == 2) {
                    addCountry(country);
                }
            }
            Log.v("asd", "fullList size: " + mFullList.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addCountry(String country) throws IOException {
        String[] items = getAssets().list(country);
        /*int countryItemCount = 0;
        for (String item : items) {
            String jsonFile = item.substring(0, item.length() - 5);
            if (jsonFile.length() < 3) {
                String feedListJson = MOUtils.readFromAssets(getAssets(), country + "/" + item);
                final FeedItem[] feedItems = new Gson().fromJson(feedListJson, FeedItem[].class);
                for (FeedItem feedItem : feedItems) {
                    mFullList.add(feedItem);
                    countryItemCount++;
                }
            }
        }
        mCountryList = mCountryList + country + " " + countryItemCount + "\n";*/
        mTextViewCountryList.setText(mCountryList);
    }

    @Override
    public void onResponse(ArrayList<RssItem> newsList, ResponseStatus status, String rssUrl) {
        if (status.equals(ResponseStatus.SUCCESS) && newsList != null && newsList.size() > 0) {
            mSuccessCount++;
        } else {
            mFailCount++;
            mFailList = mFailList + "\n" + rssUrl;
            Log.e("failed", rssUrl);
        }
        update();
    }

    private void update() {
        mTextViewTotal.setText(mSuccessCount + " / " + mTotalCount);
        mTextViewFail.setText(String.valueOf(mFailCount));
        mTextViewFailList.setText(mFailList);
    }
}
