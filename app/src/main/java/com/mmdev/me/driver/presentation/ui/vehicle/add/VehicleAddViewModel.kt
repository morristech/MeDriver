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

package com.mmdev.me.driver.presentation.ui.vehicle.add

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mmdev.me.driver.core.utils.extensions.currentEpochTime
import com.mmdev.me.driver.core.utils.log.logError
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.domain.user.UserDataInfo
import com.mmdev.me.driver.domain.vehicle.IVehicleRepository
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.core.base.BaseViewModel
import com.mmdev.me.driver.presentation.utils.extensions.domain.buildDistanceBound
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 *
 */

class VehicleAddViewModel(private val repository: IVehicleRepository): BaseViewModel() {
	
	val viewState: MutableLiveData<VehicleAddViewState> = MutableLiveData()
	
	val vinCodeInput: MutableLiveData<String?> = MutableLiveData()
	val brandInput: MutableLiveData<String?> = MutableLiveData()
	val modelInput: MutableLiveData<String?> = MutableLiveData()
	val yearInput: MutableLiveData<String?> = MutableLiveData()
	val odometerInput: MutableLiveData<String?> = MutableLiveData()
	val engineCapacityInput: MutableLiveData<String?> = MutableLiveData()
	
	//viewModel.getVehicleByVIN("WF0FXXWPDF3K73412")
	
	val vehicleList: MutableLiveData<List<Vehicle>> = MutableLiveData(emptyList())
	init {
		viewModelScope.launch {
			repository.getAllSavedVehicles().fold(
				success = { vehicleList.value = it },
				failure = { logError(TAG, "${it.message}") }
			)
			
		}
	}
	
	// check null or empty vehicle list (no vehicles have been added yet)
	// or vehicle with same vin doesn't exists
	fun checkAndAdd(user: UserDataInfo?) {
		with(buildVehicle()) {
			if (vehicleList.value.isNullOrEmpty() || !vehicleList.value!!.any { it.vin == this.vin }) {
				addVehicle(user, this)
			}
		}
		
	}
	
	private fun addVehicle(user: UserDataInfo?, vehicle: Vehicle) {
		viewModelScope.launch {
			repository.addVehicle(user, vehicle).collect { result ->
				result.fold(
					success = { viewState.postValue(VehicleAddViewState.Success) },
					failure = { viewState.postValue(VehicleAddViewState.Error(it.localizedMessage)) }
				)
			}
		}
	}
	
	private fun buildVehicle(): Vehicle =
		Vehicle(
			brand = brandInput.value!!,
			model = modelInput.value!!,
			year = yearInput.value!!.toInt(),
			vin = vinCodeInput.value!!,
			odometerValueBound = buildDistanceBound(odometerInput.value!!.toInt()),
			engineCapacity = engineCapacityInput.value!!.toDouble(),
			lastUpdatedDate = currentEpochTime()
		)
	
	
	fun getVehicleByVIN(vinCode: String) {
		
		viewState.postValue(VehicleAddViewState.Loading)
		
		viewModelScope.launch {
			
			withTimeout(30000) {
				
				repository.getVehicleInfoByVin(vinCode).fold(
					success = {
						viewState.postValue(VehicleAddViewState.Idle)
						
						logInfo(TAG, "Found by VIN: $it")
						
						//autocomplete ui
						brandInput.postValue(it.brand)
						modelInput.postValue(it.model)
						yearInput.postValue(it.year.toString())
						engineCapacityInput.postValue(it.engineCapacity.toString())
						odometerInput.postValue("")
					},
					failure = {
						viewState.postValue(VehicleAddViewState.Error(it.localizedMessage))
					}
				)
			}
		}
	}
}