/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 17.08.2020 20:35
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel.prices.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity used to been stored in database
 */

@Entity(tableName = "fuel_providers")
data class FuelStationEntity(
	val brandTitle: String,
	@PrimaryKey
	val slug: String,
	val updatedDate: String
)