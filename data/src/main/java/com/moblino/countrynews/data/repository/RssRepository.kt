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

package com.moblino.countrynews.data.repository

import com.moblino.countrynews.parser.XMLParser
import com.moblino.countynews.common.model.RssItem
import com.moblino.countynews.common.model.RssRequest
import com.moblino.countynews.common.model.RssResponse
import com.moblino.countynews.common.DateUtil
import com.moblino.countynews.common.model.RssError
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
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    val parser = XMLParser()
                    val factory = SAXParserFactory.newInstance()
                    val saxParser = factory.newSAXParser()
                    val reader = saxParser.xmlReader
                    val inputStream = body.byteStream()
                    val responseString = DateUtil.readInputStream(inputStream, encoding)
                            .replace("\"x", "\" x")
                            .replace("<title></title>", "") // for hurriyet.com.tr

                    reader.contentHandler = parser
                    val inputSource = InputSource(StringReader(responseString))
                    inputSource.encoding = encoding
                    reader.parse(inputSource)
                    val items = parser.items
                    if (items?.isNotEmpty() == true) {
                        list.addAll(items)
                        list.sortByDescending { item ->
                            DateUtil.fixDate(item.pubDate)
                        }
                        RssResponse.Success(list, System.currentTimeMillis())
                    } else {
                        RssResponse.Fail(RssError.EmptyList)
                    }
                } else {
                    RssResponse.Fail(RssError.Network)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                RssResponse.Fail(RssError.Network)
            }
        }
    }
}
