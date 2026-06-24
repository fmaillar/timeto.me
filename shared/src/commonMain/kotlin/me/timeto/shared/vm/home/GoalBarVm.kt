package me.timeto.shared.vm.home

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.timeto.shared.*
import me.timeto.shared.db.GoalDb
import me.timeto.shared.db.IntervalDb
import me.timeto.shared.vm.Vm

/**
 * Grading system based on total weighted tracking minutes of goals.
 * Level 0: [0, 30]
 * Level 1: (30, 60]
 * Level 2: (60, 120]
 * Level 3: (120, 180]
 * Level 4: (180, 240]
 * Level 5: (240, 360]
 * Level 6: (360, 480]
 * Level 7: (480, 540]
 * Level 8: (540, 600]
 * Level 9: (600, 720]
 */
enum class GoalGrade(
    val maxMinutes: Int,
    val emoji: String,
    val color: ColorRgba,
) {
    LEVEL_0(30, "\uD83C\uDF31", ColorRgba(100, 149, 237)),   // Seedling, cornflower blue
    LEVEL_1(60, "\uD83C\uDF3F", ColorRgba(72, 169, 166)),    // Herb, muted teal
    LEVEL_2(120, "\uD83C\uDF43", ColorRgba(76, 175, 80)),    // Leaves, muted green
    LEVEL_3(180, "\uD83C\uDF3A", ColorRgba(129, 178, 154)),  // Flower, sage green
    LEVEL_4(240, "\uD83C\uDF38", ColorRgba(155, 170, 180)),  // Cherry blossom, dusty blue
    LEVEL_5(360, "\uD83C\uDF3C", ColorRgba(170, 150, 190)),  // Blossom, lavender
    LEVEL_6(480, "\uD83C\uDF37", ColorRgba(185, 130, 190)),  // Tulip, mauve
    LEVEL_7(540, "\uD83C\uDF39", ColorRgba(195, 115, 185)),  // Rose, orchid
    LEVEL_8(600, "\uD83C\uDF1F", ColorRgba(200, 100, 175)),  // Star, deep pink
    LEVEL_9(720, "\uD83D\uDD25", ColorRgba(210, 85, 160)),  // Fire, magenta-pink
    ;
    companion object {
        fun forMinutes(minutes: Float): GoalGrade {
            val m = minutes.toInt()
            return entries.firstOrNull { m <= it.maxMinutes } ?: LEVEL_9
        }
    }
}

class GoalBarVm : Vm<GoalBarVm.State>() {

    data class State(
        val weightedTotalMinutes: Float,
        val actualTotalMinutes: Float,
        val grade: GoalGrade,
        val update: Int = 0,
    ) {
        val weightedTotalText: String = "%.1f".format(weightedTotalMinutes)
        val actualTotalText: String = "%.1f".format(actualTotalMinutes)
        val gradeEmoji: String = grade.emoji
    }

    data class GoalInfo(
        val goalDb: GoalDb,
        val seconds: Int,
        val importance: Float, // 0.0 to 1.0
    )

    override val state = MutableStateFlow(
        State(
            weightedTotalMinutes = 0f,
            actualTotalMinutes = 0f,
            grade = GoalGrade.LEVEL_0,
        )
    )

    init {
        val scopeVm = scopeVm()

        IntervalDb.anyChangeFlow()
            .onEachExIn(scopeVm) {
                scopeVm.launch { recalculate() }
            }

        GoalDb.anyChangeFlow()
            .onEachExIn(scopeVm) {
                scopeVm.launch { recalculate() }
            }

        scopeVm.launch {
            recalculate()
            while (true) {
                delay(5_000)
                recalculate()
            }
        }
    }

    private suspend fun recalculate() {
        val todayBars = DayBarsUi.buildToday()

        // Top-level goals (no parent)
        val topGoals: List<GoalDb> = Cache.goalsDb
            .filter { !it.hasParentGoal }

        var actualTotalSeconds = 0
        var weightedTotalSeconds = 0f

        for (parentGoal in topGoals) {
            val subGoals: List<GoalDb> = Cache.goalsDb
                .filter { it.parent_goal_id == parentGoal.id }

            val parentActivityId = parentGoal.activity_id
            val parentSeconds = todayBars.barsUi
                .filter { it.activityDb?.id == parentActivityId }
                .sumOf { it.seconds }

            var subTotalSeconds = 0
            var subWeighted = 0f

            for (subGoal in subGoals) {
                val subActivityId = subGoal.activity_id
                val subSeconds = todayBars.barsUi
                    .filter { it.activityDb?.id == subActivityId }
                    .sumOf { it.seconds }

                // Sub-goal importance: use its own if set, otherwise inherit from parent
                val subImportance = (subGoal.importance ?: parentGoal.importance)?.toFloat()?.div(100f) ?: 1f

                subTotalSeconds += subSeconds
                subWeighted += subSeconds * subImportance
            }

            // Parent importance
            val parentImportance = parentGoal.importance?.toFloat()?.div(100f) ?: 1f

            // Remaining parent time (excluding sub-goal time that's already counted)
            val remainingParentSeconds = maxOf(0, parentSeconds - subTotalSeconds)
            val parentWeighted = remainingParentSeconds * parentImportance

            actualTotalSeconds += parentSeconds
            weightedTotalSeconds += subWeighted + parentWeighted
        }

        val weightedTotalMinutes = weightedTotalSeconds / 60f
        val actualTotalMinutes = actualTotalSeconds / 60f

        state.update {
            it.copy(
                weightedTotalMinutes = weightedTotalMinutes,
                actualTotalMinutes = actualTotalMinutes,
                grade = GoalGrade.forMinutes(weightedTotalMinutes),
                update = it.update + 1,
            )
        }
    }
}
