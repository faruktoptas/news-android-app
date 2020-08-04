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

package com.moblino.countrynews.features.detail;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.moblino.countrynews.model.RssItem;
import com.moblino.countrynews.model.CardQuestion;
import com.moblino.countrynews.util.PreferenceWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by faruktoptas on 29/10/15.
 */
// TODO: 4.08.2020 use ViewPager2

public class NewsDetailViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();

    public NewsDetailViewPagerAdapter(FragmentManager manager, @NonNull List<RssItem> list, CardQuestion cardQuestion) {
        super(manager);

        int fragmentCount = list.size();
        int adPos = -1;
        if (cardQuestion != null && cardQuestion.isAd() && cardQuestion.getPosition() > -1) {
            fragmentCount = fragmentCount + 1;
            adPos = cardQuestion.getPosition();
        }
        for (int i = 0; i < fragmentCount; i++) {
            int position = i;
            if (adPos > -1 && i >= adPos) {
                position = i - 1;
            }
            mFragmentList.add(NewsDetailFragment.newInstance(list.get(position)));
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

}
