package me.timeto.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.timeto.app.ui.c
import me.timeto.app.ui.H_PADDING
import me.timeto.shared.ColorRgba
import me.timeto.shared.vm.home.TargetBarVm
import me.timeto.shared.vm.home.TargetGrade

@Composable
fun HomeTargetBarView(
    vm: TargetBarVm,
) {
    val state = vm.state.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = H_PADDING),
    ) {
        // Target bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(state.grade.color.toComposeColor().copy(alpha = 0.15f))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Emoji
            Text(
                text = state.gradeEmoji,
                fontSize = 20.sp,
            )

            Spacer(Modifier.width(8.dp))

            // Weighted total (prominent)
            Column {
                Text(
                    text = state.weightedTotalText,
                    color = state.grade.color.toComposeColor(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "weighted min",
                    color = state.grade.color.toComposeColor().copy(alpha = 0.6f),
                    fontSize = 10.sp,
                )
            }

            Spacer(Modifier.weight(1f))

            // Actual total (less prominent)
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = state.actualTotalText,
                    color = c.text.copy(alpha = 0.5f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "actual min",
                    color = c.text.copy(alpha = 0.3f),
                    fontSize = 10.sp,
                )
            }
        }
    }
}

// Extension to convert shared ColorRgba to Compose Color
private fun ColorRgba.toComposeColor(): androidx.compose.ui.graphics.Color {
    return androidx.compose.ui.graphics.Color(
        red = r / 255f,
        green = g / 255f,
        blue = b / 255f,
        alpha = 1f,
    )
}