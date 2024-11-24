package com.zeekrlife.connect.core.data.database

import android.database.Cursor
import androidx.annotation.Keep
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
@Keep
@Dao
interface AppShortcutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAppShortcut(appShortcut: AppShortcutBean): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAppShortcutList(list: List<AppShortcutBean>): LongArray?

    @Query("SELECT * FROM hicar_shortcut WHERE id = :id")
    fun queryAppShortcut(id: Long): Cursor?

    @Query("SELECT * FROM hicar_shortcut")
    fun queryAppShortcutList(): Cursor?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateAppShortcut(appShort: AppShortcutBean): Int

    @Delete
    fun deleteAppShortcut(appShort: AppShortcutBean)

    @Query("DELETE FROM hicar_shortcut WHERE id = :id")
    fun deleteById(id: Long): Int

    @Query("DELETE FROM hicar_shortcut")
    fun deleteAll()
}