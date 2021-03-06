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

import android.content.res.AssetManager
import com.google.gson.Gson
import com.moblino.countrynews.NewsApplication
import com.moblino.countrynews.ext.readFile
import com.moblino.countynews.common.model.Category
import com.moblino.countynews.common.model.FeedItem
import com.moblino.countrynews.util.Constants

interface MainRepository {

    fun countryList(): List<String>

    fun categoriesByCountry(country: String): List<Category>

    fun getCountryFiles(country: String): Array<out String>

    fun feedsByCategory(country: String, categoryId: Int): List<FeedItem>

}

class MainRepositoryImpl(private val assets: AssetManager, private val gson: Gson) : MainRepository {


    override fun countryList(): List<String> = assets.list("")!!.toList()

    override fun categoriesByCountry(country: String): List<Category> {

        val json = readAssets("$country/${Constants.CATEGORY_FILE}")
        return gson.fromJson(json, Array<Category>::class.java).toList()
    }

    override fun getCountryFiles(country: String): Array<String> = assets.list(country)!!

    override fun feedsByCategory(country: String, categoryId: Int): List<FeedItem> {
        val feedListJson = readAssets("$country/$categoryId.json")
        return gson.fromJson(feedListJson, Array<FeedItem>::class.java).toList()
    }

    private fun readAssets(file: String) = assets.readFile(file).toString(Charsets.UTF_8)
}