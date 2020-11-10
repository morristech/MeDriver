/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.11.2020 17:15
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.prices.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase

/**
 *
 */
@Entity(tableName = MeDriverRoomDatabase.FUEL_SUMMARY_TABLE)
data class FuelSummaryEntity(
	val typeCode: Int,
	val minPrice: String,
	val maxPrice: String,
	val avgPrice: String,
	val updatedDate: String
) {
	@PrimaryKey
	var summaryId: String = updatedDate + "_$typeCode"
}