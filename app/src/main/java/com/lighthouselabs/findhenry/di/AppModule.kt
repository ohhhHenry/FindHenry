package com.lighthouselabs.findhenry.di

import org.koin.dsl.module
import com.polidea.rxandroidble2.RxBleClient
import org.koin.android.ext.koin.androidContext


val appModule = module(override = true) {

    single<RxBleClient> { RxBleClient.create(androidContext())}

}