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

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.moblino.countynews.common.model.FeedItemWrapper

class MainAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    var feedItems: List<FeedItemWrapper> = listOf()

    override fun getItemCount() = feedItems.size

    override fun getItemId(position: Int): Long {
        return feedItems[position].item.feedId.toLong()
    }

    override fun createFragment(position: Int): Fragment {
        val item = feedItems[position]
        return MainActivityFragment.newInstance(item.item, item.question)
    }


}