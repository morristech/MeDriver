/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 19.08.2020 19:51
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.data.datasource.remote.fuel.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * network models which is using by retrofit and remote data source
 */

@Serializable
data class NetworkFuelStation(
	@SerialName("value")
	val price: Double,
	@SerialName("marka")
	val brand: String,
	val slug: String
)

@Serializable
data class NetworkFuelSummary(
	@SerialName("minval")
	val minPrice: String,
	@SerialName("maxval")
	val maxPrice: String,
	@SerialName("avgval")
	val avgPrice: String
)


@Serializable
data class NetworkFuelModel(
	@SerialName("data")
	val networkFuelStations: List<NetworkFuelStation> = emptyList(),
	@SerialName("date")
	val pricesLastUpdatedDate: String = "",
	@SerialName("total")
	val fuelSummaryResponse: List<NetworkFuelSummary> = emptyList()
)

@Serializable
data class NetworkFuelModelResponse(val result: NetworkFuelModel)




