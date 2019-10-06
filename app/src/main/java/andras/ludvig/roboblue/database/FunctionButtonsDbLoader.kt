package andras.ludvig.roboblue.database

import andras.ludvig.roboblue.DatabaseHelper.DatabaseHelper
import andras.ludvig.roboblue.model.Functionbuttons
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

class FunctionButtonsDbLoader (private val context: Context) {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: SQLiteDatabase

    @Throws(SQLException::class)
    fun open() {
        dbHelper = DatabaseHelper(context, DbConstants.DATABASE_NAME)
        db = dbHelper.writableDatabase

        dbHelper.onCreate(db)
    }

    fun close() {
        dbHelper.close()
    }

    // INSERT
    fun createFunctionButton(functionbuttons: Functionbuttons): Long {
        val values = ContentValues()
        values.put(DbConstants.Functionbuttons.KEY_TITLE, functionbuttons.title)
        values.put(DbConstants.Functionbuttons.KEY_VALUE, functionbuttons.value)

        return db.insert(DbConstants.Functionbuttons.DATABASE_TABLE, null, values)
    }

    // DELETE
    fun deleteFunctionButton(rowId: Long): Boolean {
        val deletedFunctionButtons = db.delete(
            DbConstants.Functionbuttons.DATABASE_TABLE,
            "${DbConstants.Functionbuttons.KEY_ROWID} = $rowId",
            null
        )
        return deletedFunctionButtons > 0
    }

    // UPDATE
    fun updateFunctionButton(newFunctionbuttons: Functionbuttons): Boolean {
        val values = ContentValues()
        values.put(DbConstants.Functionbuttons.KEY_TITLE, newFunctionbuttons.title)
        values.put(DbConstants.Functionbuttons.KEY_VALUE, newFunctionbuttons.value)

        val todosUpdated = db.update(
            DbConstants.Functionbuttons.DATABASE_TABLE,
            values,
            "${DbConstants.Functionbuttons.KEY_ROWID} = ${newFunctionbuttons.id}",
            null
        )

        return todosUpdated > 0
    }

    // Get all Functionbuttons
    fun fetchAll(): Cursor {
        return db.query(
            DbConstants.Functionbuttons.DATABASE_TABLE,
            arrayOf(
                DbConstants.Functionbuttons.KEY_ROWID,
                DbConstants.Functionbuttons.KEY_TITLE,
                DbConstants.Functionbuttons.KEY_VALUE
            ),
            null,
            null,
            null,
            null,
            DbConstants.Functionbuttons.KEY_TITLE
        )
    }

    // Querying for one of the FunctionButtons with the given id
    fun fetchFunctionButton(id: Long): Functionbuttons? {
        // Cursor pointing to a result set with 0 or 1
        val cursor = db.query(
            DbConstants.Functionbuttons.DATABASE_TABLE,
            arrayOf(
                DbConstants.Functionbuttons.KEY_ROWID,
                DbConstants.Functionbuttons.KEY_TITLE,
                DbConstants.Functionbuttons.KEY_VALUE
            ),
            "${DbConstants.Functionbuttons.KEY_ROWID} = $id",
            null,
            null,
            null,
            DbConstants.Functionbuttons.KEY_TITLE
        )

        // Return with the found entry or null if there wasn't any with the given id
        return if (cursor.moveToFirst()) {
            getFunctionButtonByCursor(cursor)
        } else {
            null
        }
    }

    companion object {
        fun getFunctionButtonByCursor(c: Cursor): Functionbuttons {

            return Functionbuttons(
                id = c.getLong(c.getColumnIndex(DbConstants.Functionbuttons.KEY_ROWID)),
                title = c.getString(c.getColumnIndex(DbConstants.Functionbuttons.KEY_TITLE)),
                value = c.getString(c.getColumnIndex(DbConstants.Functionbuttons.KEY_VALUE))
            )
        }
    }
}