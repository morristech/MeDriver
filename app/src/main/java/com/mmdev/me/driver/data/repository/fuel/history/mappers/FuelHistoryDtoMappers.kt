/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.09.2020 01:05
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.repository.fuel.history.mappers

import com.mmdev.me.driver.data.datasource.fuel.history.local.entities.FuelHistoryEntity
import com.mmdev.me.driver.data.datasource.fuel.history.remote.dto.FuelHistoryDto
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelPriceEntity
import com.mmdev.me.driver.data.datasource.fuel.prices.local.entities.FuelStationEntity
import com.mmdev.me.driver.domain.fuel.history.model.FuelHistoryRecord
import com.mmdev.me.driver.domain.fuel.prices.model.FuelPrice
import com.mmdev.me.driver.domain.fuel.prices.model.FuelStation

/**
 * In [FuelHistoryDto] -> Out: [FuelHistoryEntity], [FuelHistoryRecord]
 */

object FuelHistoryDtoMappers {
	
	/** Out [FuelHistoryEntity] */
	fun apiDtoToDbEntity(dto: FuelHistoryDto): FuelHistoryEntity =
		FuelHistoryEntity(
			commentary = dto.commentary,
			distancePassedBound = dto.distancePassedBound,
			filledLiters = dto.filledLiters,
			fuelConsumptionBound = dto.fuelConsumptionBound,
			fuelPrice = FuelPriceEntity(
				fuelStationId = dto.fuelStation.slug,
				price = dto.fuelPrice.price,
				type = dto.fuelPrice.type.code
			),
			fuelStation = FuelStationEntity(
				brandTitle = dto.fuelStation.brandTitle,
				slug = dto.fuelStation.slug,
				updatedDate = dto.fuelStation.updatedDate
			),
			odometerValueBound = dto.odometerValueBound,
			timestamp = dto.date.time,
			vehicleVinCode = dto.vehicleVinCode
		)
	
	/** Out [FuelHistoryRecord] */
	fun apiDtoToDomain(dto: FuelHistoryDto): FuelHistoryRecord =
		FuelHistoryRecord(
			commentary = dto.commentary,
			date = dto.date,
			distancePassedBound = dto.distancePassedBound,
			filledLiters = dto.filledLiters,
			fuelConsumptionBound = dto.fuelConsumptionBound,
			fuelPrice = FuelPrice(
				price = dto.fuelPrice.price,
				type = dto.fuelPrice.type.code
			),
			fuelStation = FuelStation(
				brandTitle = dto.fuelStation.brandTitle,
				slug = dto.fuelStation.slug,
				updatedDate = dto.fuelStation.updatedDate
			),
			odometerValueBound = dto.odometerValueBound,
			vehicleVinCode = dto.vehicleVinCode
		)
}