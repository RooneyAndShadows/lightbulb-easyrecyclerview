package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo

import android.content.Context
import android.graphics.drawable.Drawable
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.drawable.ShowMenuDrawable
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.StickyAdvancedDemoModel
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models.StickySimpleDemoModel
import java.time.OffsetDateTime
import java.util.*

fun getHomeIcon(context: Context): Drawable {
    return ShowMenuDrawable(context).apply {
        setEnabled(false)
        backgroundColor = ResourceUtils.getColorByAttribute(context, R.attr.colorError)
    }
    //actionBarManager.setHomeIcon(actionBarDrawable)
}

fun generateData(count: Int): List<DemoModel> {
    val models: MutableList<DemoModel> = mutableListOf()
    for (i in 1..count) models.add(DemoModel("Demo title $i", "Demo subtitle $i"))
    return models
}

fun generateData(count: Int, offset: Int): List<DemoModel> {
    val models: MutableList<DemoModel> = mutableListOf()
    for (i in 1..count) {
        val number = i + offset
        models.add(DemoModel("Demo title $number", "Demo subtitle $number"))
    }
    return models
}

fun generateStickyHeadersSimpleData(): List<StickySimpleDemoModel> {
    val models: MutableList<StickySimpleDemoModel> = mutableListOf()
    for (i in 1..9) {
        models.add(StickySimpleDemoModel(true, "Header $i", ""))
        for (j in 1..7) models.add(
            StickySimpleDemoModel(
                false,
                String.format("Demo title %s.%s", i, j),
                String.format("Demo subtitle %s.%s", i, j)
            )
        )
    }
    return models
}

fun generateStickyHeadersAdvanceData(): List<StickyAdvancedDemoModel> {
    val models: MutableList<StickyAdvancedDemoModel> = mutableListOf()
    var date: OffsetDateTime = DateUtilsOffsetDate.nowLocal()
    for (position in 1..60) {
        val isHeader = isPositionHeader(position)
        models.add(
            StickyAdvancedDemoModel(
                date,
                isHeader,
                String.format("Demo title %s", position),
                String.format("Demo subtitle %s", position)
            )
        )
        if (isHeader) date = DateUtilsOffsetDate.addHours(date, 24)
    }
    return models
}

fun generateLabelsData(): List<DemoModel> {
    val models: MutableList<DemoModel> = mutableListOf()
    models.add(DemoModel("Star", ""))
    models.add(DemoModel("Tag", ""))
    models.add(DemoModel("Search", ""))
    models.add(DemoModel("Block", ""))
    models.add(DemoModel("Center", ""))
    models.add(DemoModel("Right", ""))
    models.add(DemoModel("Cat", ""))
    models.add(DemoModel("Tree", ""))
    models.add(DemoModel("Person", ""))
    models.add(DemoModel("Generation", ""))
    models.add(DemoModel("Utility", ""))
    models.add(DemoModel("Category", ""))
    models.add(DemoModel("Label", ""))
    models.add(DemoModel("Side", ""))
    models.add(DemoModel("Section", ""))
    models.add(DemoModel("Page", ""))
    models.add(DemoModel("Class", ""))
    models.add(DemoModel("Type", ""))
    models.add(DemoModel("Performance", ""))
    models.add(DemoModel("Object", ""))
    models.add(DemoModel("Count", ""))
    models.add(DemoModel("Letter", ""))
    models.add(DemoModel("Subtitle", ""))
    models.add(DemoModel("Height", ""))
    models.add(DemoModel("Strenght", ""))
    models.add(DemoModel("Star", ""))
    models.add(DemoModel("Tag", ""))
    models.add(DemoModel("Search", ""))
    models.add(DemoModel("Block", ""))
    models.add(DemoModel("Center", ""))
    models.add(DemoModel("Right", ""))
    models.add(DemoModel("Cat", ""))
    models.add(DemoModel("Tree", ""))
    models.add(DemoModel("Person", ""))
    models.add(DemoModel("Generation", ""))
    models.add(DemoModel("Utility", ""))
    models.add(DemoModel("Category", ""))
    models.add(DemoModel("Label", ""))
    models.add(DemoModel("Side", ""))
    models.add(DemoModel("Section", ""))
    models.add(DemoModel("Page", ""))
    models.add(DemoModel("Class", ""))
    models.add(DemoModel("Type", ""))
    models.add(DemoModel("Performance", ""))
    models.add(DemoModel("Object", ""))
    models.add(DemoModel("Count", ""))
    models.add(DemoModel("Letter", ""))
    models.add(DemoModel("Subtitle", ""))
    models.add(DemoModel("Height", ""))
    models.add(DemoModel("Strenght", ""))
    models.add(DemoModel("Star", ""))
    models.add(DemoModel("Tag", ""))
    models.add(DemoModel("Search", ""))
    models.add(DemoModel("Block", ""))
    models.add(DemoModel("Center", ""))
    models.add(DemoModel("Right", ""))
    models.add(DemoModel("Cat", ""))
    models.add(DemoModel("Tree", ""))
    models.add(DemoModel("Person", ""))
    models.add(DemoModel("Generation", ""))
    models.add(DemoModel("Utility", ""))
    models.add(DemoModel("Category", ""))
    models.add(DemoModel("Label", ""))
    models.add(DemoModel("Side", ""))
    models.add(DemoModel("Section", ""))
    models.add(DemoModel("Page", ""))
    models.add(DemoModel("Class", ""))
    models.add(DemoModel("Type", ""))
    models.add(DemoModel("Performance", ""))
    models.add(DemoModel("Object", ""))
    models.add(DemoModel("Count", ""))
    models.add(DemoModel("Letter", ""))
    models.add(DemoModel("Subtitle", ""))
    models.add(DemoModel("Height", ""))
    models.add(DemoModel("Strenght", ""))
    models.add(DemoModel("Star", ""))
    models.add(DemoModel("Tag", ""))
    models.add(DemoModel("Search", ""))
    models.add(DemoModel("Block", ""))
    models.add(DemoModel("Center", ""))
    models.add(DemoModel("Right", ""))
    models.add(DemoModel("Cat", ""))
    models.add(DemoModel("Tree", ""))
    models.add(DemoModel("Person", ""))
    models.add(DemoModel("Generation", ""))
    models.add(DemoModel("Utility", ""))
    models.add(DemoModel("Category", ""))
    models.add(DemoModel("Label", ""))
    models.add(DemoModel("Side", ""))
    models.add(DemoModel("Section", ""))
    models.add(DemoModel("Page", ""))
    models.add(DemoModel("Class", ""))
    models.add(DemoModel("Type", ""))
    models.add(DemoModel("Performance", ""))
    models.add(DemoModel("Object", ""))
    models.add(DemoModel("Count", ""))
    models.add(DemoModel("Letter", ""))
    models.add(DemoModel("Subtitle", ""))
    models.add(DemoModel("Height", ""))
    models.add(DemoModel("Strenght", ""))
    models.add(DemoModel("Star", ""))
    models.add(DemoModel("Tag", ""))
    models.add(DemoModel("Search", ""))
    models.add(DemoModel("Block", ""))
    models.add(DemoModel("Center", ""))
    models.add(DemoModel("Right", ""))
    models.add(DemoModel("Cat", ""))
    models.add(DemoModel("Tree", ""))
    models.add(DemoModel("Person", ""))
    models.add(DemoModel("Generation", ""))
    models.add(DemoModel("Utility", ""))
    models.add(DemoModel("Category", ""))
    models.add(DemoModel("Label", ""))
    models.add(DemoModel("Side", ""))
    models.add(DemoModel("Section", ""))
    models.add(DemoModel("Page", ""))
    models.add(DemoModel("Class", ""))
    models.add(DemoModel("Type", ""))
    models.add(DemoModel("Performance", ""))
    models.add(DemoModel("Object", ""))
    models.add(DemoModel("Count", ""))
    models.add(DemoModel("Letter", ""))
    models.add(DemoModel("Subtitle", ""))
    models.add(DemoModel("Height", ""))
    models.add(DemoModel("Strenght", ""))
    return models
}

private fun isPositionHeader(position: Int): Boolean {
    val headerPositions = intArrayOf(1, 7, 12, 20, 25, 34, 40)
    return Arrays.stream(headerPositions).anyMatch { value: Int -> position == value }
}