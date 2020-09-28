/*
 * Created by Andrii Kovalchuk
 * Copyright (c) 2020. All rights reserved.
 * Last modified 28.09.2020 18:01
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.mmdev.me.driver.core

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.cioccarellia.ksprefs.KsPrefs
import com.mmdev.me.driver.core.di.DataSourceLocalModule
import com.mmdev.me.driver.core.di.DataSourceRemoteModule
import com.mmdev.me.driver.core.di.DatabaseModule
import com.mmdev.me.driver.core.di.FirebaseModule
import com.mmdev.me.driver.core.di.NetworkModule
import com.mmdev.me.driver.core.di.RepositoryModule
import com.mmdev.me.driver.core.di.ViewModelsModule
import com.mmdev.me.driver.core.utils.Language
import com.mmdev.me.driver.core.utils.Language.ENGLISH
import com.mmdev.me.driver.core.utils.MetricSystem
import com.mmdev.me.driver.core.utils.MetricSystem.KILOMETERS
import com.mmdev.me.driver.core.utils.helpers.LocaleHelper
import com.mmdev.me.driver.core.utils.helpers.ThemeHelper
import com.mmdev.me.driver.core.utils.helpers.ThemeHelper.ThemeMode
import com.mmdev.me.driver.core.utils.helpers.ThemeHelper.ThemeMode.LIGHT_MODE
import com.mmdev.me.driver.core.utils.log.DebugConfig
import com.mmdev.me.driver.core.utils.log.MyLogger
import com.mmdev.me.driver.core.utils.log.logDebug
import com.mmdev.me.driver.core.utils.log.logInfo
import com.mmdev.me.driver.domain.user.UserModel
import com.mmdev.me.driver.domain.vehicle.model.Vehicle
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Contains dependency modules
 * From top to bottom representing DI tree
 * For example: [ViewModelsModule] depends on [RepositoryModule]
 * [RepositoryModule] depends on [DataSourceLocalModule] and [DataSourceRemoteModule]
 * and so on.
 */

class MedriverApp : Application() {
	
	
	companion object {
		private const val TAG = "mylogs_MEDRIVERAPP"
		
		private const val PREFERENCES_NAME = "settings"
		
		private const val THEME_MODE_KEY = "theme_mode"
		private const val METRIC_SYSTEM_KEY = "metric_system"
		private const val LANGUAGE_KEY = "language"
		private const val VEHICLE_VIN_CODE_KEY = "vehicle_vin"
		
		private lateinit var appContext: Context
		val prefs by lazy {
			KsPrefs(appContext, PREFERENCES_NAME) {
				//	encryptionType = EncryptionType.KeyStore("key")
			}
		}
		
		// public because the initial UI is built from these values
		// and some of these values are used across application
		
		var themeMode: ThemeMode = LIGHT_MODE
			set (value) {
				field = value
				prefs.push(THEME_MODE_KEY, value)
				logDebug(TAG, "AppTheme changed.")
				ThemeHelper.applyTheme(value)
			}
		
		var metricSystem: MetricSystem = KILOMETERS
			set (value) {
				field = value
				prefs.push(METRIC_SYSTEM_KEY, value)
				logDebug(TAG, "Metric system changed.")
			}
		
		var appLanguage: Language = ENGLISH
			set (value) {
				field = value
				prefs.push(LANGUAGE_KEY, value)
				logDebug(TAG, "Language changed.")
			}
		
		
		var currentVehicleVinCode: String = ""
			set (value) {
				if (field != value) {
					field = value
					prefs.push(VEHICLE_VIN_CODE_KEY, value)
					logDebug(TAG, "Vehicle vin changed.")
				}
			}
		
		@Volatile
		var currentUser: UserModel? = null
			@Synchronized set
		
		
		@Volatile
		var currentVehicle: Vehicle? = null
			@Synchronized set
	
		
		@Volatile
		var debug: DebugConfig = DebugConfig.Default

		/**
		 * Enable or disable [Application] debug mode.
		 * enabled by default
		 *
		 * @param enabled enable the debug mode.
		 * @param logger logging implementation.
		 */
		fun debugMode(enabled: Boolean, logger: MyLogger) {
			debug = object: DebugConfig {
				override val isEnabled = enabled
				override val logger = logger
			}
		}
	}
	
	
	
	override fun onCreate() {
		
		appContext = applicationContext
		
		//initKoin
		startKoin {
			androidContext(this@MedriverApp)
			if (debug.isEnabled) androidLogger()
			koin.loadModules(
				// The lines on which modules are written represents
				// some kind of "layers" inside dependencies.
				listOf(
					ViewModelsModule,
					RepositoryModule,
					DataSourceRemoteModule, DataSourceLocalModule,
					NetworkModule, FirebaseModule, DatabaseModule
				)
			)
			koin.createRootScope()
		}
		
		/** if not exists - apply [LIGHT_MODE] as default theme */
		themeMode = loadInitialPropertyOrPushDefault(THEME_MODE_KEY, LIGHT_MODE).also {
			ThemeHelper.applyTheme(it)
		}
		
		/** if not exists - apply [KILOMETERS] as default metric system and save */
		metricSystem = loadInitialPropertyOrPushDefault(key = METRIC_SYSTEM_KEY, default = KILOMETERS)
		
		currentVehicleVinCode = loadInitialPropertyOrPushDefault(key = VEHICLE_VIN_CODE_KEY, default = "")
		
		/** if not exists - apply [ENGLISH] language as app default */
		appLanguage = loadInitialPropertyOrPushDefault(key = LANGUAGE_KEY, default = ENGLISH)
		
		super.onCreate()
		logInfo(TAG, "init theme mode - $themeMode")
		logInfo(TAG, "init metric system - ${metricSystem.name}")
		logInfo(TAG, "init language - ${appLanguage.name}")
		logInfo(TAG, "init vehicle - $currentVehicle")
	}
	
	//called only on app startup to pull saved value or assign default & also write default to prefs
	private inline fun <reified T : Any> loadInitialPropertyOrPushDefault(key: String, default: T): T {
		return if (prefs.exists(key)) {
			prefs.pull(key)
		}
		else {
			prefs.push(key, default)
			default
		}
	}
	
	override fun attachBaseContext(base: Context) {
		super.attachBaseContext(LocaleHelper.newLocaleContext(base, appLanguage))
	}
	
	/**
	 * When user decides to change system device language it will override application settings
	 * so its a fix
	 */
	override fun onConfigurationChanged(newConfig: Configuration) {
		super.onConfigurationChanged(newConfig)
		LocaleHelper.overrideLocale(this, appLanguage)
	}
	
}