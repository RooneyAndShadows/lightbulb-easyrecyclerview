package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models

import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbParcelable
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