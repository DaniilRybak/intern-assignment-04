package com.example.intern_assignment_04.feature.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

internal fun initFeatureKoin(): KoinApplication {
    return startKoin {
        modules(featureModule)
    }
}

