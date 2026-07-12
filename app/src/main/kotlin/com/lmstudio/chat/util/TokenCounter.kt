package com.lmstudio.chat.util

object TokenCounter {
    fun estimateTokens(text: String): Int {
        return (text.length / 4).coerceAtLeast(1)
    }

    fun estimateTokensForMessages(messages: List<Pair<String, String>>): Int {
        return messages.sumOf { (role, content) ->
            estimateTokens("$role: $content") + 4
        } + 3
    }

    fun formatTokenCount(count: Int): String {
        return when {
            count < 1000 -> "$count tokens"
            else -> "${count / 1000}k tokens"
        }
    }
}
