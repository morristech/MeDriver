/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 20.09.2020 18:44
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
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity

/**
 *
 */

@Dao
interface VehicleDao {
	
	@Query("SELECT * FROM vehicle")
	suspend fun getAllVehicles(): List<VehicleEntity>
	
	@Query("SELECT * FROM vehicle WHERE vin = :vin")
	suspend fun getVehicleByVin(vin: String): VehicleEntity
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertVehicle(vehicle: VehicleEntity)
	
	@Delete
	suspend fun deleteVehicle(vehicle: VehicleEntity)
	
	@Query("DELETE FROM vehicle")
	suspend fun deleteAll()
	
}