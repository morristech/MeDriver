/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.09.2020 17:51
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.history.local.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity

/**
 * Describes a [VehicleEntity] with a related [FuelHistoryEntity] list
 * One-to-many relation
 * [VehicleEntity] has many [FuelHistoryEntity] entries, but [FuelHistoryEntity] has only one owner
 */

data class VehicleWithFuelHistory(
	@Embedded val vehicleEntity: VehicleEntity,
	@Relation(
		parentColumn = "vin",
		entityColumn = "vehicleVinCode",
		entity = FuelHistoryEntity::class
	)
	val fuelHistory: List<FuelHistoryEntity>
)