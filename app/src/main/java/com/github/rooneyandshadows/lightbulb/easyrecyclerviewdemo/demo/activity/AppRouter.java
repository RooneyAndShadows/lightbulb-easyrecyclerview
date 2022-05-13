package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.activity;

import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity;
import com.github.rooneyandshadows.lightbulb.application.activity.routing.BaseApplicationRouter;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.BounceEffectDemoFragment;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.DragToReorderDemoFragment;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.EmptylayoutDemoFragment;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.FlowLayoutManagerDemoFragment;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.HeaderAndFooterDemoFragment;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.LazyLoadingDemoFragment;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.PullToRefreshDemoFragment;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.RegularDemoFragment;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.SelectionDemoFragment;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.StickyHeadersAdvancedDemoFragment;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.StickyHeadersSimpleDemoFragment;
import com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.fragments.SwipeToDeleteDemoFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class AppRouter extends BaseApplicationRouter {

    public AppRouter(BaseActivity contextActivity, int fragmentContainerId) {
        super(contextActivity, fragmentContainerId);
    }

    public void toDemoRegular(NavigationCommands command) {
        Screen screen = new Screens.Regular();
        navigate(command, screen);
    }

    public void toDemoSelectable(NavigationCommands command) {
        Screen screen = new Screens.Selectable();
        navigate(command, screen);
    }

    public void toFlowLayoutManager(NavigationCommands command) {
        Screen screen = new Screens.FlowLayoutManager();
        navigate(command, screen);
    }

    public void toDemoHeaderAndFooter(NavigationCommands command) {
        Screen screen = new Screens.HeaderAndFooter();
        navigate(command, screen);
    }

    public void toEmptyLayout(NavigationCommands command) {
        Screen screen = new Screens.EmptyLayout();
        navigate(command, screen);
    }

    public void toBounceEffect(NavigationCommands command) {
        Screen screen = new Screens.BounceEffect();
        navigate(command, screen);
    }

    public void toPullToRefresh(NavigationCommands command) {
        Screen screen = new Screens.PullToRefresh();
        navigate(command, screen);
    }

    public void toLazyLoading(NavigationCommands command) {
        Screen screen = new Screens.LazyLoading();
        navigate(command, screen);
    }

    public void toSwipeToDelete(NavigationCommands command) {
        Screen screen = new Screens.SwipeToDelete();
        navigate(command, screen);
    }

    public void toDragToReorder(NavigationCommands command) {
        Screen screen = new Screens.DragToReorder();
        navigate(command, screen);
    }

    public void toStickyHeadersSimple(NavigationCommands command) {
        Screen screen = new Screens.StickyHeadersSimple();
        navigate(command, screen);
    }

    public void toStickyHeadersAdvanced(NavigationCommands command) {
        Screen screen = new Screens.StickyHeadersAdvanced();
        navigate(command, screen);
    }

    //SCREENS...
    public static final class Screens {

        public static final class Regular extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return RegularDemoFragment.getNewInstance();
            }
        }

        public static final class Selectable extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return SelectionDemoFragment.getNewInstance();
            }
        }

        public static final class FlowLayoutManager extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return FlowLayoutManagerDemoFragment.getNewInstance();
            }
        }

        public static final class HeaderAndFooter extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return HeaderAndFooterDemoFragment.getNewInstance();
            }
        }

        public static final class EmptyLayout extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return EmptylayoutDemoFragment.getNewInstance();
            }
        }

        public static final class BounceEffect extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return BounceEffectDemoFragment.getNewInstance();
            }
        }

        public static final class PullToRefresh extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return PullToRefreshDemoFragment.getNewInstance();
            }
        }

        public static final class LazyLoading extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return LazyLoadingDemoFragment.getNewInstance();
            }
        }

        public static final class SwipeToDelete extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return SwipeToDeleteDemoFragment.getNewInstance();
            }
        }

        public static final class DragToReorder extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return DragToReorderDemoFragment.getNewInstance();
            }
        }

        public static final class StickyHeadersSimple extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return StickyHeadersSimpleDemoFragment.getNewInstance();
            }
        }

        public static final class StickyHeadersAdvanced extends Screen {
            @NonNull
            @Override
            public Fragment getFragment() {
                return StickyHeadersAdvancedDemoFragment.getNewInstance();
            }
        }
    }
}