package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models;

import android.os.Parcel;

import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelableUtils;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterDataModel;

public class StickyDemoModel extends EasyAdapterDataModel {
    private final boolean isHeader;
    private final String title;
    private final String subtitle;


    public StickyDemoModel(boolean isHeader, String title, String subtitle) {
        super(false);
        this.isHeader = isHeader;
        this.title = title;
        this.subtitle = subtitle;
    }

    // Parcelling part
    public StickyDemoModel(Parcel in) {
        super(in);
        this.isHeader = ParcelableUtils.readBoolean(in);
        this.title = ParcelableUtils.readString(in);
        this.subtitle = ParcelableUtils.readString(in);
    }

    public static final Creator<StickyDemoModel> CREATOR = new Creator<StickyDemoModel>() {
        public StickyDemoModel createFromParcel(Parcel in) {
            return new StickyDemoModel(in);
        }

        public StickyDemoModel[] newArray(int size) {
            return new StickyDemoModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        ParcelableUtils
                .writeBoolean(parcel, isHeader)
                .writeString(parcel, title)
                .writeString(parcel, subtitle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String getItemName() {
        return title;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}