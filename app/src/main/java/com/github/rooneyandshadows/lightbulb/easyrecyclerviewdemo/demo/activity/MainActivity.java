package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity;

import android.os.Bundle;

import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity;
import com.github.rooneyandshadows.lightbulb.application.activity.routing.BaseApplicationRouter;

import androidx.annotation.Nullable;

public class MainActivity extends BaseActivity {
    private AppRouter router;

    @Nullable
    @Override
    protected BaseApplicationRouter initializeRouter(int fragmentContainerId) {
        router = new AppRouter(this, fragmentContainerId);
        Router.getInstance().setRouter(router);
        return router;
    }

    @Override
    protected void create(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null)
            router.toDemoRegular(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO_AND_CLEAR_BACKSTACK);
    }
}