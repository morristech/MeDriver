/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 25.09.2020 21:02
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vehicle.remote.dto

import com.mmdev.me.driver.domain.fuel.history.model.DistanceBound

/**
 *
 */

data class VehicleDto (
	val brand: String = "",
	val model: String = "",
	val year: Int = 0,
	val vin: String = "",
	val odometerValueBound: DistanceBound = DistanceBound(),
	val engineCapacity: Double = 0.0
)