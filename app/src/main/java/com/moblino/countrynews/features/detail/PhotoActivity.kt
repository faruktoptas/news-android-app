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

package com.moblino.countrynews.features.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.moblino.countrynews.R
import com.moblino.countrynews.base.BaseMvvmActivity
import com.moblino.countrynews.ext.loadUrlWithResult
import com.moblino.countrynews.util.DateUtil
import kotlinx.android.synthetic.main.activity_photo.*
import uk.co.senab.photoview.PhotoViewAttacher

/**
 * Created by ftoptas on 22/02/17.
 */
class PhotoActivity : BaseMvvmActivity() {

    private var photoAttach: PhotoViewAttacher? = null

    override fun layoutRes() = R.layout.activity_photo

    override fun initViews(savedInstanceState: Bundle?) {
        val imageUrl = intent.getStringExtra(EXTRA_IMAGE)

        if (DateUtil.isLoadImagesEnabled(this) && imageUrl != null) {
            imageView.loadUrlWithResult(imageUrl) { success ->
                if (!success && !isFinishing) {
                    finish()
                }
            }
        }
        photoAttach = PhotoViewAttacher(imageView, true)
    }

    override fun finishAfterTransition() {
        super.finishAfterTransition()
        photoAttach?.cleanup()
    }

    companion object {
        const val EXTRA_IMAGE = "EXTRA_IMAGE"
        fun start(context: Context, image: String?) {
            val intent = Intent(context, PhotoActivity::class.java)
            intent.putExtra(EXTRA_IMAGE, image)
            context.startActivity(intent)
        }
    }
}