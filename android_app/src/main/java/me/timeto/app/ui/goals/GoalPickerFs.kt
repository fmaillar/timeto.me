package me.timeto.app.ui.goals

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.timeto.app.ui.ZStack
import me.timeto.app.ui.rememberVm
import me.timeto.app.ui.Screen
import me.timeto.app.ui.form.plain.FormPlainButtonSelection
import me.timeto.app.ui.form.plain.FormPlainPaddingTop
import me.timeto.app.ui.header.Header
import me.timeto.app.ui.header.HeaderActionButton
import me.timeto.app.ui.header.HeaderCancelButton
import me.timeto.app.ui.navigation.LocalNavigationLayer
import me.timeto.shared.db.GoalDb
import me.timeto.shared.vm.goals.GoalPickerVm

@Composable
fun GoalPickerFs(
    initGoalDb: GoalDb?,
    excludeGoalId: Int? = null,
    onDone: (GoalDb?) -> Unit,
) {

    val navigationLayer = LocalNavigationLayer.current

    val (_, state) = rememberVm {
        GoalPickerVm(excludeGoalId = excludeGoalId)
    }

    Screen {

        val scrollState = rememberLazyListState()

        Header(
            title = "Parent Goal",
            scrollState = scrollState,
            actionButton = HeaderActionButton(
                text = "None",
                isEnabled = true,
                onClick = {
                    onDone(null)
                    navigationLayer.close()
                },
            ),
            cancelButton = HeaderCancelButton(
                text = "Cancel",
                onClick = {
                    navigationLayer.close()
                },
            ),
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f),
            state = scrollState,
        ) {

            item {
                FormPlainPaddingTop()
            }

            state.goalsUi.forEachIndexed { idx, goalUi ->
                val goalDb: GoalDb = goalUi.goalDb
                item(key = goalDb.id) {
                    FormPlainButtonSelection(
                        title = goalUi.title,
                        isSelected = goalDb.id == initGoalDb?.id,
                        isFirst = idx == 0,
                        modifier = Modifier,
                        onClick = {
                            onDone(goalDb)
                            navigationLayer.close()
                        },
                    )
                }
            }

            item {
                ZStack(Modifier.navigationBarsPadding())
            }
        }
    }
}
