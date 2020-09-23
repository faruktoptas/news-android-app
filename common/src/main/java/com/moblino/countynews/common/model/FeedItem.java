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

package com.moblino.countynews.common.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by faruktoptas on 30/10/15.
 */
public class FeedItem implements Serializable {

    @SerializedName("i")
    private Integer feedId;

    @SerializedName("n")
    private String title;

    @SerializedName("l")
    private String feedUrl;

    @SerializedName("e")
    private String encoding;

    private transient String mCategory;
    private int mCategoryId;

    public Integer getFeedId() {
        return feedId;
    }

    public String getTitle() {
        return title;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public String getEncoding() {
        return encoding;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setFeedId(Integer feedId) {
        this.feedId = feedId;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public int getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(int categoryId) {
        mCategoryId = categoryId;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
