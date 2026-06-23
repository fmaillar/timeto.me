package me.timeto.shared.vm.summary

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.timeto.shared.ColorRgba
import me.timeto.shared.PieChart
import me.timeto.shared.launchEx
import me.timeto.shared.vm.Vm

class SummaryChartVm(
    activitiesUi: List<SummaryVm.ActivityUi>,
    categoriesUi: List<SummaryVm.CategoryUi>,
    viewMode: SummaryVm.State.ViewMode,
) : Vm<SummaryChartVm.State>() {

    data class State(
        val pieItems: List<PieChart.ItemData>,
    )

    override val state = MutableStateFlow(
        State(
            pieItems = emptyList(),
        )
    )

    init {
        scopeVm().launchEx {
            val items = when (viewMode) {
                SummaryVm.State.ViewMode.ACTIVITIES -> {
                    activitiesUi.map { activityUi ->
                        val activity = activityUi.activity
                        PieChart.ItemData(
                            id = "${activity.id}",
                            value = activityUi.seconds.toDouble(),
                            color = activity.colorRgba,
                            title = activityUi.title,
                            shortTitle = activity.emoji,
                            subtitleTop = "${(activityUi.ratio * 100).toInt()}%",
                            subtitleBottom = activityUi.totalTimeString,
                            customData = activityUi.perDayString,
                        )
                    }
                }
                SummaryVm.State.ViewMode.CATEGORIES -> {
                    // Assign colors to categories based on index
                    val categoryColors = listOf(
                        ColorRgba(100, 149, 237),
                        ColorRgba(72, 169, 166),
                        ColorRgba(76, 175, 80),
                        ColorRgba(129, 178, 154),
                        ColorRgba(170, 150, 190),
                        ColorRgba(185, 130, 190),
                        ColorRgba(195, 115, 185),
                        ColorRgba(200, 100, 175),
                        ColorRgba(210, 160, 140),
                        ColorRgba(180, 140, 150),
                    )
                    categoriesUi.mapIndexed { idx, categoryUi ->
                        PieChart.ItemData(
                            id = "${categoryUi.category.id}",
                            value = categoryUi.seconds.toDouble(),
                            color = categoryColors[idx % categoryColors.size],
                            title = categoryUi.title,
                            shortTitle = categoryUi.title.take(1),
                            subtitleTop = "${(categoryUi.ratio * 100).toInt()}%",
                            subtitleBottom = categoryUi.totalTimeString,
                            customData = "${categoryUi.activityCount} activities",
                        )
                    }
                }
            }
            state.update {
                it.copy(pieItems = items)
            }
        }
    }
}
