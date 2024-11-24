package com.zeekrlife.hicar.data.database

import android.content.ContentValues
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [AppShortcutBean::class],
    version = 1,
    exportSchema = false
)
abstract class AppShortcutDB : RoomDatabase() {
    companion object {
        @Volatile
        private var instance: AppShortcutDB? = null
        fun getInstance(context: Context): AppShortcutDB {
            return instance ?: synchronized(this) {
                return instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppShortcutDB::class.java,
                    "shortcut.db"
                ).allowMainThreadQueries()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                        }

                    }).build()
            }
        }

        fun fromContentValues(values: ContentValues?): AppShortcutBean {
            val appShortcutBean = AppShortcutBean()
            if (values != null && values.containsKey("id")) {
                appShortcutBean.id = values.getAsLong("id")
            }
            if (values != null && values.containsKey("mPackageName")) {
                appShortcutBean.mPackageName = values.getAsString("mPackageName")
            }
            if (values != null && values.containsKey("mName")) {
                appShortcutBean.mName = values.getAsString("mName")
            }
            if (values != null && values.containsKey("mType")) {
                appShortcutBean.mType = values.getAsInteger("mType")
            }
            if (values != null && values.containsKey("mIcon")) {
                appShortcutBean.mIcon = values.getAsByteArray("mIcon")
            }
            return appShortcutBean
        }
    }

    abstract fun getAppShortcutDao(): AppShortcutDao
}