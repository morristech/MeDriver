/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 10.11.2020 18:17
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.maintenance.local

import com.mmdev.me.driver.core.utils.convertToLocalDateTime
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logWarn
import com.mmdev.me.driver.data.cache.CacheDao
import com.mmdev.me.driver.data.cache.CachedOperation
import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.datasource.maintenance.local.dao.MaintenanceDao
import com.mmdev.me.driver.data.datasource.maintenance.local.entity.VehicleSparePartEntity
import com.mmdev.me.driver.domain.core.SimpleResult

/**
 * [IMaintenanceLocalDataSource] implementation
 */

class MaintenanceLocalDataSourceImpl(
	private val dao: MaintenanceDao,
	private val cache: CacheDao
): IMaintenanceLocalDataSource, BaseDataSource() {
	
	override suspend fun cachePendingWriteToBackend(cachedOperations: List<CachedOperation>): SimpleResult<Unit> =
		safeCall(TAG) { cache.insertOperation(cachedOperations) }.also {
			logWarn(TAG, "Something doesn't require to write to backend, caching operations:$cachedOperations")
		}
	
	override suspend fun cachePendingWriteToBackend(cachedOperation: CachedOperation): SimpleResult<Unit> =
		safeCall(TAG) { cache.insertOperation(cachedOperation) }.also {
			logWarn(TAG, "Something doesn't require to write to backend, caching operation:$cachedOperation")
		}
	
	override suspend fun findLastReplaced(
		vin: String, systemNode: String, customNodeComponent: String
	): SimpleResult<VehicleSparePartEntity> = safeCall(TAG) {
		dao.findLastReplaced(vin, systemNode, customNodeComponent)
	}
	
	override suspend fun getMaintenanceHistory(
		vin: String, limit: Int, offset: Int
	): SimpleResult<List<VehicleSparePartEntity>> = safeCall(TAG) {
		dao.getMaintenanceHistory(vin, limit, offset)
	}
	
	override suspend fun getByTypedQuery(
		vin: String, typedQuery: String
	): SimpleResult<List<VehicleSparePartEntity>> = safeCall(TAG) {
		dao.getByTypedQuery(vin, "%$typedQuery%") //due to specific room query syntax
	}
	
	override suspend fun getSystemNodeHistory(
		vin: String, systemNode: String
	): SimpleResult<List<VehicleSparePartEntity>> = safeCall(TAG) {
		dao.getSystemNodeHistory(vin, systemNode)
	}
	
	override suspend fun insertReplacedSpareParts(
		replacedSpareParts: List<VehicleSparePartEntity>
	): SimpleResult<Unit> = safeCall(TAG) { dao.insertVehicleReplacedSparePart(replacedSpareParts) }.also {
		replacedSpareParts.forEach {
			logDebug(TAG,
				"Adding Replaced spare part: " + "id = ${it.date}, " +
				"date = ${convertToLocalDateTime(it.date).date}, " +
				"part vendor and articulus = ${it.vendor}, ${it.articulus}, " +
				"parent = ${it.systemNode} child = ${it.systemNodeComponent} criteria = ${it.searchCriteria}"
			)
		}
	}
	
	override suspend fun updateReplacedSparePart(
		replacedSparePart: VehicleSparePartEntity
	): SimpleResult<Unit> = safeCall(TAG) { dao.updateVehicleReplacedSparePart(replacedSparePart) }
	
	override suspend fun deleteFuelHistoryEntry(
		replacedSparePart: VehicleSparePartEntity
	): SimpleResult<Unit> = safeCall(TAG) { dao.deleteVehicleReplacedSparePart(replacedSparePart) }.also {
		logDebug(TAG, "Deleting Replaced spare part entry: id = ${replacedSparePart.date}")
	}
	
	override suspend fun clearAll(): SimpleResult<Unit> = safeCall(TAG) { dao.clearHistory() }
	
}