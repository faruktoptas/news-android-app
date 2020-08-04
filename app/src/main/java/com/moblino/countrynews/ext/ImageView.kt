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

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

fun ImageView.loadUrlWithResult(url: String?, result: (Boolean) -> Unit) {
    if (url == null) result(false)
    val target: Target = object : Target {
        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            setImageBitmap(bitmap)
            result(true)
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) {
            result(false)
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

        }
    }
    tag = target
    Picasso.get().load(url).into(target)
}