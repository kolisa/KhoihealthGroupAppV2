package com.khoihealth.app.core.di

import android.content.Context
import com.khoihealth.app.auth.data.AuthRepositoryImpl
import com.khoihealth.app.auth.domain.repository.AuthRepository
import com.khoihealth.app.devices.data.DeviceRepositoryImpl
import com.khoihealth.app.devices.domain.repository.DeviceRepository
import com.khoihealth.app.goals.data.GoalsRepositoryImpl
import com.khoihealth.app.goals.domain.repository.GoalsRepository
import com.khoihealth.app.health.data.HealthRepositoryImpl
import com.khoihealth.app.health.domain.repository.HealthRepository
import com.khoihealth.app.reports.data.ReportsRepositoryImpl
import com.khoihealth.app.reports.domain.repository.ReportsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDeviceRepository(impl: DeviceRepositoryImpl): DeviceRepository

    @Binds
    @Singleton
    abstract fun bindHealthRepository(impl: HealthRepositoryImpl): HealthRepository

    @Binds
    @Singleton
    abstract fun bindGoalsRepository(impl: GoalsRepositoryImpl): GoalsRepository

    @Binds
    @Singleton
    abstract fun bindReportsRepository(impl: ReportsRepositoryImpl): ReportsRepository
}
