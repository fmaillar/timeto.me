package me.timeto.app.ui.goals.form

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.timeto.app.ui.ZStack
import me.timeto.app.ui.Screen
import me.timeto.app.ui.form.plain.FormPlainButtonSelection
import me.timeto.app.ui.form.plain.FormPlainPaddingTop
import me.timeto.app.ui.header.Header
import me.timeto.app.ui.header.HeaderActionButton
import me.timeto.app.ui.header.HeaderCancelButton
import me.timeto.app.ui.navigation.LocalNavigationLayer

private val importanceOptions = listOf(
    25, 50, 75, 100, 125, 150, 175, 200
)

@Composable
fun ImportancePickerFs(
    initImportance: Int?,
    onDone: (Int?) -> Unit,
) {

    val navigationLayer = LocalNavigationLayer.current

    Screen {

        val scrollState = rememberLazyListState()

        Header(
            title = "Importance",
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

            importanceOptions.forEachIndexed { idx, importance ->
                item(key = importance) {
                    FormPlainButtonSelection(
                        title = "$importance%",
                        isSelected = importance == initImportance,
                        isFirst = idx == 0,
                        modifier = Modifier,
                        onClick = {
                            onDone(importance)
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
