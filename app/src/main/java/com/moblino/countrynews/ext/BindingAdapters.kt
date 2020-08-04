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

package com.moblino.countrynews.ext

import androidx.databinding.BindingAdapter
import android.widget.ImageView
import android.widget.TextView
import com.moblino.countrynews.R
import com.moblino.countrynews.util.DateUtil
import com.moblino.countrynews.util.HtmlUtil
import com.squareup.picasso.Picasso

@BindingAdapter("textOrHide")
fun textOrHide(view: TextView, text: String?) {
    if (text.isNullOrEmpty()) {
        view.hide()
    } else {
        view.text = text
        view.show()
    }
}

@BindingAdapter("loadImageOrHide")
fun loadImageOrHide(view: ImageView, url: String?) {
    if (url.isNullOrEmpty()) {
        view.hide()
    } else {
        Picasso.get().load(HtmlUtil.fixImageUrl(url))
                .fit()
                .centerCrop()
                .error(R.drawable.bg_empty_image)
                .placeholder(R.drawable.news_placeholder_white)
                .into(view)
        view.show()
    }
}