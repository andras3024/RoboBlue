package andras.ludvig.roboblue.database

object DbConstants {

    const val DATABASE_NAME = "data.db"
    const val DATABASE_VERSION = 1
    const val DATABASE_CREATE_ALL = Functionbuttons.DATABASE_CREATE
    const val DATABASE_DROP_ALL = Functionbuttons.DATABASE_DROP

    object Functionbuttons {
        const val DATABASE_TABLE = "Functionbuttons"
        const val KEY_ROWID = "_id"
        const val KEY_TITLE = "title"
        const val KEY_VALUE = "value"


        const val DATABASE_CREATE =
            """create table if not exists $DATABASE_TABLE (
			$KEY_ROWID integer primary key autoincrement,
			$KEY_TITLE text not null,
			$KEY_VALUE text not null
			); """

        const val DATABASE_DROP = "drop table if exists $DATABASE_TABLE;"
    }

}