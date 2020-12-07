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

package com.mmdev.me.driver.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mmdev.me.driver.BR
import com.mmdev.me.driver.R
import com.mmdev.me.driver.databinding.ItemHomeGarageBinding
import com.mmdev.me.driver.domain.vehicle.data.Vehicle
import com.mmdev.me.driver.presentation.ui.home.MyGarageAdapter.MyGarageViewHolder
import com.mmdev.me.driver.presentation.utils.extensions.attachClickToCopyText

/**
 *
 */

class MyGarageAdapter(
	private var data: List<Vehicle> = emptyList()
): RecyclerView.Adapter<MyGarageViewHolder>() {
	
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyGarageViewHolder =
		MyGarageViewHolder(
			ItemHomeGarageBinding.inflate(
				LayoutInflater.from(parent.context),
				parent,
				false
			)
		)
	
	override fun onBindViewHolder(holder: MyGarageViewHolder, position: Int) = holder.bind(data[position])
	
	override fun getItemCount(): Int = data.size
	
	fun setNewData(newData: List<Vehicle>) {
		data = newData
		notifyDataSetChanged()
	}
	
	inner class MyGarageViewHolder(private val binding: ItemHomeGarageBinding):
			RecyclerView.ViewHolder(binding.root) {
		
		fun bind(item: Vehicle) {
			
			binding.btnCopyVehicleVin.attachClickToCopyText(
				binding.root.context,
				R.string.fg_vehicle_copy_vin
			)
			
			binding.setVariable(BR.bindItem, item)
			binding.executePendingBindings()
		}
		
	}
	
	
	
}