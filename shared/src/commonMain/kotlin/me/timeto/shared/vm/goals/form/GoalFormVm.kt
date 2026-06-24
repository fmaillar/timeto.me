package me.timeto.shared.vm.goals.form

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.timeto.shared.Cache
import me.timeto.shared.TextFeatures
import me.timeto.shared.db.ChecklistDb
import me.timeto.shared.db.GoalDb
import me.timeto.shared.db.ShortcutDb
import me.timeto.shared.textFeatures
import me.timeto.shared.toTimerHintNote
import me.timeto.shared.DialogsManager
import me.timeto.shared.UiException
import me.timeto.shared.db.ActivityDb
import me.timeto.shared.launchExIo
import me.timeto.shared.vm.Vm

class GoalFormVm(
    strategy: GoalFormStrategy,
) : Vm<GoalFormVm.State>() {

    data class State(
        val strategy: GoalFormStrategy,
        val note: String,
        val isEntireActivity: Boolean,
        val period: GoalDb.Period?,
        val seconds: Int,
        val timer: Int,
        val finishedText: String,
        val checklistsDb: List<ChecklistDb>,
        val shortcutsDb: List<ShortcutDb>,
        val parentGoalId: Int?,
        val importance: Int?,
    ) {

        val title: String = when (strategy) {
            is GoalFormStrategy.NewFormData -> "New Goal"
            is GoalFormStrategy.EditFormData -> "Edit Goal"
            is GoalFormStrategy.NewGoal -> "New Goal"
            is GoalFormStrategy.EditGoal -> "Edit Goal"
        }

        val doneText: String = when (strategy) {
            is GoalFormStrategy.NewFormData -> "Done"
            is GoalFormStrategy.EditFormData -> "Done"
            is GoalFormStrategy.NewGoal -> "Create"
            is GoalFormStrategy.EditGoal -> "Save"
        }

        val notePlaceholder = "Note"

        val isEntireActivityTitle = "Track Entire Activity"

        val periodTitle = "Period"
        val periodNote: String = period?.note() ?: "None"

        val secondsTitle = "Duration"
        val secondsNote: String = seconds.toTimerHintNote(isShort = false)

        val timerHeader = "TIMER ON BAR PRESSED"
        val timerTitleRest = "Rest of Bar"
        val timerTitleTimer = "Timer"
        val timerNote: String = timer.toTimerHintNote(isShort = false)

        val finishedTextTitle = "Finished Emoji"

        val checklistsNote: String =
            if (checklistsDb.isEmpty()) "None"
            else checklistsDb.joinToString(", ") { it.name }

        val shortcutsNote: String =
            if (shortcutsDb.isEmpty()) "None"
            else shortcutsDb.joinToString(", ") { it.name }

        val parentGoalTitle = "Parent Goal"
        val parentGoalNote: String =
            if (parentGoalId == null) "None"
            else Cache.goalsDb.firstOrNull { it.id == parentGoalId }?.let { goalDb ->
                val activityDb = goalDb.getActivityDbCached()
                "${activityDb.name} - ${goalDb.note.textFeatures().textNoFeatures}"
            } ?: "Unknown"

        val importanceTitle = "Importance"
        val importanceNote: String =
            if (importance == null) "Not Set"
            else "${importance}%"

        ///

        fun buildFormDataOrNull(
            dialogsManager: DialogsManager,
            goalDb: GoalDb?,
        ): GoalFormData? = try {
            validateData(goalDb = goalDb)
        } catch (e: UiException) {
            dialogsManager.alert(e.uiMessage)
            null
        }

        @Throws(UiException::class)
        private fun validateData(
            goalDb: GoalDb?,
        ): GoalFormData {
            val noteValidated: String = note.trim()
            val tf: TextFeatures = noteValidated.textFeatures().copy(
                checklistsDb = checklistsDb,
                shortcutsDb = shortcutsDb,
            )
            if (tf.textNoFeatures.isBlank())
                throw UiException("Empty note")
            if (period == null)
                throw UiException("Period not selected")

            // Validate: parent goal cannot be itself
            if (goalDb != null && parentGoalId == goalDb.id)
                throw UiException("A goal cannot be its own parent")

            // Validate: importance required for top-level goals (no parent)
            if (parentGoalId == null && importance == null)
                throw UiException("Importance must be set for top-level goals")

            return GoalFormData(
                goalDb = goalDb,
                note = tf.textWithFeatures(),
                seconds = seconds,
                period = period,
                finishText = finishedText.trim(),
                isEntireActivity = isEntireActivity,
                timer = timer,
                parentGoalId = parentGoalId,
                importance = importance,
            )
        }
    }

    override val state: MutableStateFlow<State>

    init {
        val tf: TextFeatures
        val isEntireActivity: Boolean
        val period: GoalDb.Period?
        val seconds: Int
        val finishedText: String
        val timer: Int
        val parentGoalId: Int?
        val importance: Int?
        // Defaults
        val defaultFf: TextFeatures = "".textFeatures()
        val defaultIsEntireActivity = false
        val defaultPeriod: GoalDb.Period? = null
        val defaultSeconds = 3_600
        val defaultFinishedText = "👍"
        val defaultTimer = 0
        val defaultParentGoalId: Int? = null
        val defaultImportance: Int? = null
        ///
        when (strategy) {
            is GoalFormStrategy.NewFormData -> {
                tf = defaultFf
                isEntireActivity = defaultIsEntireActivity
                period = defaultPeriod
                seconds = defaultSeconds
                finishedText = defaultFinishedText
                timer = defaultTimer
                parentGoalId = defaultParentGoalId
                importance = defaultImportance
            }
            is GoalFormStrategy.EditFormData -> {
                val formData: GoalFormData = strategy.initGoalFormData
                tf = formData.note.textFeatures()
                isEntireActivity = formData.isEntireActivity
                period = formData.period
                seconds = formData.seconds
                finishedText = formData.finishText
                timer = formData.timer
                parentGoalId = formData.parentGoalId
                importance = formData.importance
            }
            is GoalFormStrategy.NewGoal -> {
                tf = defaultFf
                isEntireActivity = defaultIsEntireActivity
                period = defaultPeriod
                seconds = defaultSeconds
                finishedText = defaultFinishedText
                timer = defaultTimer
                parentGoalId = defaultParentGoalId
                importance = defaultImportance
            }
            is GoalFormStrategy.EditGoal -> {
                val goalDb: GoalDb = strategy.goalDb
                tf = goalDb.note.textFeatures()
                isEntireActivity = goalDb.isEntireActivity
                period = goalDb.buildPeriod()
                seconds = goalDb.seconds
                finishedText = goalDb.finish_text
                timer = goalDb.timer
                parentGoalId = goalDb.parent_goal_id
                importance = goalDb.importance
            }
        }
        state = MutableStateFlow(
            State(
                strategy = strategy,
                note = tf.textNoFeatures,
                isEntireActivity = isEntireActivity,
                period = period,
                seconds = seconds,
                timer = timer,
                finishedText = finishedText,
                checklistsDb = tf.checklistsDb,
                shortcutsDb = tf.shortcutsDb,
                parentGoalId = parentGoalId,
                importance = importance,
            )
        )
    }

    ///

    fun setNote(newNote: String) {
        state.update { it.copy(note = newNote) }
    }

    fun setIsEntireActivity(newIsEntireActivity: Boolean) {
        state.update { it.copy(isEntireActivity = newIsEntireActivity) }
    }

    fun setPeriod(newPeriod: GoalDb.Period) {
        state.update { it.copy(period = newPeriod) }
    }

    fun setSeconds(newSeconds: Int) {
        state.update { it.copy(seconds = newSeconds) }
    }

    fun setTimer(newTimer: Int) {
        state.update { it.copy(timer = newTimer) }
    }

    fun setFinishedText(newFinishedText: String) {
        state.update { it.copy(finishedText = newFinishedText) }
    }

    fun setChecklistsDb(newChecklistsDb: List<ChecklistDb>) {
        state.update { it.copy(checklistsDb = newChecklistsDb) }
    }

    fun setShortcutsDb(newShortcutsDb: List<ShortcutDb>) {
        state.update { it.copy(shortcutsDb = newShortcutsDb) }
    }

    fun setParentGoalId(newParentGoalId: Int?) {
        state.update { it.copy(parentGoalId = newParentGoalId) }
    }

    fun setImportance(newImportance: Int?) {
        state.update { it.copy(importance = newImportance) }
    }

    fun addGoal(
        activityDb: ActivityDb,
        dialogsManager: DialogsManager,
        onCreate: (GoalDb) -> Unit,
    ) {
        val formData: GoalFormData = state.value.buildFormDataOrNull(
            dialogsManager = dialogsManager,
            goalDb = null,
        ) ?: return
        launchExIo {
            val newGoalDb: GoalDb = GoalDb.insertAndGet(
                activityDb = activityDb,
                goalFormData = formData,
                parentGoalId = formData.parentGoalId,
                importance = formData.importance,
            )
            onUi {
                onCreate(newGoalDb)
            }
        }
    }

    fun saveGoal(
        goalDb: GoalDb,
        dialogsManager: DialogsManager,
        onSuccess: () -> Unit,
    ) {
        val formData: GoalFormData = state.value.buildFormDataOrNull(
            dialogsManager = dialogsManager,
            goalDb = goalDb,
        ) ?: return
        launchExIo {
            goalDb.update(
                formData,
                parentGoalId = formData.parentGoalId,
                importance = formData.importance,
            )
            onUi {
                onSuccess()
            }
        }
    }

    fun deleteGoal(goalDb: GoalDb) {
        launchExIo {
            goalDb.delete()
        }
    }
}
