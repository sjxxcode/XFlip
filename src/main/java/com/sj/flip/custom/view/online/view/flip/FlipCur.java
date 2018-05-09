package com.sj.flip.custom.view.online.view.flip;

import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.sj.flip.custom.view.online.IAction;
import com.sj.flip.custom.view.online.IDegree;

/**
 * Created by SJ on 2018/5/7.
 */

public class FlipCur implements IFlip{

    private static final FlipCur INSTANCE = new FlipCur();

    private FlipCur(){}

    public static FlipCur getInstance(){
        return INSTANCE;
    }

    @Override
    public void onFlip(Canvas canvas, Camera camera, IAction action, float degree, IFlipAction flipAction) {
        this.checkParams(canvas, camera, action, flipAction);

        this.flipCur(canvas, camera, action, degree, flipAction);
    }

    private void checkParams(Canvas canvas, Camera camera, IAction action, IFlipAction flipAction){
        if(canvas == null
                || camera == null
                || action == IAction.NONE
                || flipAction == null){
            throw new IllegalArgumentException("checkParams() params is error!");
        }
    }

    private Rect getCurUnRotaRect(IAction action, int canvasW, int canvasH){
        Rect rect = new Rect();

        if(action == IAction.UP){
            rect.set(0, 0, canvasW, canvasH / 2);
        } else {
            rect.set(0, canvasH / 2, canvasW, canvasH);
        }

        return rect;
    }

    private Rect getCurRotaRect(float degree, int canvasW, int canvasH){
        Rect rect = null;

        if(degree <= IDegree.DEGREE_90){
            rect = new Rect(0, canvasH / 2, canvasW, canvasH);
        } else {
            rect = new Rect(0, 0, canvasW, canvasH / 2);
        }

        return rect;
    }

    private void flipCur(Canvas canvas, Camera camera, IAction action, float degree, IFlipAction flipAction){
        final int canvasW = canvas.getWidth();
        final int canvasH = canvas.getHeight();
        final int currentX = canvasW >> 1;
        final int currentY = canvasH >> 1;

        //================上半部分=================//
        canvas.save();
        canvas.clipRect(this.getCurUnRotaRect(action, canvasW, canvasH));
        //FIXME super.dispatchDraw(canvas);
        flipAction.doSome();
        canvas.restore();
        //================上半部分=================//

        //================下半部分=================//
        canvas.save();
        // 裁剪要处理的区域
        canvas.clipRect(this.getCurRotaRect(degree, canvasW, canvasH));

        canvas.translate(currentX, currentY);
        camera.save();

        float tempDegree = IDegree.DEGREE_0;
        if(action == IAction.UP){
            tempDegree = degree;
        } else {
            tempDegree = degree - IDegree.DEGREE_180;
        }
        camera.rotateX(tempDegree);

        camera.applyToCanvas(canvas);
        camera.restore();
        canvas.translate(-currentX, -currentY);
        //FIXME super.dispatchDraw(canvas);
        flipAction.doSome();
        canvas.restore();
    }
}
