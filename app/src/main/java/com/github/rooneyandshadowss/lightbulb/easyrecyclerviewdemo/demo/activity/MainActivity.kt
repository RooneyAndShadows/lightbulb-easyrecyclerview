package com.github.rooneyandshadowss.lightbulb.easyrecyclerviewdemo.demo.activity

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
        if (savedInstanceState == null)
            _root_ide_package_.com.github.rooneyandshadowss.lightbulb.easyrecyclerviewdemo.demo.activity.MainActivityNavigator.route().toDemoRegular().newRootScreen()
    }
}