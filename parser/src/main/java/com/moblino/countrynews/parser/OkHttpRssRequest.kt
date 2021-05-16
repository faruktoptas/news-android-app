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

package com.moblino.countrynews.parser

import android.os.AsyncTask

import com.moblino.countynews.common.DateUtil
import com.moblino.countynews.common.model.RssItem
import okhttp3.Request
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.SAXParserFactory

// TODO: Still AsyncTask OMG
class OkHttpRssRequest(private val listener: RssResponseListener) :
        AsyncTask<String, Void, ArrayList<RssItem>>() {

    private val client = NetworkManager.getInstance().client
    var mEncoding: String? = "utf-8"
    private var status: ResponseStatus = ResponseStatus.FAIL
    private lateinit var url: String

    override fun doInBackground(vararg params: String?): ArrayList<RssItem> {
        val list = ArrayList<RssItem>()
        url = params[0]!!

        val request = Request.Builder()
                .url(url)
                .build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                if (response?.body() != null) {
                    val myXMLHandler = XMLParser()
                    val saxPF = SAXParserFactory.newInstance()
                    val saxP = saxPF.newSAXParser()
                    val xmlR = saxP.xmlReader

                    response.body()?.apply {
                        val inputStream = byteStream()
                        val responseString = DateUtil.readInputStream(inputStream, mEncoding)
                                .replace("\"x","\" x")
                                .replace("<title></title>","") // for hurriyet.com.tr

                        xmlR.contentHandler = myXMLHandler
                        val inputSource = InputSource(StringReader(responseString))
                        inputSource.encoding = mEncoding
                        xmlR.parse(inputSource)
                        val items = myXMLHandler.items
                        if (items?.isNotEmpty() == true) {
                            list.addAll(items)
                            status = ResponseStatus.SUCCESS
                        }
                    }
                }
            } else {
                status = ResponseStatus.NETWORK
            }
        } catch (e: Exception) {
            e.printStackTrace()
            status = ResponseStatus.NETWORK
        }
        return list
    }

    override fun onPostExecute(result: ArrayList<RssItem>) {
        super.onPostExecute(result)
        result.sortByDescending{ item ->
            DateUtil.fixDate(item.pubDate)
        }
        listener.onResponse(result, status, url)
    }
}