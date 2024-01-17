package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models

import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils.Companion.writeString
import android.os.Parcel
import android.os.Parcelable.Creator
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbParcelable
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel
import java.time.OffsetDateTime

@LightbulbParcelable
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
}