package me.timeto.app.ui.activities.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import me.timeto.app.ui.HStack
import me.timeto.app.ui.ZStack
import me.timeto.app.ui.c
import me.timeto.app.ui.rememberVm
import me.timeto.app.ui.roundedShape
import me.timeto.app.toColor
import me.timeto.app.ui.Screen
import me.timeto.app.ui.checklists.ChecklistsPickerFs
import me.timeto.app.ui.color_picker.ColorPickerFs
import me.timeto.app.ui.emoji.EmojiPickerFs
import me.timeto.app.ui.form.button.FormButton
import me.timeto.app.ui.form.FormInput
import me.timeto.app.ui.form.padding.FormPaddingBottom
import me.timeto.app.ui.form.padding.FormPaddingSectionSection
import me.timeto.app.ui.form.padding.FormPaddingHeaderSection
import me.timeto.app.ui.form.padding.FormPaddingSectionHeader
import me.timeto.app.ui.form.padding.FormPaddingTop
import me.timeto.app.ui.form.FormSwitch
import me.timeto.app.ui.form.FormHeader
import me.timeto.app.ui.form.button.FormButtonArrowView
import me.timeto.app.ui.form.button.FormButtonEmoji
import me.timeto.app.ui.form.button.FormButtonView
import me.timeto.app.ui.header.Header
import me.timeto.app.ui.header.HeaderActionButton
import me.timeto.app.ui.header.HeaderCancelButton
import me.timeto.app.ui.navigation.LocalNavigationFs
import me.timeto.app.ui.navigation.LocalNavigationLayer
import me.timeto.app.ui.navigation.picker.NavigationPickerItem
import me.timeto.app.ui.shortcuts.ShortcutsPickerFs
import me.timeto.shared.db.ActivityCategoryDb
import me.timeto.shared.db.ActivityDb
import me.timeto.shared.db.CategoryDb
import me.timeto.shared.vm.activities.form.ActivityFormVm
import me.timeto.shared.Cache

@Composable
fun ActivityFormFs(
    initActivityDb: ActivityDb?,
) {

    val navigationFs = LocalNavigationFs.current
    val navigationLayer = LocalNavigationLayer.current

    val (vm, state) = rememberVm {
        ActivityFormVm(
            initActivityDb = initActivityDb,
        )
    }

    Screen(
        modifier = Modifier
            .imePadding(),
    ) {

        val scrollState = rememberLazyListState()

        Header(
            title = state.title,
            scrollState = scrollState,
            actionButton = HeaderActionButton(
                text = state.doneText,
                isEnabled = true,
                onClick = {
                    vm.save(
                        dialogsManager = navigationFs,
                        onSave = {
                            navigationLayer.close()
                        },
                    )
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
                .fillMaxSize(),
            state = scrollState,
        ) {

            item {

                FormPaddingTop()

                FormInput(
                    initText = state.name,
                    placeholder = state.namePlaceholder,
                    onChange = { newName ->
                        vm.setName(newName)
                    },
                    isFirst = true,
                    isLast = true,
                    isAutoFocus = false,
                    imeAction = ImeAction.Done,
                )

                FormPaddingSectionSection()

                fun openEmojiFs() {
                    navigationFs.push {
                        EmojiPickerFs(
                            onDone = { newEmoji ->
                                vm.setEmoji(newEmoji = newEmoji)
                            },
                        )
                    }
                }

                val emoji: String? = state.emoji
                if (emoji != null) {
                    FormButtonEmoji(
                        title = state.emojiTitle,
                        emoji = emoji,
                        isFirst = true,
                        isLast = false,
                        onClick = {
                            openEmojiFs()
                        },
                    )
                } else {
                    FormButton(
                        title = state.emojiTitle,
                        isFirst = true,
                        isLast = false,
                        note = state.emojiNotSelected,
                        noteColor = c.red,
                        withArrow = true,
                        onClick = {
                            openEmojiFs()
                        },
                    )
                }

                FormButtonView(
                    title = state.colorTitle,
                    titleColor = null,
                    isFirst = false,
                    isLast = true,
                    modifier = Modifier,
                    rightView = {
                        HStack(
                            verticalAlignment = CenterVertically,
                        ) {
                            ZStack(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .size(28.dp)
                                    .clip(roundedShape)
                                    .background(state.colorRgba.toColor()),
                            )
                            FormButtonArrowView()
                        }
                    },
                    onClick = {
                        navigationFs.push {
                            ColorPickerFs(
                                title = state.colorPickerTitle,
                                examplesUi = state.buildColorPickerExamplesUi(),
                                onDone = { newColorRgba ->
                                    vm.setColorRgba(newColorRgba = newColorRgba)
                                },
                            )
                        }
                    },
                    onLongClick = null,
                )

                FormPaddingSectionSection()

                FormSwitch(
                    title = state.keepScreenOnTitle,
                    isEnabled = state.keepScreenOn,
                    isFirst = true,
                    isLast = false,
                    onChange = { newKeepScreenOn ->
                        vm.setKeepScreenOn(newKeepScreenOn = newKeepScreenOn)
                    },
                )

                FormButton(
                    title = state.pomodoroTitle,
                    isFirst = false,
                    isLast = true,
                    note = state.pomodoroNote,
                    withArrow = true,
                    onClick = {
                        navigationFs.push {
                            ActivityFormPomodoroFs(
                                vm = vm,
                                state = state,
                            )
                        }
                    },
                )

                FormPaddingSectionSection()

                FormButton(
                    title = state.goalsTitle,
                    isFirst = true,
                    isLast = false,
                    note = state.goalsNote,
                    withArrow = true,
                    onClick = {
                        navigationFs.push {
                            ActivityFormGoalsFs(
                                initGoalFormsData = state.goalFormsData,
                                onDone = { newGoalFormsData ->
                                    vm.setGoalFormsData(newGoalFormsData = newGoalFormsData)
                                },
                            )
                        }
                    },
                )

                FormButton(
                    title = state.timerHintsTitle,
                    isFirst = false,
                    isLast = true,
                    note = state.timerHintsNote,
                    withArrow = true,
                    onClick = {
                        navigationFs.push {
                            ActivityFormTimerHintsFs(
                                initTimerHints = state.timerHints,
                                onDone = { newTimerHints ->
                                    vm.setTimerHints(newTimerHints)
                                },
                            )
                        }
                    },
                )

                FormPaddingSectionSection()

                FormButton(
                    title = "Checklists",
                    isFirst = true,
                    isLast = false,
                    note = state.checklistsNote,
                    withArrow = true,
                    onClick = {
                        navigationFs.push {
                            ChecklistsPickerFs(
                                initChecklistsDb = state.checklistsDb,
                                onDone = { newChecklistsDb ->
                                    vm.setChecklistsDb(newChecklistsDb)
                                }
                            )
                        }
                    },
                )

                FormButton(
                    title = "Shortcuts",
                    isFirst = false,
                    isLast = true,
                    note = state.shortcutsNote,
                    withArrow = true,
                    onClick = {
                        navigationFs.push {
                            ShortcutsPickerFs(
                                initShortcutsDb = state.shortcutsDb,
                                onDone = { newShortcutsDb ->
                                    vm.setShortcutsDb(newShortcutsDb)
                                }
                            )
                        }
                    },
                )

                //
                // Target Settings

                FormPaddingSectionHeader()

                FormHeader("TARGET")

                FormPaddingHeaderSection()

                FormSwitch(
                    title = state.isTargetTitle,
                    isEnabled = state.isTarget,
                    isFirst = true,
                    isLast = true,
                    onChange = { newIsTarget ->
                        vm.setIsTarget(newIsTarget)
                    },
                )

                if (state.isTarget) {

                    FormPaddingSectionSection()

                    FormButton(
                        title = state.parentActivityTitle,
                        isFirst = true,
                        isLast = false,
                        note = state.parentActivityNote,
                        withArrow = true,
                        onClick = {
                            val targetActivities = Cache.activitiesDbSorted
                                .filter { it.isTarget }
                                .map { activity ->
                                    NavigationPickerItem(
                                        title = activity.name,
                                        isSelected = activity.id == state.parentActivityId,
                                        item = activity.id,
                                    )
                                }
                            val noneItem = NavigationPickerItem(
                                title = "None (Top-level Target)",
                                isSelected = state.parentActivityId == null,
                                item = null as Int?,
                            )
                            navigationFs.picker(
                                title = "Select Parent Target",
                                items = listOf(noneItem) + targetActivities,
                                onDone = { selected ->
                                    vm.setParentActivityId(selected.item)
                                },
                            )
                        },
                    )

                    FormButton(
                        title = state.importanceTitle,
                        isFirst = false,
                        isLast = true,
                        note = state.importanceNote,
                        withArrow = true,
                        onClick = {
                            val items = listOf(0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100).map { pct ->
                                NavigationPickerItem(
                                    title = "${pct}%",
                                    isSelected = state.importance == pct,
                                    item = pct,
                                )
                            }
                            navigationFs.picker(
                                title = "Select Importance",
                                items = items,
                                onDone = { selected ->
                                    vm.setImportance(selected.item)
                                },
                            )
                        },
                    )
                }

                //
                // Categories

                FormPaddingSectionHeader()

                FormHeader("CATEGORIES")

                FormPaddingHeaderSection()

                FormButton(
                    title = "Categories",
                    isFirst = true,
                    isLast = true,
                    note = state.categoryIds.takeIf { it.isNotEmpty() }?.let { ids ->
                        ids.joinToString(", ") { id ->
                            Cache.categoriesDb.firstOrNull { it.id == id }?.name ?: "Unknown"
                        }
                    } ?: "None",
                    withArrow = true,
                    onClick = {
                        val items = Cache.categoriesDb.map { category ->
                            NavigationPickerItem(
                                title = category.name,
                                isSelected = state.categoryIds.contains(category.id),
                                item = category.id,
                            )
                        }
                        navigationFs.picker(
                            title = "Select Categories",
                            items = items,
                            onDone = { selected ->
                                // Toggle selection
                                val newIds = state.categoryIds.toMutableList()
                                val id = selected.item
                                if (newIds.contains(id)) {
                                    newIds.remove(id)
                                } else {
                                    newIds.add(id)
                                }
                                vm.setCategoryIds(newIds)
                            },
                        )
                    },
                )

                val activityDb: ActivityDb? = state.initActivityDb
                if (activityDb != null) {
                    FormPaddingSectionSection()
                    FormButton(
                        title = "Delete Activity",
                        titleColor = c.red,
                        isFirst = true,
                        isLast = true,
                        onClick = {
                            vm.delete(
                                activityDb = activityDb,
                                dialogsManager = navigationFs,
                                onSuccess = {
                                    navigationLayer.close()
                                },
                            )
                        },
                    )
                }

                FormPaddingBottom(
                    withNavigation = true,
                )
            }
        }
    }
}
