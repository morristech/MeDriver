/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.10.2020 18:07
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.presentation.utils.extensions

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.TextView
import androidx.annotation.Px
import androidx.annotation.StringRes
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop


fun TextView.text(): String = text.toString()

/**
 * Extension method to provide simpler access to {@link View#getResources()#getString(int)}.
 */
fun View.getStringRes(@StringRes stringRes: Int): String = resources.getString(stringRes)

fun Int.toIntArray(context: Context): IntArray {
	val typedArray = context.resources.obtainTypedArray(this)
	val drawableArray = IntArray(typedArray.length())
	for (i in drawableArray.indices) {
		drawableArray[i] = typedArray.getResourceId(i, 0)
	}
	//Recycles the TypedArray, to be re-used by a later caller.
	//After calling this function you must not ever touch the typed array again.
	typedArray.recycle()
	return drawableArray
}



inline fun <T: View> T.setDebounceOnClick(
	debounceTime: Long = 1000L, crossinline block: T.() -> Unit
) =
	setOnClickListener {
		when {
			tag != null && (tag as Long) > System.currentTimeMillis() -> return@setOnClickListener
			else -> {
				tag = System.currentTimeMillis() + debounceTime
				block()
			}
		}
	}

inline fun <T: View> T.setSingleOnClick(crossinline block: T.() -> Unit) = setOnClickListener {
	block()
	it.isClickable = false
	setOnClickListener(null)
}

inline fun <T: TextView> T.setOnClickWithSelection(crossinline block: T.() -> Unit) = setOnClickListener {
	block()
	it.isSelected = true
}

fun View.updateMargins(
	@Px start: Int = marginStart, @Px top: Int = marginTop, @Px end: Int = marginEnd,
	@Px bottom: Int = marginBottom
) {
	val params = layoutParams as MarginLayoutParams
	params.setMargins(start, top, end, bottom)
	this.layoutParams = params
}


fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()