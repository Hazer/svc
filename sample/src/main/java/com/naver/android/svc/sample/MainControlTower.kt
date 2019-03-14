/*
 * Copyright 2018 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.naver.android.svc.sample

import com.naver.android.svc.core.controltower.ControlTower
import com.naver.android.annotation.InjectScreen
import com.naver.android.svc.sample.tabs.MainTab

class MainControlTower : ControlTower(), MainViewsAction {

    @InjectScreen
    lateinit var mainScreen: MainActivity

    override fun onClickHome() {
        this.mainScreen.changeScreen(MainTab.HOME)
    }

    override fun onClickPaper() {
        this.mainScreen.changeScreen(MainTab.PAPER)
    }

    override fun onClickPalette() {
        this.mainScreen.changeScreen(MainTab.PALETTE)
    }

    override fun onClickSearch() {
        this.mainScreen.changeScreen(MainTab.SEARCH)
    }

    override fun onClickStatistic() {
        this.mainScreen.changeScreen(MainTab.STATISTIC)
    }
}