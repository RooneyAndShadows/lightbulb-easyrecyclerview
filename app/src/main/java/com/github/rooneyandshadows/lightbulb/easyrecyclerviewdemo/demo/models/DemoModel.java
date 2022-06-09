package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelableUtils;
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel;

import java.util.List;

public class DemoModel extends EasyAdapterDataModel {
    private final String title;
    private final String subtitle;


    public DemoModel(String title, String subtitle) {
        super(false);
        this.title = title;
        this.subtitle = subtitle;
    }

    // Parcelling part
    public DemoModel(Parcel in) {
        super(in);
        this.title = ParcelableUtils.readString(in);
        this.subtitle = ParcelableUtils.readString(in);
    }

    public static final Parcelable.Creator<DemoModel> CREATOR = new Parcelable.Creator<DemoModel>() {
        public DemoModel createFromParcel(Parcel in) {
            return new DemoModel(in);
        }

        public DemoModel[] newArray(int size) {
            return new DemoModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        ParcelableUtils.writeString(parcel, title)
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

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}