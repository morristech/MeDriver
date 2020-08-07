/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 07.08.20 16:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.local.fuel.dao

import androidx.room.*
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelProviderAndPrices
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelProviderEntity
import com.mmdev.me.driver.data.datasource.local.fuel.entities.FuelSummaryEntity

/**
 * Dao interface to "talk" with MeDriverRoomDatabase
 */

@Dao
interface FuelDao {
	
	@Transaction
	@Query("SELECT * FROM fuel_providers")
	suspend fun getFuelPrices(): List<FuelProviderAndPrices>
	
	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insertFuelProvider(fuelProviderEntity: FuelProviderEntity)
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertFuelPrice(fuelPrice: FuelPriceEntity)
	
	@Query("DELETE FROM fuel_providers")
	suspend fun deleteAllFuelProviders()
	
	
	
	@Query("SELECT * FROM fuel_summary")
	suspend fun getFuelSummaries(): List<FuelSummaryEntity>
	
	@Query("SELECT * FROM fuel_summary WHERE type = :fuelType AND updatedDate = :updatedDate")
	suspend fun getFuelSummaryByDateAndType(fuelType: Int, updatedDate: String): List<FuelSummaryEntity>
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertFuelSummary(fuelSummaryEntity: FuelSummaryEntity)
	
	@Query("DELETE FROM fuel_summary")
	suspend fun deleteAllFuelSummaries()
	
}