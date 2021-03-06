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

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.moblino.countrynews.NewsApplication
import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseMvvmActivity
import com.moblino.countrynews.data.firebase.FirebaseManager
import com.moblino.countynews.common.ext.openEmailIntent
import com.moblino.countrynews.util.Constants
import com.moblino.countynews.common.PreferenceWrapper

class SettingsActivity : BaseMvvmActivity() {

    override fun layoutRes() = R.layout.activity_settings

    override fun initViews(savedInstanceState: Bundle?) {
        setupToolbar()

        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, SettingsFragment())
                .commit()
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = super.onCreateView(inflater, container, savedInstanceState)
            view?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.mainBackground))
            return view
        }

        override fun onCreatePreferences(bundle: Bundle?, s: String?) {
            addPreferencesFromResource(R.xml.main_settings)
            val prefFeedback = findPreference<Preference>(PREF_FEEDBACK)
            prefFeedback?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                requireActivity().openEmailIntent()
                true
            }
            findPreference<Preference>(PREF_GWL)?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                FirebaseManager.getInstance().setUserPropertyGWL(PreferenceWrapper.getInstance().readGWLMode())
                false
            }
            findPreference<Preference>(PREF_SHOW_SUMMARY)?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                FirebaseManager.getInstance().setUserPropertyDisableSummary(!PreferenceWrapper.getInstance().readShowDetailFirst())
                false
            }
            findPreference<Preference>(PreferenceWrapper.KEY_NIGHT_MODE)?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                FirebaseManager.getInstance().setUserPropertyNightMode(PreferenceWrapper.getInstance().readNightMode())
                requireActivity().recreate()
                true
            }
            findPreference<Preference>(PreferenceWrapper.KEY_FONT_SIZE)?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                NewsApplication.instance.fontSizeHelper.update(Integer.valueOf(newValue as String))
                prefChangeListener.onPreferenceChange(preference, newValue)
                true
            }
            findPreference<Preference>(PREF_PRIVACY)?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.PRIVACY_URL))
                startActivity(intent)
                false
            }
            val prefLoadImages = findPreference<ListPreference>(PREF_KEY_LOAD_IMAGES)
            val prefCountry = findPreference<ListPreference>(PREF_KEY_COUNTRY)
            prefLoadImages?.onPreferenceChangeListener = prefChangeListener
            prefCountry?.onPreferenceChangeListener = prefChangeListener
            updateSummary(prefLoadImages?.value, prefLoadImages, R.array.array_load_images, R.array.array_load_images_values)
            updateSummary(prefCountry?.value, prefCountry, R.array.array_country_keys, R.array.array_country_values)
        }

        private fun updateSummary(value: String?, pref: Preference?, id_keys: Int, id_values: Int) {
            val arr = resources.getStringArray(id_keys)
            val arrValues = resources.getStringArray(id_values)
            for (i in arr.indices) {
                if (arrValues[i] == value) {
                    pref?.summary = arr[i]
                }
            }
        }

        private val prefChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
            val key = o as String
            when (preference.key) {
                PREF_KEY_LOAD_IMAGES -> updateSummary(key, preference, R.array.array_load_images, R.array.array_load_images_values)
                PREF_KEY_COUNTRY -> updateSummary(key, preference, R.array.array_country_keys, R.array.array_country_values)
            }
            val intent = Intent()
            intent.putExtra(EXTRA_SETTINGS_CHANGED, true)
            requireActivity().setResult(Activity.RESULT_OK, intent)
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val PREF_KEY_LOAD_IMAGES = "pref_load_images"
        const val PREF_KEY_COUNTRY = "pref_country"
        const val PREF_FEEDBACK = "give_feedback"
        const val PREF_GWL = "pref_readability"
        const val PREF_PRIVACY = "privacy"
        const val PREF_SHOW_SUMMARY = "pref_show_detail_first"
        const val EXTRA_SETTINGS_CHANGED = "extra_settings_changed"
    }
}