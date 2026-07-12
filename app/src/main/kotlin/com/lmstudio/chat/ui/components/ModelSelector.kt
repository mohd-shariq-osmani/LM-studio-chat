package com.lmstudio.chat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.lmstudio.chat.domain.model.ModelInfo
import com.lmstudio.chat.theme.*

@Composable
fun ModelSelector(
    models: List<ModelInfo>,
    selectedModel: String,
    onModelSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedModelInfo = models.find { it.id == selectedModel }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceVariant)
                .border(1.dp, OutlineSubtle, RoundedCornerShape(12.dp))
                .clickable { if (!isLoading) expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Memory,
                contentDescription = null,
                tint = AccentPrimary,
                modifier = Modifier.size(18.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                if (isLoading) {
                    Text(
                        text = "Loading models...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary
                    )
                } else if (selectedModelInfo != null) {
                    Text(
                        text = selectedModelInfo.name.ifBlank { selectedModelInfo.id },
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    selectedModelInfo.contextLength?.let { ctx ->
                        Text(
                            text = "${ctx / 1000}k context",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                    }
                } else {
                    Text(
                        text = if (models.isEmpty()) "No models found" else "Select a model",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary
                    )
                }
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = AccentPrimary
                )
            } else {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        AnimatedVisibility(visible = expanded && models.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineSubtle)
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 300.dp)
                ) {
                    items(models) { model ->
                        ModelItem(
                            model = model,
                            isSelected = model.id == selectedModel,
                            onClick = {
                                onModelSelected(model.id)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModelItem(
    model: ModelInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isSelected) AccentPrimaryDim.copy(alpha = 0.3f) else androidx.compose.ui.graphics.Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = model.name.ifBlank { model.id },
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) AccentPrimary else TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (model.id != model.name && model.name.isNotBlank()) {
                Text(
                    text = model.id,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            model.contextLength?.let { ctx ->
                Text(
                    text = "${ctx / 1000}k context",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AccentPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
