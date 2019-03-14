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

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.naver.android.svc.core.controltower.ControlTower
import com.naver.android.svc.core.controltower.DialogFragmentControlTowerManager
import com.naver.android.svc.core.controltower.FragmentControlTowerManager
import com.naver.android.annotation.RequireControlTower
import com.naver.android.svc.core.views.Views

/**
 * you should set dialogListener after you create dialog instance.
 * if your dialog has no interaction set Unit.INSTANCE at "dialogListener" field
 * @author bs.nam@navercorp.com 2017. 11. 22..
 */
abstract class SvcDialogFragment<out V : Views, DL : Any> : DialogFragment(), LifecycleOwner, Screen<V> {

    private val CONTROLTOWER_KEY = "controlTower"
    val CLASS_SIMPLE_NAME = javaClass.simpleName
    var TAG: String = CLASS_SIMPLE_NAME

    val views by lazy { createViews() }
    lateinit var controlTower: ControlTower

    override val hostActivity: FragmentActivity?
        get() = activity

    override val screenFragmentManager: FragmentManager?
        get() = fragmentManager

    lateinit var dialogListener: DL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // assigns ControlTower
        assignControlTower()

        if (!::dialogListener.isInitialized) {
            dismissAllowingStateLoss()
            return
        }

        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        views.rootView.setOnClickListener {
            dismissAllowingStateLoss()
        }

        return views.rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // initialize SVC
        initializeSVC(this, views, controlTower)

        lifecycle.addObserver(views)
        lifecycle.addObserver(controlTower)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(controlTower)
        lifecycle.removeObserver(views)

        // destroy controlTower
        fragmentManager?.let {
            if (!it.isStateSaved) {
                FragmentControlTowerManager.instance.destroy(controlTower)
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        views.rootView = LayoutInflater.from(context).inflate(views.layoutResId, null) as ViewGroup
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.setOnKeyListener { _, keyCode, _ ->

            if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                if (!onBackPressed()) {
                    dismissAllowingStateLoss()
                }
                true
            } else {
                false

            }
        }

        return dialog
    }

    open fun onBackPressed(): Boolean {
        if (controlTower.onBackPressed() || views.onBackPressed()) {
            return true
        }
        return false
    }

    override fun dismiss() {
        dismissAllowingStateLoss()
    }

    /**
     * assign ControlTower
     */
    private fun assignControlTower() {
        val annotation = javaClass.getAnnotation(com.naver.android.annotation.RequireControlTower::class.java)
        annotation?.let {
            val controlTowerClass = it.value
            this.controlTower = DialogFragmentControlTowerManager.instance.fetch(this,
                    controlTowerClass,
                    views)
        } ?: throw IllegalAccessException("$javaClass missing RequireControlTower annotation")
    }


    override val isActive: Boolean
        get() = hostActivity != null && context != null && isAdded && !isRemoving && !isDetached

}
