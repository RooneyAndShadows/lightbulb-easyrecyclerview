package com.github.rooneyandshadowss.lightbulb.easyrecyclerviewdemo.demo.models

import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils.Companion.writeString
import android.os.Parcel
import android.os.Parcelable.Creator
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel

class StickySimpleDemoModel : EasyAdapterDataModel {
    val isHeader: Boolean
    val subtitle: String
    override val itemName: String

    constructor(isHeader: Boolean, title: String, subtitle: String) {
        this.isHeader = isHeader
        itemName = title
        this.subtitle = subtitle
    }

    // Parcelling part
    constructor(parcel: Parcel) {
        isHeader = ParcelUtils.readBoolean(parcel)!!
        itemName = ParcelUtils.readString(parcel)!!
        subtitle = ParcelUtils.readString(parcel)!!
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        writeString(parcel, subtitle)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<StickySimpleDemoModel> {
        override fun createFromParcel(parcel: Parcel): StickySimpleDemoModel {
            return StickySimpleDemoModel(parcel)
        }

        override fun newArray(size: Int): Array<StickySimpleDemoModel?> {
            return arrayOfNulls(size)
        }
    }
}