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

package com.moblino.countrynews.features.about

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseActivity
import com.moblino.countrynews.ext.getAppVersion
import com.moblino.countrynews.features.settings.HealthCheckActivity
import kotlinx.android.synthetic.main.activity_about.activity_about_textversion
import kotlinx.android.synthetic.main.activity_about.ivLogo

class AboutActivity : BaseActivity() {

    private var mCounter = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        setupToolbar()

        activity_about_textversion.text = "v${getAppVersion()}"

        ivLogo.setOnClickListener {
            if (mCounter++ == 5) {
                startActivity(Intent(this, HealthCheckActivity::class.java))
            }
        }

    }
}

