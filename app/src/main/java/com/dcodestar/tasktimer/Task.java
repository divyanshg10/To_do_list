package com.dcodestar.tasktimer;

import java.io.Serializable;

class Task implements Serializable {
    public static final long serialVersionUID=20190811L;

    private long m_Id;
    private final String mName;
    private final String mDescription;
    private final int mSortOrder;

    public Task(long _id, String mName, String mDescription, int mSortOrder) {
        this.m_Id = _id;
        this.mName = mName;
        this.mDescription = mDescription;
        this.mSortOrder = mSortOrder;
    }

    public long getId() {
        return m_Id;
    }

    public String getmName() {
        return mName;
    }

    public String getmDescription() {
        return mDescription;
    }

    public int getmSortOrder() {
        return mSortOrder;
    }

    public void setId(long m_Id) {
        this.m_Id = m_Id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "m_Id=" + m_Id +
                ", mName='" + mName + '\'' +
                ", mDescription='" + mDescription + '\'' +
                ", mSortOrder=" + mSortOrder +
                '}';
    }
}
