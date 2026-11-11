package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DisplayFormulaColor
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonOrange

@Composable
fun MemoryToolbar(
    hasMemory: Boolean,
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySubtract: () -> Unit,
    onMemoryStore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated.copy(alpha = 0.4f))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Memory status indicator
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (hasMemory) NeonOrange.copy(alpha = 0.2f) else Color.Transparent)
                .padding(horizontal = 8.dp, vertical = 2.dp)
                .testTag("memory_indicator"),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "M",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (hasMemory) NeonOrange else DisplayFormulaColor.copy(alpha = 0.3f)
            )
        }

        MemoryTextButton(
            text = "MC",
            enabled = hasMemory,
            onClick = onMemoryClear,
            testTag = "btn_mc"
        )

        MemoryTextButton(
            text = "MR",
            enabled = hasMemory,
            onClick = onMemoryRecall,
            testTag = "btn_mr"
        )

        MemoryTextButton(
            text = "M+",
            enabled = true,
            onClick = onMemoryAdd,
            testTag = "btn_m_plus"
        )

        MemoryTextButton(
            text = "M-",
            enabled = true,
            onClick = onMemorySubtract,
            testTag = "btn_m_minus"
        )

        MemoryTextButton(
            text = "MS",
            enabled = true,
            onClick = onMemoryStore,
            testTag = "btn_ms"
        )
    }
}

@Composable
private fun MemoryTextButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (enabled) NeonCyan else DisplayFormulaColor.copy(alpha = 0.3f)
        )
    }
}
