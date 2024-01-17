package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models

import androidx.databinding.Bindable
import com.github.rooneyandshadows.lightbulb.commons.databinding.ObservableProperty
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.BR
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterObservableDataModel

class DemoModel(title: String, subtitle: String) : EasyAdapterObservableDataModel() {
    @get:Bindable
    var title: String by ObservableProperty(title, BR.title)

    @get:Bindable
    var subtitle: String by ObservableProperty(subtitle, BR.subtitle)

    @get:Bindable("title")
    override val itemName: String
        get() = title
}