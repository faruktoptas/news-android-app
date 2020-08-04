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

package com.moblino.countrynews.data.network;

import android.os.AsyncTask;
import androidx.annotation.Nullable;
import android.util.Log;

import com.moblino.countrynews.models.RssItem;
import com.moblino.countrynews.data.XMLParser;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RssHttpRequest extends AsyncTask<String, Integer, ArrayList<RssItem>> {

    private final RssResponseListener mRssResponseListener;
    private final String mRssUrl;

    public RssHttpRequest(RssResponseListener rssResponseListener, String rssUrl) {
        mRssResponseListener = rssResponseListener;
        mRssUrl = rssUrl;
    }

    private String mEncoding;


    @Override
    protected ArrayList<RssItem> doInBackground(String... params) {
        ArrayList<RssItem> list = null;
        InputStream inputStream = null;
        try {
            java.net.URL url = new URL(mRssUrl);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.addRequestProperty("User-Agent", "");

            httpURLConnection.connect();
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200 || responseCode == 302) {
                list = new ArrayList<>();
                inputStream = httpURLConnection.getInputStream();
                String responseString = readInputStream(inputStream, mEncoding);
                XMLParser myXMLHandler = new XMLParser();
                SAXParserFactory saxPF = SAXParserFactory.newInstance();
                SAXParser saxP = saxPF.newSAXParser();
                XMLReader xmlR = saxP.getXMLReader();

                /**
                 * Create the Handler to handle each of the XML tags.
                 **/
                xmlR.setContentHandler(myXMLHandler);

                InputSource inputSource = new InputSource(new StringReader(responseString));
                if (mEncoding != null) {
                    inputSource.setEncoding(mEncoding);
                }
                xmlR.parse(inputSource);
                ArrayList<RssItem> items = myXMLHandler.getItems();
                list.addAll(items);
                Log.v("asd", "parsed: " + items.size());

                return list;
            } else {
                inputStream = httpURLConnection.getErrorStream();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }


    @Override
    protected void onPostExecute(ArrayList<RssItem> rssItems) {
        super.onPostExecute(rssItems);
        if (rssItems != null) {
            mRssResponseListener.onResponse(rssItems, ResponseStatus.SUCCESS, mRssUrl);
        } else {
            mRssResponseListener.onResponse(null, ResponseStatus.FAIL, mRssUrl);
        }
    }

    public void setEncoding(String encoding) {
        mEncoding = encoding;
    }

    private String readInputStream(InputStream stream, @Nullable String encoding) throws IOException {
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