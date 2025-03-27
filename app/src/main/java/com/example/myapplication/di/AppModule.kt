package com.example.myapplication.di

import com.example.myapplication.data.AuthRepository
import com.example.myapplication.viewmodel.IAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract  fun bindAuthRepository(
        authRepository: AuthRepository
    ): IAuthRepository
}