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

package com.moblino.countrynews.features.main

import com.moblino.countrynews.parser.XMLParser
import com.moblino.countynews.common.model.RssItem
import com.moblino.countrynews.model.RssRequest
import com.moblino.countrynews.model.RssResponse
import com.moblino.countynews.common.DateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.SAXParserFactory

interface RssRepository {

    suspend fun fetchRss(req: RssRequest): RssResponse

}

class RssRepositoryImpl(private val okHttpClient: OkHttpClient) : RssRepository {

    override suspend fun fetchRss(req: RssRequest): RssResponse {
        return withContext(Dispatchers.IO) {
            val list = ArrayList<RssItem>()
            val url = req.url
            val encoding = req.encoding

            val request = Request.Builder()
                    .url(url)
                    .build()

            try {
                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful && response?.body() != null) {

                    val myXMLHandler = XMLParser()
                    val saxPF = SAXParserFactory.newInstance()
                    val saxP = saxPF.newSAXParser()
                    val xmlR = saxP.xmlReader

                    response.body()!!.apply {
                        val inputStream = byteStream()
                        val responseString = DateUtil.readInputStream(inputStream, encoding)
                                .replace("\"x", "\" x")
                                .replace("<title></title>", "") // for hurriyet.com.tr

                        xmlR.contentHandler = myXMLHandler
                        val inputSource = InputSource(StringReader(responseString))
                        inputSource.encoding = encoding
                        xmlR.parse(inputSource)
                        val items = myXMLHandler.items
                        if (items?.isNotEmpty() == true) {
                            list.addAll(items)
                            // status = ResponseStatus.SUCCESS // TODO:
                        }
                    }

                } else {
                    // status = ResponseStatus.NETWORK // TODO:
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // status = ResponseStatus.NETWORK // TODO:
            }

            list.sortByDescending { item ->
                DateUtil.fixDate(item.pubDate)
            }
            RssResponse(list, System.currentTimeMillis())
        }
    }


}