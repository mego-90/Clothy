package com.mego.clothy.di

import com.mego.clothy.data.ItemsDataSource
import com.mego.clothy.data.ItemsRepository
import com.mego.clothy.framework.room.ItemsDAO
import com.mego.clothy.framework.room.ItemsRoomDatasource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
object RepositoryModule {

    @Provides
    fun provideItemsRepository(itemsRoomDataSource: ItemsRoomDatasource) =
        ItemsRepository(itemsRoomDataSource)

    @Provides
    fun provideItemsRoomDatasource(itemsDAO: ItemsDAO) =
        ItemsRoomDatasource(itemsDAO)


}