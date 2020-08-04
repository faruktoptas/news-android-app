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

package com.moblino.countrynews.features.activity

import com.moblino.countrynews.ext.unite
import org.junit.Test

class ExtensionTest {

    @Test
    fun testUniteList() {
        val empty = listOf<Int>()
        assert(empty.unite() == "")

        val single = listOf(3)
        assert(single.unite() == "3")

        val multiple = listOf(1, 3)
        assert(multiple.unite() == "1,3")

        assert(multiple.unite('-') == "1-3")
    }
}