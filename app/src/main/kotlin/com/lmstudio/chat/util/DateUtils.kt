package com.lmstudio.chat.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatTimestamp(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return when {
            diff < 60_000 -> "Just now"
            diff < 3600_000 -> "${diff / 60_000}m ago"
            diff < 86400_000 -> "${diff / 3600_000}h ago"
            diff < 604800_000 -> {
                val sdf = SimpleDateFormat("EEE", Locale.getDefault())
                sdf.format(Date(timestamp))
            }
            else -> {
                val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
                sdf.format(Date(timestamp))
            }
        }
    }

    fun formatFullTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM d, yyyy 'at' h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun getGreeting(): String {
        return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            else -> "Good evening"
        }
    }
}
