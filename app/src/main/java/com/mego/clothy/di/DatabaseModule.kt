package com.mego.clothy.di

import android.content.Context
import androidx.room.Room
import com.mego.clothy.framework.room.ClothyDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideClothyDAO(clothyDatabase: ClothyDatabase) =
        clothyDatabase.itemsDAO

    @Provides
    @Singleton
    fun provideClothyDatabase( @ApplicationContext context: Context) =
        Room
            .databaseBuilder(context, ClothyDatabase::class.java, ClothyDatabase.databaseName)
            .build()


}