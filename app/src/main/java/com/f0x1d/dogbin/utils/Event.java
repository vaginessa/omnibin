package com.f0x1d.dogbin.utils;

public class Event {

    private boolean mConsumed;
    private final String mType;
    private final Object mData;
    private final Object[] mArguments;

    public Event(String type, Object data) {
        mType = type;
        mData = data;
        mArguments = new Object[0];
    }

    public Event(String type, Object data, Object... arguments) {
        mType = type;
        mData = data;
        mArguments = arguments;
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

    public <T> T argument(int index) {
        return (T) mArguments[index];
    }
}