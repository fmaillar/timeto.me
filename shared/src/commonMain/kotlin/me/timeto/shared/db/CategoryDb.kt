package me.timeto.shared.db

import app.cash.sqldelight.coroutines.asFlow
import dbsq.CategorySQ
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.*
import me.timeto.shared.*
import me.timeto.shared.backups.Backupable__Holder
import me.timeto.shared.backups.Backupable__Item
import me.timeto.shared.getInt
import me.timeto.shared.getString
import me.timeto.shared.time
import me.timeto.shared.toJsonArray

data class CategoryDb(
    val id: Int,
    val name: String,
    val sort: Int,
) : Backupable__Item {

    companion object : Backupable__Holder {

        fun anyChangeFlow(): Flow<*> =
            db.categoryQueries.anyChange().asFlow()

        fun selectSortedSync(): List<CategoryDb> =
            db.categoryQueries.selectSorted().asList { toDb() }

        suspend fun selectSorted(): List<CategoryDb> = dbIo {
            selectSortedSync()
        }

        fun selectSortedFlow(): Flow<List<CategoryDb>> =
            db.categoryQueries.selectSorted().asListFlow { toDb() }

        @Throws(UiException::class)
        suspend fun addWithValidation(
            name: String,
        ): CategoryDb = dbIo {
            val validatedName: String = name.trim().takeIf { it.isNotEmpty() }
                ?: throw UiException("Empty category name")
            val categoriesDb: List<CategoryDb> = selectSortedSync()
            val lastId: Int = categoriesDb.maxOfOrNull { it.id } ?: 0
            val nextId: Int = maxOf(time(), lastId + 1)
            val maxSort: Int = categoriesDb.maxOfOrNull { it.sort } ?: -1
            val categorySQ = CategorySQ(
                id = nextId,
                name = validatedName,
                sort = maxSort + 1,
            )
            db.categoryQueries.insert(categorySQ)
            categorySQ.toDb()
        }

        suspend fun updateSortMany(
            categoriesDb: List<CategoryDb>,
        ): Unit = dbIo {
            db.transaction {
                categoriesDb.forEachIndexed { idx, categoryDb ->
                    db.categoryQueries.updateSortById(
                        id = categoryDb.id,
                        sort = idx,
                    )
                }
            }
        }

        suspend fun updateById(
            id: Int,
            name: String,
        ): Unit = dbIo {
            val validatedName: String = name.trim().takeIf { it.isNotEmpty() }
                ?: throw UiException("Empty category name")
            val categoryDb = selectSortedSync().firstOrNull { it.id == id }
                ?: throw UiException("Category not found")
            db.categoryQueries.updateById(
                id = id,
                name = validatedName,
                sort = categoryDb.sort,
            )
        }

        suspend fun deleteById(id: Int): Unit = dbIo {
            db.transaction {
                db.activityCategoryQueries.deleteByCategoryId(category_id = id)
                db.categoryQueries.deleteById(id)
            }
        }

        override fun backupable__getAll(): List<Backupable__Item> =
            db.categoryQueries.selectSorted().asList { toDb() }

        override fun backupable__restore(json: JsonElement) {
            val j = json.jsonArray
            db.categoryQueries.insert(
                CategorySQ(
                    id = j.getInt(0),
                    name = j.getString(1),
                    sort = j.getInt(2),
                )
            )
        }
    }

    override fun backupable__getId(): String = id.toString()

    override fun backupable__backup(): JsonElement = listOf(
        id, name, sort,
    ).toJsonArray()

    override fun backupable__update(json: JsonElement) {
        val j = json.jsonArray
        db.categoryQueries.updateById(
            id = j.getInt(0),
            name = j.getString(1),
            sort = j.getInt(2),
        )
    }

    override fun backupable__delete() {
        db.categoryQueries.deleteById(id)
    }
}

private fun CategorySQ.toDb() = CategoryDb(
    id = id, name = name, sort = sort,
)