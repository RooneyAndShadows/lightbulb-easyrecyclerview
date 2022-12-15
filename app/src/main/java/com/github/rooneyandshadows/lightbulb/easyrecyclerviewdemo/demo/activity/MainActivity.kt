package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity

import android.os.Bundle
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.R

class MainActivity : BaseActivity() {

    @Override
    override fun doBeforeCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.DemoTheme)
    }

    override fun doOnCreate(savedInstanceState: Bundle?) {
        //if (savedInstanceState == null)
        //router!!.toDemoRegular(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO_AND_CLEAR_BACKSTACK)
    }
}