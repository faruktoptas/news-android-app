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

package com.moblino.countrynews.features.settings

import android.os.Bundle
import com.mikepenz.aboutlibraries.LibsBuilder
import com.moblino.countrynews.R
import com.moblino.countrynews.R.string
import com.moblino.countrynews.base.BaseMvvmActivity

/**
 * Created by faruktoptas on 21/07/16.
 */
class LicensesActivity : BaseMvvmActivity() {

    override fun layoutRes() = R.layout.activity_base

    override fun initViews(savedInstanceState: Bundle?) {
        val fragment = LibsBuilder().withFields(string::class.java.fields).supportFragment()
        supportFragmentManager.beginTransaction().add(R.id.rlMain, fragment).commitAllowingStateLoss()
        setupToolbar()
    }
}