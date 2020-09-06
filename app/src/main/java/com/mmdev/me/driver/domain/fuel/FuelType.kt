/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 01.09.2020 20:19
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.domain.fuel


/**
 * Used to define map: which [com.mmdev.me.driver.data.datasource.remote.fuel.model.NetworkFuelModel]
 * corresponds to a given [FuelType]
 */

enum class FuelType (val code: Int) {
	A100(9), A98(1), A95PLUS(2), A95(3), A92(4), DT(5), GAS(6)
}