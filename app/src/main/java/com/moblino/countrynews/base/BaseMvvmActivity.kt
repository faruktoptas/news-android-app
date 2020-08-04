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

package com.moblino.countrynews.base

import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import com.moblino.countrynews.R
import com.moblino.countrynews.data.ResourceRepositoryImpl
import com.moblino.countrynews.ext.observeNotNull
import com.moblino.countrynews.utils.PreferenceWrapper

/**
 * Created by ftoptas on 22/01/18.
 */
abstract class BaseMvvmActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        delegate.setLocalNightMode(PreferenceWrapper.getInstance().readNightMode())
        super.onCreate(savedInstanceState)
        setContentView(layoutRes())
        initViews(savedInstanceState)

    }

    /**
     * @return resources file for Activity
     */
    @LayoutRes
    protected abstract fun layoutRes(): Int


    /**
     * Method is called after ButterKnife.setup() method.
     */
    protected abstract fun initViews(savedInstanceState: Bundle?)


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }


    protected fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    protected fun setDisplayHomeAsEnabled() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun getPreferenceWrapper(): PreferenceWrapper {
        return PreferenceWrapper.getInstance()
    }

    fun bindViewModel(vm: BaseViewModel) {
        vm.res = ResourceRepositoryImpl(this)
        vm.toastLive.observeNotNull(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
    }
}