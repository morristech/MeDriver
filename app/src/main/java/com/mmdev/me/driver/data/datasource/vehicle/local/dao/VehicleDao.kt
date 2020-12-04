/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 20:32
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vehicle.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.mmdev.me.driver.data.core.database.MeDriverRoomDatabase
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts
import com.mmdev.me.driver.domain.vehicle.data.Expenses

/**
 * Dao responsible for storing and retrieving vehicles info from database
 */

@Dao
interface VehicleDao {
	
	@Transaction
	suspend fun getExpenses(vin: String): Expenses {
		return Expenses(
			getExpensesMaintenance(vin) ?: 0.0,
			getExpensesFuelHistory(vin) ?: 0.0
		)
	}
	
	@Query(
		"""
		SELECT SUM(moneySpent) as fuelExpenses
		FROM ${MeDriverRoomDatabase.FUEL_HISTORY_TABLE}
		WHERE vehicleVinCode = :vin
		"""
	)
	suspend fun getExpensesFuelHistory(vin: String): Double?
	
	@Query(
		"""
		SELECT SUM(moneySpent)
		FROM ${MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE}
		WHERE vehicleVinCode = :vin
		"""
	)
	suspend fun getExpensesMaintenance(vin: String): Double?
	
	@Transaction
	suspend fun getPlannedReplacements(vin: String): Map<String, VehicleSparePartEntity> {
		return PlannedParts.valuesArray.map { it.getSparePartName() }.zip(
			PlannedParts.valuesArray.map {
				getPlannedLastReplacement(vin, it.getSparePartName())
			}
		).toMap()
	}
	
	@Query(
		"""
		SELECT * FROM ${MeDriverRoomDatabase.MAINTENANCE_HISTORY_TABLE}
		WHERE vehicleVinCode =:vin AND systemNodeComponent = :plannedSparePart
		ORDER BY date DESC
		LIMIT 1
		"""
	)
	fun getPlannedLastReplacement(vin: String, plannedSparePart: String): VehicleSparePartEntity
	
	@Query("SELECT * FROM ${MeDriverRoomDatabase.VEHICLES_TABLE}")
	suspend fun getAllVehicles(): List<VehicleEntity>
	
	@Query("SELECT * FROM ${MeDriverRoomDatabase.VEHICLES_TABLE} WHERE vin = :vin")
	suspend fun getVehicleByVin(vin: String): VehicleEntity
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertVehicle(vehicle: VehicleEntity)
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun importVehicles(vehiclesToImport: List<VehicleEntity>)
	
	@Delete
	suspend fun deleteVehicle(vehicle: VehicleEntity)
	
	@Query("DELETE FROM ${MeDriverRoomDatabase.VEHICLES_TABLE}")
	suspend fun clearAll()
	
}