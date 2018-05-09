package com.sj.flip.custom.view.online.view.flip;

import android.graphics.Camera;
import android.graphics.Canvas;

import com.sj.flip.custom.view.online.IAction;

/**
 * Created by SJ on 2018/5/7.
 */

public interface IFlip {

    void onFlip(Canvas canvas, Camera camera, IAction action, float degree, IFlipAction flipAction);

    interface IFlipAction{
        void doSome();
    }
}
