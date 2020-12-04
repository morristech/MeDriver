/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 04.12.2020 18:25
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.sync.upload.maintenance

import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.data.datasource.maintenance.local.IMaintenanceLocalDataSource
import com.mmdev.me.driver.data.datasource.maintenance.server.IMaintenanceServerDataSource
import com.mmdev.me.driver.data.repository.maintenance.mappers.MaintenanceMappersFacade
import com.mmdev.me.driver.domain.core.ResultState
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

/**
 * [IMaintenanceUploader] implementation
 */

class MaintenanceUploader(
	private val local: IMaintenanceLocalDataSource,
	private val server: IMaintenanceServerDataSource,
	private val mappers: MaintenanceMappersFacade
): IMaintenanceUploader {
	
	private val TAG = "mylogs_${javaClass.simpleName}"
	
	override suspend fun fetch(email: String) = flow {
		logDebug(TAG, "getting maintenance cached operations...")
		local.getCachedOperations().fold(
			success = { operations ->
				logInfo(TAG, "maintenance cached operations count = ${operations.size}")
				operations.asFlow().flatMapMerge { operation ->
					logDebug(TAG, "executing $operation")
					flow {
						local.getRecordById(operation.recordId.toLong()).fold(
							success = { record ->
								if (record != null) {
									server.addMaintenanceHistoryItems(
										email,
										record.vehicleVinCode,
										mappers.listEntitiesToDto(listOf(record))
									).collect { result ->
										result.fold(
											success = { emit(local.deleteCachedOperation(operation)) },
											failure = { emit(ResultState.failure(it)) }
										)
									}
								}
								else {
									logError(TAG, "No such record stored in database")
									emit(local.deleteCachedOperation(operation))
								}
								
							},
							failure = {
								emit(ResultState.failure(it))
							}
						)
					}
				}.collect { emit(it) }
			},
			failure = {
				emit(ResultState.failure(it))
			}
		)
	}
}