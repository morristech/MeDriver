/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.11.2020 18:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.vehicle.local

import com.mmdev.me.driver.core.utils.log.logWarn
import com.mmdev.me.driver.data.cache.CacheDao
import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.datasource.vehicle.local.dao.VehicleDao
import com.mmdev.me.driver.data.datasource.vehicle.local.entities.VehicleEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * [IVehicleLocalDataSource] implementation
 */

class VehicleLocalDataSourceImpl(
	private val dao: VehicleDao,
	private val cache: CacheDao
): IVehicleLocalDataSource, BaseDataSource() {
	
	override suspend fun cachePendingWriteToBackend(cachedOperation: CachedOperation): SimpleResult<Unit> =
		safeCall(TAG) { cache.insertOperation(cachedOperation) }.also {
			logWarn(TAG, "Something doesn't require to write to backend, caching operation:$cachedOperation")
		}
	
	override suspend fun getAllVehicles(): SimpleResult<List<VehicleEntity>> =
		safeCall(TAG) { dao.getAllVehicles() }
	
	override suspend fun getVehicle(vin: String): SimpleResult<VehicleEntity> =
		safeCall(TAG) { dao.getVehicleByVin(vin) }
	
	override suspend fun insertVehicle(vehicleEntity: VehicleEntity): SimpleResult<Unit> =
		safeCall(TAG) { dao.insertVehicle(vehicleEntity) }
	
	override suspend fun deleteVehicle(vehicleEntity: VehicleEntity): SimpleResult<Unit> =
		safeCall(TAG) { dao.deleteVehicle(vehicleEntity) }
	
	override suspend fun clearAll(): SimpleResult<Unit> =
		safeCall(TAG) { dao.clearAll() }
	
	
}