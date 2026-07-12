package com.lmstudio.chat.di

import android.content.Context
import androidx.room.Room
import com.lmstudio.chat.data.local.db.AppDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "lmstudio_chat.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideConversationDao(db: AppDatabase) = db.conversationDao()

    @Provides
    fun provideMessageDao(db: AppDatabase) = db.messageDao()

    @Provides
    fun providePersonaDao(db: AppDatabase) = db.personaDao()

    @Provides
    fun providePromptDao(db: AppDatabase) = db.promptDao()
}
