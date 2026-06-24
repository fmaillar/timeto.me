package me.timeto.shared.vm.goals

import kotlinx.coroutines.flow.MutableStateFlow
import me.timeto.shared.Cache
import me.timeto.shared.db.GoalDb
import me.timeto.shared.textFeatures
import me.timeto.shared.vm.Vm

class GoalPickerVm(
    val excludeGoalId: Int? = null,
) : Vm<GoalPickerVm.State>() {

    data class State(
        val goalsUi: List<GoalUi>,
    )

    override val state = MutableStateFlow(
        State(
            goalsUi = Cache.goalsDb
                .filter { it.id != excludeGoalId }
                .map { GoalUi(it) }
        )
    )

    ///

    data class GoalUi(
        val goalDb: GoalDb,
    ) {
        val title: String
            get() {
                val activityDb = goalDb.getActivityDbCached()
                val goalNote = goalDb.note.textFeatures().textNoFeatures
                return "${activityDb.name} - $goalNote"
            }
    }
}
