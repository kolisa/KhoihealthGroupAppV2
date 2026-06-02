package com.khoihealth.app.core.di

import android.content.Context
import androidx.room.Room
import com.khoihealth.app.core.database.KhoiHealthDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KhoiHealthDatabase =
        Room.databaseBuilder(
            context,
            KhoiHealthDatabase::class.java,
            KhoiHealthDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideDeviceDao(db: KhoiHealthDatabase) = db.deviceDao()

    @Provides
    fun provideHealthDao(db: KhoiHealthDatabase) = db.healthDao()

    @Provides
    fun provideGoalDao(db: KhoiHealthDatabase) = db.goalDao()
}
