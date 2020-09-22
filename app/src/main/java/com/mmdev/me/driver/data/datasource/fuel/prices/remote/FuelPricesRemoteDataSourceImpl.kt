/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 22.09.2020 01:41
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.fuel.prices.remote


import com.mmdev.me.driver.core.utils.toMap
import com.mmdev.me.driver.data.core.base.BaseDataSource
import com.mmdev.me.driver.data.datasource.fuel.prices.remote.api.FuelApi
import com.mmdev.me.driver.data.datasource.fuel.prices.remote.dto.FuelPricesDtoResponse
import com.mmdev.me.driver.domain.core.SimpleResult
import com.mmdev.me.driver.domain.fuel.FuelType
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow

/**
 * [IFuelPricesRemoteDataSource] implementation
 */

class FuelPricesRemoteDataSourceImpl(private val fuelApi: FuelApi) :
		IFuelPricesRemoteDataSource, BaseDataSource() {
	
	override suspend fun requestFuelPrices(date: String): SimpleResult<Map<FuelType, FuelPricesDtoResponse>> =
		safeCall { getPricesPerType(date, FuelType.values().asIterable()) }
	
	//get all prices for every fuel type
	//response contains list of fuelProviders and their prices for specified fuel type
	private suspend fun getPricesPerType(
		date:String,
		fuelTypes: Iterable<FuelType>
	): Map<FuelType, FuelPricesDtoResponse> =
		fuelTypes.asFlow().flatMapMerge(concurrency = 7) { fuelType ->
			flow { emit(fuelType to fuelApi.getFuelInfoFromApi(date, fuelType.code)) }
		}.toMap()
}