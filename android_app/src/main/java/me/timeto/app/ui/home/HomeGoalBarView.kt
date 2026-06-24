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
import me.timeto.app.toColor
import me.timeto.app.ui.c
import me.timeto.app.ui.H_PADDING
import me.timeto.shared.vm.home.GoalBarVm
import me.timeto.shared.vm.home.GoalGrade

@Composable
fun HomeGoalBarView(
    vm: GoalBarVm,
) {
    val state = vm.state.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = H_PADDING),
    ) {
        // Goal bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(state.grade.color.toColor().copy(alpha = 0.15f))
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
                    color = state.grade.color.toColor(),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "weighted min",
                    color = state.grade.color.toColor().copy(alpha = 0.6f),
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