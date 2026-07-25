package com.lmstudio.chat.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lmstudio.chat.theme.*
import com.lmstudio.chat.util.applePressEffect

enum class BottomTab {
    HOME, CHATS, PERSONAS, SETTINGS
}

@Composable
fun LmBottomBar(
    currentTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(32.dp),
            color = Color(0xF21C1C1E),
            border = androidx.compose.foundation.BorderStroke(0.5.dp, GlassBorder.copy(alpha = 0.4f)),
            shadowElevation = 12.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavItem(
                    label = "Home",
                    icon = Icons.Default.Home,
                    isSelected = currentTab == BottomTab.HOME,
                    onClick = { onTabSelected(BottomTab.HOME) }
                )
                BottomNavItem(
                    label = "Chats",
                    icon = Icons.AutoMirrored.Filled.Chat,
                    isSelected = currentTab == BottomTab.CHATS,
                    onClick = { onTabSelected(BottomTab.CHATS) }
                )
                BottomNavItem(
                    label = "Personas",
                    icon = Icons.Default.Face,
                    isSelected = currentTab == BottomTab.PERSONAS,
                    onClick = { onTabSelected(BottomTab.PERSONAS) }
                )
                BottomNavItem(
                    label = "Settings",
                    icon = Icons.Default.Settings,
                    isSelected = currentTab == BottomTab.SETTINGS,
                    onClick = { onTabSelected(BottomTab.SETTINGS) }
                )
            }
        }
    }
}

@Composable
private fun RowScope.BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) AccentPrimary else TextTertiary,
        animationSpec = tween(200),
        label = "TabColor"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .applePressEffect(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = animatedColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = animatedColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(AccentPrimary)
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
