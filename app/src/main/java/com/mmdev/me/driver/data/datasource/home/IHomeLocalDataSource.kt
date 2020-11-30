/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 30.11.2020 20:23
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.home

import com.mmdev.me.driver.data.datasource.home.entity.VehicleWithExpenses
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.vehicle.data.Expenses

/**
 *
 */

interface IHomeLocalDataSource {
	
	suspend fun getMyGarage(): SimpleResult<List<VehicleWithExpenses>>
	
	suspend fun getExpensesBetweenTimeRange(start: Long, end: Long): Expenses
	
}