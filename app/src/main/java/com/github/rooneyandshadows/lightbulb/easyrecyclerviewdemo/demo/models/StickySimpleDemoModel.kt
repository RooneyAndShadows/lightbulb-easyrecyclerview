package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models

import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils.Companion.writeString
import android.os.Parcel
import android.os.Parcelable.Creator
import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbParcelable
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel

@LightbulbParcelable
class StickySimpleDemoModel : EasyAdapterDataModel {
    val isHeader: Boolean
    val subtitle: String
    override val itemName: String

    constructor(isHeader: Boolean, title: String, subtitle: String) {
        this.isHeader = isHeader
        itemName = title
        this.subtitle = subtitle
    }
}