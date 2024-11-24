package com.zeekrlife.hicar.data.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "hicar_shortcut")
data class AppShortcutBean(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var mPackageName: String? = null,
    var mName: String? = null,
    var mIcon: ByteArray? = null,
    var mType: Int = 0,
    ) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AppShortcutBean

        if (id != other.id) return false
        if (mPackageName != other.mPackageName) return false
        if (mName != other.mName) return false
        if (mIcon != null) {
            if (other.mIcon == null) return false
            if (!mIcon.contentEquals(other.mIcon)) return false
        } else if (other.mIcon != null) return false
        if (mType != other.mType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (mPackageName?.hashCode() ?: 0)
        result = 31 * result + (mName?.hashCode() ?: 0)
        result = 31 * result + (mIcon?.contentHashCode() ?: 0)
        result = 31 * result + mType
        return result
    }

}
