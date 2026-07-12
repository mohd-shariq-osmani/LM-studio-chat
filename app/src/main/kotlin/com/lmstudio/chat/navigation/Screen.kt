package com.lmstudio.chat.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Chat : Screen("chat/{conversationId}") {
        fun createRoute(conversationId: Long) = "chat/$conversationId"
        const val ARG_CONVERSATION_ID = "conversationId"
    }
    object NewChat : Screen("new_chat")
    object Conversations : Screen("conversations")
    object Personas : Screen("personas")
    object PersonaDetail : Screen("persona_detail/{personaId}") {
        fun createRoute(personaId: Long = -1L) = "persona_detail/$personaId"
        const val ARG_PERSONA_ID = "personaId"
    }
    object Prompts : Screen("prompts")
    object Settings : Screen("settings")
    object Search : Screen("search")
}
