package com.lmstudio.chat.di

import com.lmstudio.chat.data.repository.ChatRepositoryImpl
import com.lmstudio.chat.data.repository.ConversationRepositoryImpl
import com.lmstudio.chat.data.repository.ModelRepositoryImpl
import com.lmstudio.chat.data.repository.PersonaRepositoryImpl
import com.lmstudio.chat.data.repository.PromptRepositoryImpl
import com.lmstudio.chat.data.repository.SettingsRepositoryImpl
import com.lmstudio.chat.domain.repository.ChatRepository
import com.lmstudio.chat.domain.repository.ConversationRepository
import com.lmstudio.chat.domain.repository.ModelRepository
import com.lmstudio.chat.domain.repository.PersonaRepository
import com.lmstudio.chat.domain.repository.PromptRepository
import com.lmstudio.chat.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds @Singleton
    abstract fun bindConversationRepository(impl: ConversationRepositoryImpl): ConversationRepository

    @Binds @Singleton
    abstract fun bindModelRepository(impl: ModelRepositoryImpl): ModelRepository

    @Binds @Singleton
    abstract fun bindPersonaRepository(impl: PersonaRepositoryImpl): PersonaRepository

    @Binds @Singleton
    abstract fun bindPromptRepository(impl: PromptRepositoryImpl): PromptRepository

    @Binds @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
