package com.f0x1d.dogbin.utils;

public class Event {

    private boolean mConsumed;
    private final String mType;
    private final Object mData;

    public Event(String type, Object data) {
        mType = type;
        mData = data;
    }

    public String type() {
        return mType;
    }

    public <T> T consume() {
        if (mConsumed)
            return null;

        mConsumed = true;
        return (T) mData;
    }

    public boolean isConsumed() {
        return mConsumed;
    }
}