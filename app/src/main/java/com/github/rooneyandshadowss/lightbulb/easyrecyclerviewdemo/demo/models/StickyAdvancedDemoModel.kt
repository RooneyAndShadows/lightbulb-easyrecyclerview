package com.github.rooneyandshadowss.lightbulb.easyrecyclerviewdemo.demo.models

import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils.Companion.writeString
import android.os.Parcel
import android.os.Parcelable.Creator
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import java.time.OffsetDateTime

class StickyAdvancedDemoModel : EasyAdapterDataModel {
    private val date: OffsetDateTime
    val isHeader: Boolean
    val subtitle: String
    val dateString: String
    override val itemName: String

    constructor(date: OffsetDateTime, isHeader: Boolean, title: String, subtitle: String) {
        this.date = date
        this.isHeader = isHeader
        itemName = title
        this.subtitle = subtitle
        dateString = DateUtilsOffsetDate.getDateString("dd\nMMM", date)
    }

    // Parcelling part
    constructor(parcel: Parcel) {
        date = ParcelUtils.readOffsetDateTime(parcel)!!
        isHeader = ParcelUtils.readBoolean(parcel)!!
        itemName = ParcelUtils.readString(parcel)!!
        subtitle = ParcelUtils.readString(parcel)!!
        dateString = ParcelUtils.readString(parcel)!!
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        writeString(parcel, dateString)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<StickyAdvancedDemoModel> {
        override fun createFromParcel(parcel: Parcel): StickyAdvancedDemoModel {
            return StickyAdvancedDemoModel(parcel)
        }

        override fun newArray(size: Int): Array<StickyAdvancedDemoModel?> {
            return arrayOfNulls(size)
        }
    }
}