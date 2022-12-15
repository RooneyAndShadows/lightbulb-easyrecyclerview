package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models

import android.os.Parcel
import android.os.Parcelable.Creator
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel

class DemoModel : EasyAdapterDataModel {
    override val itemName: String
    val subtitle: String

    constructor(title: String, subtitle: String) {
        itemName = title
        this.subtitle = subtitle
    }

    // Parcelling part
    constructor(parcel: Parcel) {
        itemName = parcel.readString()!!
        subtitle = parcel.readString()!!
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(itemName)
        dest.writeString(subtitle)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Creator<DemoModel> {
        override fun createFromParcel(parcel: Parcel): DemoModel {
            return DemoModel(parcel)
        }

        override fun newArray(size: Int): Array<DemoModel?> {
            return arrayOfNulls(size)
        }
    }
}