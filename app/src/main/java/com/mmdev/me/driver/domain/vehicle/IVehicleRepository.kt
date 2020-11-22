/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.11.2020 14:52
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.vehicle

import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.PendingReplacement
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import kotlinx.coroutines.flow.Flow

/**
 * Vehicle repository provides data for [com.mmdev.me.driver.presentation.ui.vehicle]
 */

interface IVehicleRepository {
	
	suspend fun addVehicle(user: UserDataInfo?, vehicle: Vehicle): Flow<SimpleResult<Unit>>
	
	suspend fun getExpensesInfo(vin: String): SimpleResult<Expenses>
	
	suspend fun getPendingReplacements(vehicle: Vehicle): SimpleResult<Map<SparePart, PendingReplacement?>>
	
	suspend fun getAllSavedVehicles(): SimpleResult<List<Vehicle>>
	
	suspend fun getVehicleInfoByVin(vin: String) : SimpleResult<Vehicle>
}