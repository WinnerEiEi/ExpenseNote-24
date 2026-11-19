package com.example.expensenote_24.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.expensenote_24.model.ExpenseItem

class ExpenseDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "expense_note.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_EXPENSES = "expenses"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_NOTE = "note"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_EXPENSES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_TYPE TEXT NOT NULL,
                $COLUMN_CATEGORY TEXT NOT NULL,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_NOTE TEXT
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        onCreate(db)
    }

    fun insertExpense(item: ExpenseItem): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, item.title)
            put(COLUMN_AMOUNT, item.amount)
            put(COLUMN_TYPE, item.type)
            put(COLUMN_CATEGORY, item.category)
            put(COLUMN_DATE, item.date)
            put(COLUMN_NOTE, item.note)
        }
        val id = db.insert(TABLE_EXPENSES, null, values)
        db.close()
        return id
    }

    fun deleteExpense(id: Long): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_EXPENSES, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun getAllExpenses(filterType: String = "ALL"): List<ExpenseItem> {
        val itemList = mutableListOf<ExpenseItem>()
        val db = readableDatabase

        val selection = if (filterType == "ALL") null else "$COLUMN_TYPE = ?"
        val selectionArgs = if (filterType == "ALL") null else arrayOf(filterType)

        val cursor = db.query(
            TABLE_EXPENSES,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "$COLUMN_ID DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val title = getString(getColumnIndexOrThrow(COLUMN_TITLE))
                val amount = getDouble(getColumnIndexOrThrow(COLUMN_AMOUNT))
                val type = getString(getColumnIndexOrThrow(COLUMN_TYPE))
                val category = getString(getColumnIndexOrThrow(COLUMN_CATEGORY))
                val date = getString(getColumnIndexOrThrow(COLUMN_DATE))
                val note = getString(getColumnIndexOrThrow(COLUMN_NOTE)) ?: ""

                itemList.add(ExpenseItem(id, title, amount, type, category, date, note))
            }
        }
        cursor.close()
        db.close()
        return itemList
    }

    fun getTotalIncome(): Double {
        return getTotalByType(ExpenseItem.TYPE_INCOME)
    }

    fun getTotalExpense(): Double {
        return getTotalByType(ExpenseItem.TYPE_EXPENSE)
    }

    private fun getTotalByType(type: String): Double {
        val db = readableDatabase
        var total = 0.0
        val query = "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_EXPENSES WHERE $COLUMN_TYPE = ?"
        val cursor = db.rawQuery(query, arrayOf(type))
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return total
    }
}
