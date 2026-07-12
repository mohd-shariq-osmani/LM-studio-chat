package com.lmstudio.chat.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lmstudio.chat.theme.AccentPrimary
import com.lmstudio.chat.theme.TextPrimary

@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    textColor: androidx.compose.ui.graphics.Color = TextPrimary
) {
    val lines = text.split("\n")
    var i = 0
    val segments = mutableListOf<MarkdownSegment>()

    while (i < lines.size) {
        val line = lines[i]
        when {
            line.trimStart().startsWith("```") -> {
                val lang = line.trim().removePrefix("```").trim()
                val codeLines = mutableListOf<String>()
                i++
                while (i < lines.size && !lines[i].trimStart().startsWith("```")) {
                    codeLines.add(lines[i])
                    i++
                }
                segments.add(MarkdownSegment.Code(codeLines.joinToString("\n"), lang))
            }
            line.startsWith("# ") -> segments.add(MarkdownSegment.Heading(line.removePrefix("# "), 1))
            line.startsWith("## ") -> segments.add(MarkdownSegment.Heading(line.removePrefix("## "), 2))
            line.startsWith("### ") -> segments.add(MarkdownSegment.Heading(line.removePrefix("### "), 3))
            line.trimStart().startsWith("- ") || line.trimStart().startsWith("* ") ->
                segments.add(MarkdownSegment.Bullet(line.trimStart().removePrefix("- ").removePrefix("* ")))
            line.trimStart().matches(Regex("^\\d+\\. .*")) ->
                segments.add(MarkdownSegment.Bullet(line.trimStart().replace(Regex("^\\d+\\. "), "")))
            line.trim() == "---" || line.trim() == "***" -> segments.add(MarkdownSegment.Divider)
            line.isBlank() -> segments.add(MarkdownSegment.Blank)
            else -> segments.add(MarkdownSegment.Paragraph(line))
        }
        i++
    }

    Column(modifier = modifier) {
        segments.forEach { segment ->
            when (segment) {
                is MarkdownSegment.Code -> {
                    Spacer(Modifier.height(8.dp))
                    CodeBlock(code = segment.code, language = segment.language)
                    Spacer(Modifier.height(8.dp))
                }
                is MarkdownSegment.Heading -> {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = parseInlineMarkdown(segment.text),
                        style = when (segment.level) {
                            1 -> MaterialTheme.typography.headlineSmall
                            2 -> MaterialTheme.typography.titleLarge
                            else -> MaterialTheme.typography.titleMedium
                        },
                        color = textColor
                    )
                    Spacer(Modifier.height(4.dp))
                }
                is MarkdownSegment.Bullet -> {
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text(text = "•  ", color = AccentPrimary, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = parseInlineMarkdown(segment.text),
                            style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                        )
                    }
                }
                is MarkdownSegment.Paragraph -> {
                    Text(
                        text = parseInlineMarkdown(segment.text),
                        style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                    )
                }
                MarkdownSegment.Blank -> Spacer(Modifier.height(6.dp))
                MarkdownSegment.Divider -> {
                    Spacer(Modifier.height(8.dp))
                    androidx.compose.material3.HorizontalDivider(
                        color = com.lmstudio.chat.theme.OutlineSubtle
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

sealed class MarkdownSegment {
    data class Code(val code: String, val language: String) : MarkdownSegment()
    data class Heading(val text: String, val level: Int) : MarkdownSegment()
    data class Bullet(val text: String) : MarkdownSegment()
    data class Paragraph(val text: String) : MarkdownSegment()
    object Blank : MarkdownSegment()
    object Divider : MarkdownSegment()
}

fun parseInlineMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var remaining = text
        while (remaining.isNotEmpty()) {
            when {
                remaining.startsWith("***") && remaining.indexOf("***", 3) != -1 -> {
                    val end = remaining.indexOf("***", 3)
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) {
                        append(remaining.substring(3, end))
                    }
                    remaining = remaining.substring(end + 3)
                }
                remaining.startsWith("**") && remaining.indexOf("**", 2) != -1 -> {
                    val end = remaining.indexOf("**", 2)
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = TextPrimary)) {
                        append(remaining.substring(2, end))
                    }
                    remaining = remaining.substring(end + 2)
                }
                remaining.startsWith("*") && remaining.indexOf("*", 1) != -1 -> {
                    val end = remaining.indexOf("*", 1)
                    withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(remaining.substring(1, end))
                    }
                    remaining = remaining.substring(end + 1)
                }
                remaining.startsWith("`") && remaining.indexOf("`", 1) != -1 -> {
                    val end = remaining.indexOf("`", 1)
                    withStyle(
                        SpanStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            background = com.lmstudio.chat.theme.CodeBackground,
                            color = AccentPrimary
                        )
                    ) {
                        append(" ${remaining.substring(1, end)} ")
                    }
                    remaining = remaining.substring(end + 1)
                }
                else -> {
                    val nextSpecial = listOf(
                        remaining.indexOf("***", 1).takeIf { it != -1 } ?: Int.MAX_VALUE,
                        remaining.indexOf("**", 1).takeIf { it != -1 } ?: Int.MAX_VALUE,
                        remaining.indexOf("*", 1).takeIf { it != -1 } ?: Int.MAX_VALUE,
                        remaining.indexOf("`", 1).takeIf { it != -1 } ?: Int.MAX_VALUE
                    ).min()
                    if (nextSpecial == Int.MAX_VALUE) {
                        append(remaining)
                        remaining = ""
                    } else {
                        append(remaining.substring(0, nextSpecial))
                        remaining = remaining.substring(nextSpecial)
                    }
                }
            }
        }
    }
}
