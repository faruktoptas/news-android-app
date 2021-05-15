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

package com.moblino.countrynews.model;

import java.io.Serializable;

/**
 * Created by faruktoptas on 25/07/16.
 */

// TODO: 15.05.2021 Refactor data class
public class CardQuestion implements Serializable {

    private String mQuestionId;
    private final int mPosition;
    private final boolean mIsAd;
    private String mText;
    private int mImageResId;
    private String mFirstButtonText;
    private String mSecondButtonText;

    public CardQuestion(int position, boolean isAd) {
        mPosition = position;
        mIsAd = isAd;
    }

    public CardQuestion(String questionId, int position, boolean isAd, String text, int imageResId, String firstButtonText, String secondButtonText) {
        mQuestionId = questionId;
        mPosition = position;
        mIsAd = isAd;
        mText = text;
        mImageResId = imageResId;
        mFirstButtonText = firstButtonText;
        mSecondButtonText = secondButtonText;
    }

    public String getQuestionId() {
        return mQuestionId;
    }

    public int getPosition() {
        return mPosition;
    }

    public boolean isAd() {
        return mIsAd;
    }

    public String getText() {
        return mText;
    }

    public int getImageResId() {
        return mImageResId;
    }

    public String getFirstButtonText() {
        return mFirstButtonText;
    }

    public String getSecondButtonText() {
        return mSecondButtonText;
    }

}
