/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.09.2020 02:09
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel.prices.model

/**
 * Domain model
 */

data class FuelStation (
	val brandTitle: String = "",
	val slug: String = "",
	val updatedDate: String = ""
)
