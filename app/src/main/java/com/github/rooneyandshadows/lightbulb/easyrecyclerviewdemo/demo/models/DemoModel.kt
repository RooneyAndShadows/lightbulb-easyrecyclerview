package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models

import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbParcelable
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.collection.ExtendedCollection.Item

@LightbulbParcelable
class DemoModel(
    var title: String,
    var subtitle: String
) : Item() {
    override val itemName: String
        get() = title
}