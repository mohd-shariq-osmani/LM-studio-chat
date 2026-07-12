package com.lmstudio.chat.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lmstudio.chat.ui.chat.ChatScreen
import com.lmstudio.chat.ui.conversations.ConversationsScreen
import com.lmstudio.chat.ui.home.HomeScreen
import com.lmstudio.chat.ui.personas.PersonaDetailScreen
import com.lmstudio.chat.ui.personas.PersonasScreen
import com.lmstudio.chat.ui.prompts.PromptsScreen
import com.lmstudio.chat.ui.search.SearchScreen
import com.lmstudio.chat.ui.settings.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            fadeIn(animationSpec = tween(220)) +
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(220))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(180)) +
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(180))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(220)) +
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(220))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(180)) +
                slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(180))
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToChat = { navController.navigate(Screen.Chat.createRoute(it)) },
                onNavigateToNewChat = { navController.navigate(Screen.NewChat.route) },
                onNavigateToConversations = { navController.navigate(Screen.Conversations.route) },
                onNavigateToPersonas = { navController.navigate(Screen.Personas.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToPrompts = { navController.navigate(Screen.Prompts.route) }
            )
        }

        composable(Screen.NewChat.route) {
            ChatScreen(
                conversationId = null,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToPersonas = { navController.navigate(Screen.Personas.route) }
            )
        }

        composable(
            route = Screen.Chat.route,
            arguments = listOf(navArgument(Screen.Chat.ARG_CONVERSATION_ID) { type = NavType.LongType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getLong(Screen.Chat.ARG_CONVERSATION_ID)
            ChatScreen(
                conversationId = conversationId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToPersonas = { navController.navigate(Screen.Personas.route) }
            )
        }

        composable(Screen.Conversations.route) {
            ConversationsScreen(
                onNavigateToChat = { navController.navigate(Screen.Chat.createRoute(it)) },
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToNewChat = { navController.navigate(Screen.NewChat.route) }
            )
        }

        composable(Screen.Personas.route) {
            PersonasScreen(
                onNavigateToPersonaDetail = { navController.navigate(Screen.PersonaDetail.createRoute(it)) },
                onNavigateToCreatePersona = { navController.navigate(Screen.PersonaDetail.createRoute()) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PersonaDetail.route,
            arguments = listOf(navArgument(Screen.PersonaDetail.ARG_PERSONA_ID) { type = NavType.LongType })
        ) { backStackEntry ->
            val personaId = backStackEntry.arguments?.getLong(Screen.PersonaDetail.ARG_PERSONA_ID)
            PersonaDetailScreen(
                personaId = if (personaId == -1L) null else personaId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Prompts.route) {
            PromptsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToChat = { navController.navigate(Screen.Chat.createRoute(it)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
