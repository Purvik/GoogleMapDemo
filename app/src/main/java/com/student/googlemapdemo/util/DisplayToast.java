package com.student.googlemapdemo.util;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DisplayToast {

    private String mMessage;
    private int mDuraiton;
    private Context mContext;

    public DisplayToast(String mMessage, int mDuraiton, Context mContext) {
        this.mMessage = mMessage;
        this.mDuraiton = mDuraiton;
        this.mContext = mContext;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public int getmDuraiton() {
        return mDuraiton;
    }

    public void setmDuraiton(int mDuraiton) {
        this.mDuraiton = mDuraiton;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void display(){
        Toast.makeText(mContext, mMessage, mDuraiton).show();
    }
}
