/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.data.datasource.garage

import com.mmdev.me.driver.data.core.base.datasource.BaseDataSource
import com.mmdev.me.driver.data.datasource.garage.dao.GarageDao
import com.mmdev.me.driver.data.datasource.garage.entity.VehicleWithExpenses

/**
 * [IGarageLocalDataSource] implementation
 */

class GarageLocalDataSourceImpl(private val dao: GarageDao): BaseDataSource(), IGarageLocalDataSource {
	
	override suspend fun getMyGarage(): List<VehicleWithExpenses> = dao.getAllVehiclesWithExpenses()
	
	override suspend fun getExpensesBetweenTimeRange(
		start: Long, end: Long
	) = dao.getExpensesBetweenTime(start, end)
	
}