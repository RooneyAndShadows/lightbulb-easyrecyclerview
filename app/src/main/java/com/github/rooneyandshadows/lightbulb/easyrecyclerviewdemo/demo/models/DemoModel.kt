package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models

import androidx.databinding.Bindable
import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbParcelable
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.BR
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterObservableDataModel

@LightbulbParcelable
class DemoModel(title: String, subtitle: String) : EasyAdapterObservableDataModel() {
    @get:Bindable
    var title: String = title
        set(value) {
            field = value
            notifyPropertyChanged(BR.title)
        }

    @get:Bindable
    var subtitle: String = subtitle
        set(value) {
            field = value
            notifyPropertyChanged(BR.subtitle)
        }

    @get:Bindable("title")
    override val itemName: String
        get() = title
}