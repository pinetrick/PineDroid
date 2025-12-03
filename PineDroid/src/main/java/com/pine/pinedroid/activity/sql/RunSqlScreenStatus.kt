package com.pine.pinedroid.activity.sql




import com.pine.pinedroid.db.ColumnInfo
import com.pine.pinedroid.db.DbRecord
import com.pine.pinedroid.db.bean.DatabaseInfo
import com.pine.pinedroid.db.bean.TableInfo
import com.pine.pinedroid.db.bean.fakeColumnInfos
import com.pine.pinedroid.db.bean.fakeDbRecords
import com.pine.pinedroid.db.table
import com.pine.pinedroid.utils.shrinker_keep.Keep

@Keep
data class RunSqlScreenStatus(
    val dbName: String = "Default Database",
    val tableName: String = "Default Table",
    val sql: String = "SELECT * \n FROM table_name \n LIMIT 100",
    val table: List<DbRecord> = fakeDbRecords,
    val tableHeader: List<ColumnInfo> = fakeColumnInfos
)
