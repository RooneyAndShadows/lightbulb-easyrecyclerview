package com.github.rooneyandshadows.lightbulb.easyrecyclerviewdemo.demo.models;

import android.os.Parcel;

import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate;
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelableUtils;
import com.github.rooneyandshadows.lightbulb.recycleradapters.EasyAdapterDataModel;

import java.time.OffsetDateTime;

public class StickyAdvancedDemoModel extends EasyAdapterDataModel {
    private final OffsetDateTime date;
    private final boolean isHeader;
    private final String title;
    private final String subtitle;
    private final String dateString;


    public StickyAdvancedDemoModel(OffsetDateTime date, boolean isHeader, String title, String subtitle) {
        super(false);
        this.date = date;
        this.isHeader = isHeader;
        this.title = title;
        this.subtitle = subtitle;
        this.dateString = DateUtilsOffsetDate.getDateString("dd\nMMM", date);
    }

    // Parcelling part
    public StickyAdvancedDemoModel(Parcel in) {
        super(in);
        this.date = ParcelableUtils.readOffsetDateTime(in);
        this.isHeader = ParcelableUtils.readBoolean(in);
        this.title = ParcelableUtils.readString(in);
        this.subtitle = ParcelableUtils.readString(in);
        this.dateString = ParcelableUtils.readString(in);
    }

    public static final Creator<StickyAdvancedDemoModel> CREATOR = new Creator<StickyAdvancedDemoModel>() {
        public StickyAdvancedDemoModel createFromParcel(Parcel in) {
            return new StickyAdvancedDemoModel(in);
        }

        public StickyAdvancedDemoModel[] newArray(int size) {
            return new StickyAdvancedDemoModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        ParcelableUtils
                .writeOffsetDateTime(parcel, date)
                .writeBoolean(parcel, isHeader)
                .writeString(parcel, title)
                .writeString(parcel, subtitle)
                .writeString(parcel, dateString);
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

    public String getDateString() {
        return dateString;
    }
}