package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity

import android.os.Bundle
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.ActivityConfiguration
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R

@ActivityConfiguration
class MainActivity : BaseActivity() {

    @Override
    override fun doBeforeCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.DemoTheme)
    }

    @Override
    override fun doOnCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            updateMenuConfiguration(
                this,
                MainActivity::class.java
            ) { activity: BaseActivity -> MenuConfigurations.getConfiguration(activity) }
            MainActivityNavigator.route().toDemoRegular().newRootScreen()
        }
    }
}