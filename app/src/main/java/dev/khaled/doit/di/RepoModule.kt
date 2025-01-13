package dev.khaled.doit.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.khaled.doit.data.repo.AuthRepo
import dev.khaled.doit.data.repo.AuthRepoImpl
import dev.khaled.doit.data.repo.TaskRepo
import dev.khaled.doit.data.repo.TaskRepoImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences,
        gson: Gson
    ): AuthRepo {
        return AuthRepoImpl(auth,database,appPreferences,gson)
    }

    @Provides
    @Singleton
    fun ProvidesTaskRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
    ): TaskRepo {
        return TaskRepoImpl(auth,database)
    }
}