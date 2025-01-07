package com.helloyogesh.mycanvas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LightDarkSwitch(
    isLightMode: Boolean,
    onModeChange: (Boolean) -> Unit
) {
    //val backgroundColor = if (isLightMode) Color.White else Color.Black
    val lightTextColor = if (isLightMode) Color.Black else Color.Gray
    val darkTextColor = if (isLightMode) Color.Gray else Color.White

    Box(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Gray.copy(alpha = 0.2f))
            .height(40.dp)
            .width(120.dp)
            .clickable { onModeChange(!isLightMode) },

        ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Light Mode Section
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isLightMode) Color.White else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Light",
                    color = lightTextColor,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Dark Mode Section
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (!isLightMode) Color.Black else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Dark",
                    color = darkTextColor,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}