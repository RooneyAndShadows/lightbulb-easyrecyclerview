package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models

import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils.Companion.writeString
import android.os.Parcel
import android.os.Parcelable.Creator
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import java.time.OffsetDateTime

class StickyAdvancedDemoModel : EasyAdapterDataModel {
    private val date: OffsetDateTime
    val isHeader: Boolean
    override val itemName: String
    val subtitle: String
    val dateString: String

    constructor(date: OffsetDateTime, isHeader: Boolean, title: String, subtitle: String) {
        this.date = date
        this.isHeader = isHeader
        itemName = title
        this.subtitle = subtitle
        dateString = DateUtilsOffsetDate.getDateString("dd\nMMM", date)
    }

    // Parcelling part
    constructor(`in`: Parcel?) {
        date = readOffsetDateTime.readOffsetDateTime(`in`)
        isHeader = readBoolean.readBoolean(`in`)
        itemName = readString.readString(`in`)
        subtitle = readString.readString(`in`)
        dateString = readString.readString(`in`)
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        writeString(parcel, dateString)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Creator<StickyAdvancedDemoModel> = object : Creator<StickyAdvancedDemoModel?> {
            override fun createFromParcel(`in`: Parcel): StickyAdvancedDemoModel? {
                return StickyAdvancedDemoModel(`in`)
            }

            override fun newArray(size: Int): Array<StickyAdvancedDemoModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}