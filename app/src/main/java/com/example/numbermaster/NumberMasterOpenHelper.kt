package com.example.numbermaster

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class NumberMasterOpenHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    // current_game_statusにレコードがあるかどうか？
    var exists = false

    // DBがない時はNumberMasterOpenHelperのinitialInsertの値が使われる。(以下は使われない。)
    var dataGame = mutableMapOf(
        "settings" to mutableMapOf(
            "counter_stop_count" to 0,
            "enabled_cube" to 0,
            "add_icon_read" to 1,
        ),
        "current_game_status" to mutableMapOf(
            "use_cube_mode" to 0,
            "blindfold_mode" to 0,
            "size" to 1,
            "cube_side_number" to 0,
            "score" to 0,
            "time" to 0,
            "tap_count" to 0,
        ),
    )
    /**
     * 1から9までの数字の羅列
     * non number部分はwhite spaceを入れる
     */
    var dataNumbers = MutableList(6) {MutableList(9) {MutableList(9) {0} } }
    var dataNonNumberPanelPosition = mutableMapOf(
        "cubeSideNumber" to 0,
        "x" to 2,
        "y" to 2,
    )

    val testModeNumbers = listOf("000", "111")

    // DBがない時はNumberMasterOpenHelperのinitialInsertの値が使われる。(以下は使われない。)
    companion object {
        private const val DATABASE_NAME = "NumberMasterDB.db"
        private const val DATABASE_VERSION = 3

        private const val CREATE_TABLE_QUERY_SETTINGS = """
CREATE TABLE IF NOT EXISTS settings
(
    id INTEGER PRIMARY KEY,
    counter_stop_count INTEGER NOT NULL DEFAULT 0,
    enabled_cube INTEGER NOT NULL DEFAULT 0, -- 0 or 1
    add_icon_read INTEGER NOT NULL DEFAULT 1 -- 0 or 1
)
"""

        private const val CREATE_TABLE_QUERY_CURRENT_GAME_STATUS = """
CREATE TABLE IF NOT EXISTS current_game_status
(
    id INTEGER PRIMARY KEY,
    use_cube_mode INTEGER NOT NULL DEFAULT 0, -- 0 or 1
    blindfold_mode INTEGER NOT NULL DEFAULT 0, -- 0 or 1
    size INTEGER NOT NULL DEFAULT 1, -- 1 or 2 or 3
    cube_side_number INTEGER NOT NULL DEFAULT 0, -- 0 to 5
    score INTEGER NOT NULL DEFAULT 0,
    time INTEGER NOT NULL DEFAULT 0,
    tap_count INTEGER NOT NULL DEFAULT 0,
    start_numbers TEXT NOT NULL,
    finish_numbers TEXT NOT NULL
)
"""

        private const val CREATE_TABLE_QUERY_SUCCESS_HISTORY = """
CREATE TABLE IF NOT EXISTS success_history
(
    id INTEGER PRIMARY KEY,
    created_at TEXT NOT NULL DEFAULT (DATETIME(CURRENT_TIMESTAMP, 'localtime')),
    use_cube_mode INTEGER NOT NULL DEFAULT 0,
    blindfold_mode INTEGER NOT NULL DEFAULT 0, -- 0 or 1
    size INTEGER NOT NULL DEFAULT 1,
    score INTEGER NOT NULL DEFAULT 0,
    time INTEGER NOT NULL DEFAULT 0,
    tap_count INTEGER NOT NULL DEFAULT 0,
    start_numbers TEXT NOT NULL,
    finish_numbers TEXT NOT NULL
)
"""
    }

    // tableが無い時にreadableDatabase又はwritableDatabaseを実行すると呼ばれる
    override fun onCreate(database: SQLiteDatabase?) {
        database?.execSQL(CREATE_TABLE_QUERY_SETTINGS)
        database?.execSQL(CREATE_TABLE_QUERY_CURRENT_GAME_STATUS)
        database?.execSQL(CREATE_TABLE_QUERY_SUCCESS_HISTORY)

        val result = this.initialInsert(database)
        if (!result) {
            // DBエラー
            Log.e("db error", "insertOrThrow")
        }
    }

    override fun onUpgrade(database: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            if (oldVersion == 1 && newVersion == 2) {
                database!!.execSQL(
                """
                    ALTER TABLE current_game_status
                    add blindfold_mode INTEGER NOT NULL DEFAULT 0
                """
                )
                database.execSQL(
                    """
                    ALTER TABLE success_history
                    add blindfold_mode INTEGER NOT NULL DEFAULT 0
                """
                )
            }
            if (oldVersion == 2 && newVersion == 3) {
                database!!.execSQL(
                    """
                    ALTER TABLE current_game_status
                    add tap_count INTEGER NOT NULL DEFAULT 0
                """
                )
                database.execSQL(
                    """
                    ALTER TABLE success_history
                    add tap_count INTEGER NOT NULL DEFAULT 0
                """
                )
            }
        }
    }

    /**
     * load, update, insert, deleteのbase群
     */
    private fun loadBase(tableName: String, columns: List<String>, orderColumnName: String = "id", orderOption: String = "DESC", count: Int = 1, whereString: String = "", whereValues: Array<String>? = null): MutableList<MutableMap<String, String>> {
        val database = this.readableDatabase
        val cursor: Cursor?
        var sql = "SELECT " + columns.joinToString(", ") + " FROM " + tableName
        if (whereString != "") {
            sql = "$sql WHERE $whereString"
        }

        sql = "$sql ORDER BY $orderColumnName $orderOption LIMIT $count"
        try {
            cursor = database.rawQuery(sql, whereValues)
        } catch(exception: Exception) {
            // データベースエラー
            Log.e("db error",
                "loadBase1:$tableName, columns=$columns, order_column_name=$orderColumnName, order_option=$orderOption, count=$count, whereString=$whereString, whereValues=$whereValues")
            Log.e("sql", sql)
            Log.e("whereValues", whereValues.toString())
            return mutableListOf()
        }

        if (cursor.count == 0) {
            Log.e("db success", "no data:$tableName")
            return mutableListOf()
        } else if (tableName != "success_history" && cursor.count != 1) {
            // エラー
            Log.e("db error",
                "loadBase2:$tableName, columns=$columns, order_column_name=$orderColumnName, order_option=$orderOption, count=$count, whereString=$whereString, whereValues=$whereValues"
            )
            return mutableListOf()
        }

        cursor.moveToFirst()

        val result: MutableList<MutableMap<String, String>> = mutableListOf()
        var rowIndex = 0
        while (!cursor.isAfterLast) {
            result.add(mutableMapOf())
            var columnIndex = 0
            for (column in columns) {
                when {
                    cursor.getType(columnIndex) == Cursor.FIELD_TYPE_STRING -> {
                        result[rowIndex][column] = cursor.getString(columnIndex)
                    }
                    cursor.getType(columnIndex) == Cursor.FIELD_TYPE_INTEGER -> {
                        result[rowIndex][column] = cursor.getInt(columnIndex).toString()
                    }
                    else -> {
                        result[rowIndex][column] = ""
                    }
                }
                columnIndex += 1
            }
            cursor.moveToNext()
            rowIndex += 1
        }

        cursor.close()

        return result
    }

    private fun updateBase(tableName: String, values: MutableMap<String, String?>): Boolean {
        val database = this.writableDatabase

        if (values["id"] == null) {
            val cursor = database.rawQuery("SELECT id FROM $tableName ORDER BY id DESC LIMIT 1", null)
                    .apply {
                        moveToFirst()
                    }
            values["id"] = cursor.getInt(0).toString()

            cursor.close()
        }

        val whereClauses = "id = ?"
        val whereArgs = arrayOf(values["id"])

        val settingsValues = ContentValues().apply {
            for (column in values.keys) {
                if (column == "id") {
                    continue
                }

                if (values[column] != null) {
                    put(column, values[column])
                }
            }
        }

        try {
            database.update(tableName, settingsValues, whereClauses, whereArgs)
        } catch(exception: Exception) {
            Log.e("db error", "updateBase:$tableName, values=$values")
            return false
        }

        return true
    }

    private fun insertBase(tableName: String, values: Map<String, String?>, database: SQLiteDatabase? = this.writableDatabase): Boolean {
        val insertValues = ContentValues().apply {
            for (column in values.keys) {
                if (values[column] != null) {
                    put(column, values[column])
                }
            }
        }

        try {
            database!!.insertOrThrow(tableName, null, insertValues)
        } catch(exception: Exception) {
            Log.e("db error", "insertBase:$tableName, values=$values")
            return false
        }

        return true
    }

    /**
     * 各テーブルのload群
     */
    fun loadSettings(columns: List<String> = listOf("id", "counter_stop_count", "enabled_cube", "add_icon_read")): MutableMap<String, Int> {
        val settings = this.loadBase("settings", columns)
        val result: MutableMap<String, Int> = mutableMapOf()
        if (settings.isEmpty()) {
            return mutableMapOf()
        }

        for (key in settings[0].keys) {
            result[key] = settings[0][key]!!.toInt()
        }

        return result
    }

    private fun loadCurrentGameStatus(columns: List<String> = listOf("id", "use_cube_mode", "blindfold_mode", "size", "cube_side_number", "score", "time", "tap_count", "start_numbers", "finish_numbers")): MutableMap<String, String> {
        this.exists = false

        val result = this.loadBase("current_game_status", columns)

        if (result.isEmpty()) {
            return mutableMapOf()
        }

        this.exists = true

        return result[0].toMutableMap()
    }

    private fun loadSuccessHistories(orderColumnName: String = "created_at", orderOption: String = "DESC", count: Int = 9, whereString: String = "", whereValues: Array<String>? = null, columns: List<String> = listOf("id", "created_at", "use_cube_mode", "blindfold_mode", "size", "score", "time", "tap_count", "start_numbers", "finish_numbers")): MutableList<MutableMap<String, String>> {
        return this.loadBase("success_history", columns, orderColumnName, orderOption, count, whereString, whereValues)
    }

    /**
     * 各テーブルのinsert, update群
     */
    fun writeSettings(values: MutableMap<String, String?> = mutableMapOf("id" to null, "counter_stop_count" to 0.toString(), "enabled_cube" to 0.toString())): Boolean {
        return this.updateBase("settings", values)
    }

    private fun writeCurrentGameStatus(values: MutableMap<String, String?> = mutableMapOf("id" to null, "use_cube_mode" to 0.toString(), "blindfold_mode" to 0.toString(), "size" to 1.toString(), "cube_side_number" to 0.toString(), "score" to 0.toString(), "time" to 0.toString(), "tap_count" to 0.toString(), "start_numbers" to null, "finish_numbers" to null)): Boolean {
        return this.updateBase("current_game_status", values)
    }

    private fun writeSuccessHistory(values: MutableMap<String, String?> = mutableMapOf("id" to null, "created_at" to null, "use_cube_mode" to 0.toString(), "blindfold_mode" to 0.toString(), "size" to 1.toString(), "score" to 0.toString(), "time" to 0.toString(), "tap_count" to 0.toString(), "start_numbers" to null, "finish_numbers" to null)): Boolean {
        return this.insertBase("success_history", values)
    }

    /**
     * 数字配列の変換群
     */
    private fun numbersToString(numbers: MutableList<MutableList<MutableList<Int>>>, nonNumberPanelPosition: MutableMap<String, Int>): String {
        var string = ""

        for (cubeSideIndex in 0 until numbers.size) {
            for (yIndex in 0 until numbers[cubeSideIndex].size) {
                var tmp = numbers[cubeSideIndex][yIndex].joinToString("").replace(" ", "")

                if (nonNumberPanelPosition["cubeSideNumber"]!!.toInt() == cubeSideIndex
                    && nonNumberPanelPosition["y"]!!.toInt() == yIndex) {
                    tmp = tmp.replaceRange(nonNumberPanelPosition["x"]!!.toInt(), nonNumberPanelPosition["x"]!!.toInt() + 1, " ")
                }

                string += tmp
            }
        }

        return string
    }

    fun stringToNumbers(string: String): MutableList<MutableList<MutableList<Int>>> {
        val numbers = MutableList(6) {MutableList(9) {MutableList(9) {0} } }
        val dimensional3s = string.chunked(81)
        for (cubeSideIndex in dimensional3s.indices) {
            val dimensional2s = dimensional3s[cubeSideIndex].chunked(9)
            for (yIndex in dimensional2s.indices) {
                val dimensional1s = dimensional2s[yIndex].chunked(1)
                for (xIndex in dimensional1s.indices) {
                    if (dimensional1s[xIndex] == " ") {
                        this.dataNonNumberPanelPosition = mutableMapOf(
                            "cubeSideNumber" to cubeSideIndex,
                            "y" to yIndex,
                            "x" to xIndex,
                        )
                        numbers[cubeSideIndex][yIndex][xIndex] = 9
                    } else {
                        numbers[cubeSideIndex][yIndex][xIndex] = dimensional1s[xIndex].toInt()
                    }
                }
            }
        }

        return numbers
    }

    private fun loadGameStartNumbers(): String {
        val currentGameStatus = this.loadCurrentGameStatus()

        if (currentGameStatus.containsKey("start_numbers")) {
            return currentGameStatus["start_numbers"].toString()
        }

        return ""
    }

    // 初期insert
    // DBがない時はNumberMasterOpenHelperのinitialInsertの値が使われる。
    private fun initialInsert(database: SQLiteDatabase?): Boolean {
        val result = this.insertBase("settings", mapOf(
            "counter_stop_count" to 0.toString(),
            "enabled_cube" to 0.toString(),
            "add_icon_read" to 1    .toString(),
        ), database)

        if (!result) return false

        return this.insertBase("current_game_status", mapOf(
            "use_cube_mode" to 0.toString(),
            "blindfold_mode" to 0.toString(),
            "size" to 1.toString(),
            "cube_side_number" to 0.toString(),
            "score" to 0.toString(),
            "time" to 0.toString(),
            "tap_count" to 0.toString(),
            "start_numbers" to this.numbersToString(this.dataNumbers, this.dataNonNumberPanelPosition),
            "finish_numbers" to this.numbersToString(this.dataNumbers, this.dataNonNumberPanelPosition),
        ), database)
    }

    /**
     * 外部から呼ばれる処理群
     */

    // ゲーム画面起動時に実行
    fun loadGame(): Boolean {
        val settings = this.loadSettings()
        if (settings.containsKey("counter_stop_count")) {
            this.dataGame["settings"]!!["counter_stop_count"] = settings["counter_stop_count"]!!
            this.dataGame["settings"]!!["enabled_cube"] = settings["enabled_cube"]!!
            this.dataGame["settings"]!!["add_icon_read"] = settings["add_icon_read"]!!
        }

        val currentGameStatus = this.loadCurrentGameStatus()
        if (currentGameStatus.containsKey("use_cube_mode")) {
            this.dataGame["current_game_status"]!!["use_cube_mode"] = currentGameStatus["use_cube_mode"]!!.toInt()
            this.dataGame["current_game_status"]!!["blindfold_mode"] = currentGameStatus["blindfold_mode"]!!.toInt()
            this.dataGame["current_game_status"]!!["size"] = currentGameStatus["size"]!!.toInt()
            this.dataGame["current_game_status"]!!["cube_side_number"] = currentGameStatus["cube_side_number"]!!.toInt()
            this.dataGame["current_game_status"]!!["score"] = currentGameStatus["score"]!!.toInt()
            this.dataGame["current_game_status"]!!["time"] = currentGameStatus["time"]!!.toInt()
            this.dataGame["current_game_status"]!!["tap_count"] = currentGameStatus["tap_count"]!!.toInt()
            this.dataNumbers = this.stringToNumbers(currentGameStatus["finish_numbers"]!!)
        }

        return true
    }

    // シャッフル時に実行
    fun writeGameStartNumbers(startNonNumberPanelPosition: MutableMap<String, Int>, startNumbers: MutableList<MutableList<MutableList<Int>>>): Boolean {
        return this.writeCurrentGameStatus(mutableMapOf(
            "start_numbers" to this.numbersToString(startNumbers, startNonNumberPanelPosition),
        ))
    }

    fun makeStatus(numbers: String): MutableMap<String, String> {
        val status = mutableMapOf(
            "use_cube_mode" to 0.toString(),
            "blindfold_mode" to 0.toString(),
            "size" to 1.toString(),
            "cube_side_number" to 0.toString(),
        )

        // 10行目1文字目(9x9+1)に数字があればuseCubeMode = 1
        val nextCubeSideFirstChar = numbers.substring(9 * 9 + 1, 9 * 9 + 2)
        status["use_cube_mode"] = (if (nextCubeSideFirstChar != "0") {
            1
        } else {
            0
        }).toString()

        val zeroIndex = numbers.indexOf("0")
        status["size"] = ((zeroIndex + 1) / 3).toString()
        if (status["size"]!!.toInt() > 3 || zeroIndex == -1) {
            // 9x9または9x9のcube mode
            status["size"] = 3.toString()
        }

        return status
    }

    private fun testMode(numbers: String): Boolean {
        if (this.testModeNumbers.contains(numbers)) {
            val values = mutableMapOf(
                "counter_stop_count" to 0,
                "enabled_cube" to 0,
                "use_cube_mode" to 0,
            )
            if (numbers == "000") {
                values["counter_stop_count"] = 0
                values["enabled_cube"] = 0
                values["use_cube_mode"] = 0
            } else if (numbers == "111") {
                values["counter_stop_count"] = 1
                values["enabled_cube"] = 1
                values["use_cube_mode"] = 1
            }

            this.writeSettings(
                mutableMapOf(
                    "counter_stop_count" to values["counter_stop_count"].toString(),
                    "enabled_cube" to values["enabled_cube"].toString(),
                    "add_icon_read" to 1.toString(),
                )
            )

            val numberMasterCalculator = NumberMasterCalculator()
            val shuffledNumbers = numberMasterCalculator.shuffleNumbers(0, 1)
            val nonNumberPanelPosition =
                numberMasterCalculator.getRandom9Position(0, 1, shuffledNumbers)
            val numbersString = this.numbersToString(shuffledNumbers, nonNumberPanelPosition)
            this.writeCurrentGameStatus(
                mutableMapOf(
                    "use_cube_mode" to values["use_cube_mode"].toString(),
                    "blindfold_mode" to 0.toString(),
                    "size" to 1.toString(),
                    "cube_side_number" to 0.toString(),
                    "score" to 0.toString(),
                    "time" to 0.toString(),
                    "tap_count" to 0.toString(),
                    "start_numbers" to numbersString,
                    "finish_numbers" to numbersString,
                )
            )

            return true
        }

        return false
    }

    fun writeByInput(numbers: String) {
        if (this.testMode(numbers)) return

        val status = this.makeStatus(numbers)

        this.writeCurrentGameStatus(mutableMapOf(
            "use_cube_mode" to status["use_cube_mode"],
            "blindfold_mode" to 0.toString(),
            "size" to status["size"],
            "cube_side_number" to 0.toString(),
            "score" to 0.toString(),
            "time" to 0.toString(),
            "tap_count" to 0.toString(),
            "start_numbers" to numbers,
            "finish_numbers" to numbers,
        ))
    }

    // stopボタン押下時に実行
    fun writeByStopButton(settings: MutableMap<String, String>, statusGame: MutableMap<String, String>, statusPuzzle: MutableMap<String, String>, nonNumberPanelPosition: MutableMap<String, Int>, numbers: MutableList<MutableList<MutableList<Int>>>): Boolean {
        val result = this.writeSettings(mutableMapOf(
            "counter_stop_count" to settings["counterStopCount"].toString(),
            "enabled_cube" to settings["enabledCube"].toString(),
            "add_icon_read" to settings["addIconRead"].toString(),
        ))

        if (!result) return false


        return this.writeCurrentGameStatus(mutableMapOf(
            "use_cube_mode" to statusPuzzle["useCubeMode"].toString(),
            "blindfold_mode" to statusPuzzle["blindfoldMode"].toString(),
            "size" to statusPuzzle["size"].toString(),
            "cube_side_number" to statusPuzzle["cubeSideNumber"].toString(),
            "score" to statusGame["score"].toString(),
            "time" to statusGame["time"].toString(),
            "tap_count" to statusGame["tapCount"].toString(),
            "start_numbers" to this.loadGameStartNumbers(),
            "finish_numbers" to this.numbersToString(numbers, nonNumberPanelPosition),
        ))
    }

    // ランキング画面表示時に実行
    fun loadHistory(orderColumnName: String = "created_at", orderOption: String = "DESC", startNumber: String = ""): List<Map<String, String>> {
        var whereString = ""
        var whereValues: Array<String>? = null

        if (startNumber != "") {
            whereString = "start_numbers = ?"
            whereValues = arrayOf(startNumber)
        }

        return this.loadSuccessHistories(orderColumnName, orderOption, 9, whereString, whereValues)
    }

    fun loadHistoryStartNumber(id: Int): String {
        val result = this.loadSuccessHistories("created_at", "DESC", 1, "id = ?", arrayOf(id.toString()))
        return result[0]["start_numbers"]!!.toString()
    }

    fun loadHistoryFinishNumber(id: Int): String {
        val result = this.loadSuccessHistories("created_at", "DESC", 1, "id = ?", arrayOf(id.toString()))
        return result[0]["finish_numbers"]!!.toString()
    }

    fun deleteHistory(id: Int) {
        val database = this.writableDatabase

        database.delete("success_history", "id = ?", arrayOf(id.toString()))
    }

    // finishボタン押下時に実行
    fun writeHistory(statusGame: MutableMap<String, String>, statusPuzzle: MutableMap<String, String>, nonNumberPanelPosition: MutableMap<String, Int>, numbers: MutableList<MutableList<MutableList<Int>>>): Boolean {
        return this.writeSuccessHistory(mutableMapOf(
            "use_cube_mode" to statusPuzzle["useCubeMode"].toString(),
            "blindfold_mode" to statusPuzzle["blindfoldMode"].toString(),
            "size" to statusPuzzle["size"].toString(),
            "score" to statusGame["score"].toString(),
            "time" to statusGame["time"].toString(),
            "tap_count" to statusGame["tapCount"].toString(),
            "start_numbers" to this.loadGameStartNumbers(),
            "finish_numbers" to this.numbersToString(numbers, nonNumberPanelPosition),
        ))
    }
}