package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models

import android.os.Parcel
import android.os.Parcelable.Creator
import androidx.databinding.Bindable
import com.github.rooneyandshadows.lightbulb.commons.databinding.ObservableProperty
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.BR
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterObservableDataModel

class DemoModel : EasyAdapterObservableDataModel {
    @get:Bindable
    var title: String by ObservableProperty("", BR.title)

    @get:Bindable
    var subtitle: String by ObservableProperty("", BR.subtitle)

    override val itemName: String
        get() = title

    constructor(title: String, subtitle: String) {
        this.title = title
        this.subtitle = subtitle
    }

    constructor(parcel: Parcel) {
        title = parcel.readString()!!
        subtitle = parcel.readString()!!
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(title)
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