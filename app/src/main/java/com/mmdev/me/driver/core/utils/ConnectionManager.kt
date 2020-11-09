/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 09.11.2020 19:02
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core.utils

import android.Manifest
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.core.utils.log.logWarn

/**
 * class for monitor network connection
 * @see https://gist.github.com/PasanBhanu/730a32a9eeb180ec2950c172d54bb06a
 *
 * The callback is called for every separate network instead of the default network.
 * For example, if both WiFi and Cellular Data are connected, and only Cellular Data is turned off,
 * onLost() will be called.
 * This does not mean that the device network is lost, but rather that the specific network has been
 * lost.
 * The device could still be connected via another network.
 * Because of this, a list of activeNetworks is maintained, and used to check if the device is
 * connected to any active networks.
 */

class ConnectionManager(
	context: Context,
	lifecycleOwner: LifecycleOwner,
	private val callback: (Boolean) -> Unit
) : LifecycleObserver {

	private var connectivityManager =
		context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
	
	private val networkRequest = NetworkRequest.Builder()
		.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
		.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
		.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
		.build()
	
	init {
//		try {
//			connectivityManager.unregisterNetworkCallback(callback)
//		} catch (e: Exception) {
//			logWarn(this@ConnectionManager.javaClass,
//			        "NetworkCallback for Wi-fi was not registered or already unregistered")
//		}
//		connectivityManager.registerNetworkCallback(NetworkRequest.Builder().build(), callback)
		lifecycleOwner.lifecycle.addObserver(this)
		callback.invoke(getInitialConnectionStatus())
	}
	
	@Suppress("unused")
	@OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
	fun onResume() {
		connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
	}
	
	@Suppress("unused")
	@OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
	fun onPause() {
		connectivityManager.unregisterNetworkCallback(networkCallback)
	}
	
	private fun getInitialConnectionStatus(): Boolean {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val network = connectivityManager.activeNetwork
			val capabilities = connectivityManager.getNetworkCapabilities(network)
			capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
		} else {
			val activeNetwork = connectivityManager.activeNetworkInfo // Deprecated in 29
			activeNetwork != null && activeNetwork.isConnectedOrConnecting // // Deprecated in 28
		}
	}
	
	
	private val networkCallback = object : ConnectivityManager.NetworkCallback() {
		
		private val activeNetworks: MutableList<Network> = mutableListOf()
		
		@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
		override fun onLost(network: Network) {
			super.onLost(network)
			logWarn(this@ConnectionManager.javaClass, "onLost")
			
			// Remove network from active network list
			activeNetworks.removeAll { activeNetwork -> activeNetwork == network }
			callback.invoke(activeNetworks.isNotEmpty())
		}
		
		@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
		override fun onAvailable(network: Network) {
			super.onAvailable(network)
			logInfo(this@ConnectionManager.javaClass, "onAvailable")
			
			// Add to list of active networks if not already in list
			if (activeNetworks.none { activeNetwork -> activeNetwork == network }) {
				activeNetworks.add(network)
			}
			callback.invoke(activeNetworks.isNotEmpty())
		}
		
		override fun onCapabilitiesChanged(
			network: Network,
			networkCapabilities: NetworkCapabilities
		) {
			super.onCapabilitiesChanged(network, networkCapabilities)
			logDebug(this@ConnectionManager.javaClass, "onCapabilitiesChanged")
			//lastInternetConnectionCheck()
		}
		
		override fun onLosing(network: Network, maxMsToLive: Int) {
			super.onLosing(network, maxMsToLive)
			logWarn(this@ConnectionManager.javaClass, "onLosing")
			//lastInternetConnectionCheck()
		}
		
		override fun onUnavailable() {
			super.onUnavailable()
			logWarn(this@ConnectionManager.javaClass, "onUnavailable")
			//lastInternetConnectionCheck()
		}
		
	}
	
}