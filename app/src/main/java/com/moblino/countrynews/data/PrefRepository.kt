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

package com.moblino.countrynews.data

import com.moblino.countrynews.utils.PreferenceWrapper

interface PrefRepository {
    fun isStaggered(): Boolean

    fun readDetailShowCase(): Boolean

    fun readCheckString(): String?

    fun writeCheckString(items: String)

    fun writeListOrder(order: String)

    fun readStartCount(): Int

    fun writeStartCount(count: Int)

    fun getListOrder(): String

    fun writeCategoryId(id: Int)

    fun readCategoryId(): Int

    fun readCountry(): String

    fun writeCountry(country: String)

    fun readAppVersion(): String

    fun writeNewVersion()

    fun readGWLMode(): Boolean
}

class PrefRepositoryImpl : PrefRepository {

    private val pref = PreferenceWrapper.getInstance()

    override fun isStaggered() = pref.readStaggered()

    override fun readDetailShowCase() = pref.readDetailShowcase()

    override fun readCheckString() = pref.readCheckString()

    override fun writeCheckString(items: String) = pref.writeCheckString(items)

    override fun writeListOrder(order: String) {
        pref.writeListOrder(order)
    }

    override fun readStartCount() = pref.readStartCount()

    override fun writeStartCount(count: Int) = pref.writeStartCount(count)

    override fun getListOrder(): String = pref.readListOrder()

    override fun writeCategoryId(id: Int) = pref.writeCategoryId(id)

    override fun readCategoryId() = pref.readCategoryId()

    override fun readCountry() = pref.readCountry()

    override fun writeCountry(country: String) = pref.writeCountry(country)

    override fun readAppVersion() = pref.readAppVersion() ?: ""

    override fun writeNewVersion() = pref.writeAppVersion()

    override fun readGWLMode() = pref.readGWLMode()
}