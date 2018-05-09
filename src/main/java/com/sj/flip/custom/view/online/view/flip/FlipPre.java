package com.sj.flip.custom.view.online.view.flip;

import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.sj.flip.custom.view.online.IAction;
import com.sj.flip.custom.view.online.IDegree;

/**
 * Created by SJ on 2018/5/7.
 */

public class FlipPre implements IFlip{

    private static final FlipPre INSTANCE = new FlipPre();

    private FlipPre(){}

    public static FlipPre getInstance(){
        return INSTANCE;
    }

    @Override
    public void onFlip(Canvas canvas, Camera camera, IAction action, float degree, IFlipAction flipAction) {
        this.checkParams(canvas, camera, action, flipAction);

        this.flipPre(canvas, camera, degree, flipAction);
    }

    private void checkParams(Canvas canvas, Camera camera, IAction action, IFlipAction flipAction){
        if(canvas == null
                || camera == null
                || action == IAction.NONE
                || flipAction == null){
            throw new IllegalArgumentException("checkParams() params is error!");
        }
    }

    private Rect getPreUnRotaRect(int canvasW, int canvasH){
        return new Rect(0, 0, canvasW, canvasH >> 1);
    }

    private Rect getPreRotaRect(float degree, int canvasW, int canvasH){
        Rect rect = null;

        if(degree <= IDegree.DEGREE_90){
            rect = new Rect(0, canvasH >> 1, canvasW, canvasH);
        } else {
            rect = new Rect(0, 0, canvasW, canvasH >> 1);
        }

        return rect;
    }

    private void flipPre(Canvas canvas, Camera camera, float degree, IFlipAction flipAction){
        final int canvasW = canvas.getWidth();
        final int canvasH = canvas.getHeight();
        final int currentX = canvasW >> 1;
        final int currentY = canvasH >> 1;

        //================下半部分=================//
        canvas.save();
        canvas.clipRect(this.getPreUnRotaRect(canvasW, canvasH));
        //FIXME super.dispatchDraw(canvas);
        flipAction.doSome();
        canvas.restore();
        //================下半部分=================//

        //================上半部分=================//
        canvas.save();
        // 裁剪要处理的区域
        canvas.clipRect(this.getPreRotaRect(degree, canvasW, canvasH));

        canvas.translate(currentX, currentY);
        camera.save();
        camera.rotateX(degree);
        camera.applyToCanvas(canvas);
        camera.restore();
        canvas.translate(-currentX, -currentY);
        //FIXME super.dispatchDraw(canvas);
        flipAction.doSome();
        canvas.restore();
        //================上半部分=================//
    }
}
