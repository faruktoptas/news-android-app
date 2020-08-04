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

package com.moblino.countrynews.features.editlist


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseMvvmActivity
import com.moblino.countrynews.ext.observeNotNull
import com.moblino.countrynews.features.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_edit_list.recyclerView
import org.koin.android.viewmodel.ext.android.viewModel

class EditFeedsActivity : BaseMvvmActivity() {


    private val viewModel: EditFeedsViewModel by viewModel()

    override fun layoutRes() = R.layout.activity_edit_list

    override fun initViews(savedInstanceState: Bundle?) {
        bindViewModel(viewModel)
        viewModel.getList()
        viewModel.finishAfterSave.observeNotNull(this) {
            val intent = Intent()
            intent.putExtra(SettingsActivity.EXTRA_SETTINGS_CHANGED, true)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        setupList()
        setupToolbar()
    }

    private fun setupList() {
        val adapter = SortableAdapter(viewModel)
        viewModel.feedListLive.observeNotNull(this) { adapter.items = it }

        adapter.enableDragDrop(recyclerView) { old, new ->
            viewModel.onItemMove(old, new)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_editlist, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_save_list -> {
                viewModel.save()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
