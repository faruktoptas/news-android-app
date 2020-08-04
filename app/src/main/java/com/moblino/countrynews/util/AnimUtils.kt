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

package com.moblino.countrynews.util

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View

object AnimUtils {

    fun shake(imageView: View, isFinishing: Boolean) {
        imageView.postDelayed({
            if (!isFinishing) {
                val animatorSet = AnimatorSet()
                val slideLeft = ObjectAnimator.ofFloat(imageView, "translationX", -50f)
                slideLeft.duration = 400
                val slideRight = ObjectAnimator.ofFloat(imageView, "translationX", 50f)
                slideLeft.duration = 400
                val slideLeft2 = ObjectAnimator.ofFloat(imageView, "translationX", 0f)
                slideLeft.duration = 300
                animatorSet.playSequentially(slideLeft, slideRight, slideLeft2)
                animatorSet.start()
            }
        }, 1000)
    }

}