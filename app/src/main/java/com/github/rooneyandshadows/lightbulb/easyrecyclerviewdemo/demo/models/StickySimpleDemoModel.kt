package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models

import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils.Companion.writeString
import android.os.Parcel
import android.os.Parcelable.Creator
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel

class StickySimpleDemoModel : EasyAdapterDataModel {
    val isHeader: Boolean
    override val itemName: String
    val subtitle: String

    constructor(isHeader: Boolean, title: String, subtitle: String) {
        this.isHeader = isHeader
        itemName = title
        this.subtitle = subtitle
    }

    // Parcelling part
    constructor(`in`: Parcel?) {
        isHeader = readBoolean.readBoolean(`in`)
        itemName = readString.readString(`in`)
        subtitle = readString.readString(`in`)
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        writeString(parcel, subtitle)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        val CREATOR: Creator<StickySimpleDemoModel> = object : Creator<StickySimpleDemoModel?> {
            override fun createFromParcel(`in`: Parcel): StickySimpleDemoModel? {
                return StickySimpleDemoModel(`in`)
            }

            override fun newArray(size: Int): Array<StickySimpleDemoModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}