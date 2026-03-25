package com.example.intern_assignment_04.feature.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initFeatureKoin(): KoinApplication {
    return startKoin {
        modules(featureModule)
    }
}
