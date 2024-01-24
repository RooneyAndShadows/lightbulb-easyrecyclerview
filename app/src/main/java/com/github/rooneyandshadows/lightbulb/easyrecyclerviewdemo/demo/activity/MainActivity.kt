package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity

import android.os.Bundle
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.config.SliderMenuConfiguration
import com.github.rooneyandshadows.lightbulb.apt.annotations.LightbulbActivity
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.lightbulb.service.LightbulbService

@LightbulbActivity(fragmentContainerId = "fragmentContainer")
class MainActivity : BaseActivity() {

    override fun doBeforeCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.DemoTheme)
    }

    override fun getMenuConfiguration(): SliderMenuConfiguration {
        return MenuConfigurations.getConfiguration(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            LightbulbService.route().toDemoRegular().newRootScreen()
        }
    }

    override fun onUnhandledException(paramThread: Thread?, exception: Throwable) {
        super.onUnhandledException(paramThread, exception)
        exception.printStackTrace()
    }
}