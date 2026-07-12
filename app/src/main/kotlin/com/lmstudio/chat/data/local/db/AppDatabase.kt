package com.lmstudio.chat.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lmstudio.chat.data.local.entities.ConversationEntity
import com.lmstudio.chat.data.local.entities.MessageEntity
import com.lmstudio.chat.data.local.entities.PersonaEntity
import com.lmstudio.chat.data.local.entities.PromptEntity

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        PersonaEntity::class,
        PromptEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun personaDao(): PersonaDao
    abstract fun promptDao(): PromptDao
}
