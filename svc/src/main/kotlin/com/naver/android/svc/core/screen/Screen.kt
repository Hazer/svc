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
package com.naver.android.svc.core.screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.naver.android.svc.core.controltower.ControlTower
import com.naver.android.svc.core.views.ActionViews
import com.naver.android.svc.core.views.Views
import com.naver.android.svc.core.views.ViewsAction

/**
 * @author bs.nam@navercorp.com 2018. 2. 21..
 */
interface Screen : LifecycleOwner {
    /**
     * every screen can access to their host Activity.
     *
     * I had to change name "activity" as "hostActivity"
     * because fragment's getActivity() method is final method
     */
    val hostActivity: FragmentActivity?

    val screenFragmentManager: FragmentManager?

    val isActive: Boolean

    fun getChildFragmentManager(): FragmentManager
    fun getParentFragment(): Fragment?

    fun createViews(): Views
    fun createControlTower(): ControlTower

    val baseControlTower: ControlTower
    val baseViews: Views

    /**
     * add dependency of Screen, Views, ControlTower and ViewsAction
     */
    fun <V : Views, C : ControlTower> initializeSVC(screen: Screen, views: V, ct: C) {
        views.apply {
            views.screen = screen

            if (this is ActionViews<*> && ct is ViewsAction) {
                setAction(ct)
            }
        }

        ct.apply {
            baseScreen = screen
            baseViews = views
            activity = screen.hostActivity
        }
    }
}
