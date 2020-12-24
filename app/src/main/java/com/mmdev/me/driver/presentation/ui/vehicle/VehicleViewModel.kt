/*
 * Created by Andrii Kovalchuk
 * Copyright (C) 2020. medriver
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses
 */

package com.mmdev.me.driver.presentation.ui.vehicle

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.R.*
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.data.core.mappers.mapList
import com.mmdev.me.driver.domain.maintenance.data.components.PlannedParts
import com.mmdev.me.driver.domain.maintenance.data.components.base.SparePart
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.data.ConsumptionHistory
import com.mmdev.me.driver.domain.vehicle.data.Expenses
import com.mmdev.me.driver.domain.vehicle.data.PendingReplacement
import com.mmdev.me.driver.domain.vehicle.data.Regulation
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.ui.MainActivity
import com.mmdev.me.driver.presentation.ui.maintenance.VehicleSystemNodeConstants
import com.mmdev.me.driver.presentation.ui.vehicle.data.ConsumablePartUi
import com.mmdev.me.driver.presentation.ui.vehicle.data.VehicleConstants
import com.mmdev.me.driver.presentation.ui.vehicle.data.VehicleUi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 *
 */

class VehicleViewModel(private val repository: IVehicleRepository) : BaseViewModel() {
	
	//called when new entry was added
	val newWasAdded = MutableLiveData(false)
	
	//init current vehicle from static inside Application class
	val chosenVehicle = MutableLiveData(MainActivity.currentVehicle)
	
	//init vehicle list
	private val vehicleList = MutableLiveData<List<Vehicle>>(emptyList())
	val vehicleUiList = MutableLiveData<List<VehicleUi>>(emptyList())
	val replacements = MutableLiveData<List<ConsumablePartUi>>()
	val expensesData = MutableLiveData(Expenses())
	val fuelConsumptionData = MutableLiveData(emptyList<ConsumptionHistory>())
	
	init {
		getSavedVehicles()
		getReplacementsList(chosenVehicle.value)
		getExpenses(chosenVehicle.value)
		getConsumption(chosenVehicle.value)
	}
	
	
	fun getSavedVehicles() {
		viewModelScope.launch {
			repository.getAllSavedVehicles().fold(
				success = {
					vehicleList.postValue(it)
					vehicleUiList.postValue(mapVehicles(it))
				},
				failure = { logError(TAG, "${it.message}") }
			)
		}
	}
	
	fun setVehicle(position: Int) {
		if (position > vehicleList.value!!.size) return
		refreshVehicle(vehicleList.value?.get(position))
	}
	
	private fun refreshVehicle(vehicleToSet: Vehicle?) {
		chosenVehicle.postValue(vehicleToSet)
		getReplacementsList(vehicleToSet)
		getExpenses(vehicleToSet)
		getConsumption(vehicleToSet)
	}
	
	fun deleteVehicle() = chosenVehicle.value?.let {
		viewModelScope.launch {
			repository.deleteVehicle(MainActivity.currentUser, it).collect { result ->
				result.fold(
					success = {
						refreshVehicle(null)
						getSavedVehicles()
					},
					failure = { logError(TAG, "${it.message}") }
				)
			}
		}
	}
	
	
	fun convertToUiVehicle(vehicle: Vehicle): VehicleUi = VehicleUi(
		icon = VehicleConstants.vehicleBrandIconMap.getOrDefault(vehicle.brand, 0),
		title = "${vehicle.brand} ${vehicle.model} (${vehicle.year}), ${vehicle.engineCapacity}",
		vin = vehicle.vin
	)
	
	private fun mapVehicles(input: List<Vehicle>): List<VehicleUi> = mapList(input) { vehicle ->
		convertToUiVehicle(vehicle)
	}.plus(VehicleUi(drawable.ic_plus_in_frame_24, "", string.fg_vehicle_add_new_vehicle, ""))
	
	private fun getReplacementsList(vehicle: Vehicle?) {
		if (vehicle == null) replacements.value = emptyList()
		else {
			viewModelScope.launch {
				repository.getPendingReplacements(vehicle).fold(
					success = {
						replacements.postValue(buildConsumables(it))
							  },
					failure = { logError(TAG, "${it.message}") }
				)
			}
		}
	}
	
	private fun buildConsumables(replacements: Map<SparePart, PendingReplacement?>): List<ConsumablePartUi> =
		if (replacements.isNotEmpty()) replacements.map {
			ConsumablePartUi(
				VehicleSystemNodeConstants.plannedComponents[replacements.keys.indexOf(it.key)],
				it.value,
				chosenVehicle.value?.maintenanceRegulations?.get(it.key) ?: Regulation()
			)
		}
		else List(PlannedParts.valuesArray.size) {
			ConsumablePartUi(VehicleSystemNodeConstants.plannedComponents[it])
		}
	
	
	private fun getExpenses(vehicle: Vehicle?) {
		if (vehicle == null) expensesData.value = Expenses()
		else viewModelScope.launch {
			repository.getExpensesInfo(vehicle.vin).fold(
				success = { expensesData.postValue(it) },
				failure = { logError(TAG, "${it.message}") }
			)
		}
	
	}
	
	private fun getConsumption(vehicle: Vehicle?) {
		if (vehicle == null) fuelConsumptionData.value = emptyList()
		else viewModelScope.launch {
			repository.getFuelConsumption(vehicle.vin).fold(
				success = { fuelConsumptionData.postValue(it) },
				failure = { logError(TAG, "${it.message}") }
			)
		}
	}
}