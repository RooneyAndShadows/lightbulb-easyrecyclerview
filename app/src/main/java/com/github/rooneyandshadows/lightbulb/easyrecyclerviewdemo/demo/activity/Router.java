package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity;

public class Router {
    private static Router instance = null;
    private AppRouter router;

    public static synchronized Router getInstance() {
        if (null == instance) {
            instance = new Router();
        }
        return instance;
    }

    public AppRouter getRouter() {
        return router;
    }

    public void setRouter(AppRouter router) {
        this.router = router;
    }
}