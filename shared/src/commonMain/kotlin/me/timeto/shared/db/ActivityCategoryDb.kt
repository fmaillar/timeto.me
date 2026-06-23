package me.timeto.shared.db

import app.cash.sqldelight.coroutines.asFlow
import dbsq.ActivityCategorySQ
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.*
import me.timeto.shared.*
import me.timeto.shared.backups.Backupable__Holder
import me.timeto.shared.backups.Backupable__Item
import me.timeto.shared.getInt
import me.timeto.shared.toJsonArray

data class ActivityCategoryDb(
    val activity_id: Int,
    val category_id: Int,
) : Backupable__Item {

    companion object : Backupable__Holder {

        fun anyChangeFlow(): Flow<*> =
            db.activityCategoryQueries.anyChange().asFlow()

        fun selectAllFlow(): Flow<List<ActivityCategoryDb>> =
            db.activityCategoryQueries.selectAll().asListFlow { toDb() }

        suspend fun selectAll(): List<ActivityCategoryDb> = dbIo {
            db.activityCategoryQueries.selectAll().asList { toDb() }
        }

        suspend fun selectByActivityId(activityId: Int): List<ActivityCategoryDb> = dbIo {
            db.activityCategoryQueries.selectByActivityId(activityId).asList { toDb() }
        }

        suspend fun selectByCategoryId(categoryId: Int): List<ActivityCategoryDb> = dbIo {
            db.activityCategoryQueries.selectByCategoryId(categoryId).asList { toDb() }
        }

        suspend fun setCategoriesForActivity(
            activityId: Int,
            categoryIds: List<Int>,
        ): Unit = dbIo {
            db.transaction {
                db.activityCategoryQueries.deleteByActivityId(activity_id = activityId)
                categoryIds.forEach { categoryId ->
                    db.activityCategoryQueries.insert(
                        activity_id = activityId,
                        category_id = categoryId,
                    )
                }
            }
        }

        override fun backupable__getAll(): List<Backupable__Item> =
            db.activityCategoryQueries.selectAll().asList { toDb() }

        override fun backupable__restore(json: JsonElement) {
            val j = json.jsonArray
            db.activityCategoryQueries.insert(
                activity_id = j.getInt(0),
                category_id = j.getInt(1),
            )
        }
    }

    override fun backupable__getId(): String = "$activity_id-$category_id"

    override fun backupable__backup(): JsonElement = listOf(
        activity_id, category_id,
    ).toJsonArray()

    override fun backupable__update(json: JsonElement) {
        // Not applicable for join table
    }

    override fun backupable__delete() {
        db.activityCategoryQueries.deleteByActivityIdAndCategoryId(
            activity_id = activity_id,
            category_id = category_id,
        )
    }
}

private fun ActivityCategorySQ.toDb() = ActivityCategoryDb(
    activity_id = activity_id, category_id = category_id,
)