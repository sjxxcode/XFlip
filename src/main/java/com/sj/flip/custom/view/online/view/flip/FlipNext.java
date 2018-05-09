package com.sj.flip.custom.view.online.view.flip;

import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.sj.flip.custom.view.online.IAction;
import com.sj.flip.custom.view.online.IDegree;

/**
 * Created by SJ on 2018/5/7.
 */

public class FlipNext implements IFlip{

    private static final FlipNext INSTANCE = new FlipNext();

    private FlipNext(){}

    public static FlipNext getInstance(){
        return INSTANCE;
    }

    @Override
    public void onFlip(Canvas canvas, Camera camera, IAction action, float degree, IFlipAction flipAction) {
        this.checkParams(canvas, camera, action, flipAction);

        this.flipNext(canvas, camera, degree, flipAction);
    }

    private void checkParams(Canvas canvas, Camera camera, IAction action, IFlipAction flipAction){
        if(canvas == null
                || camera == null
                || action == IAction.NONE
                || flipAction == null){
            throw new IllegalArgumentException("checkParams() params is error!");
        }
    }

    private Rect getNextUnRotaRect(int canvasW, int canvasH){
        return new Rect(0, canvasH >> 1, canvasW, canvasH);
    }

    private Rect getNextRotaRect(int canvasW, int canvasH){
        return new Rect(0, 0, canvasW, canvasH >> 1);
    }

    private void flipNext(Canvas canvas, Camera camera, float degree, IFlipAction flipAction){
        final int canvasW = canvas.getWidth();
        final int canvasH = canvas.getHeight();
        final int currentX = canvasW >> 1;
        final int currentY = canvasH >> 1;

        //================下半部分=================//
        canvas.save();
        canvas.clipRect(this.getNextUnRotaRect(canvasW, canvasH));
        //FIXME super.dispatchDraw(canvas);
        flipAction.doSome();
        canvas.restore();
        //================下半部分=================//

        //================上半部分=================//
        canvas.save();
        // 裁剪要处理的区域
        canvas.clipRect(this.getNextRotaRect(canvasW, canvasH));

        canvas.translate(currentX, currentY);
        camera.save();
        camera.rotateX(IDegree.DEGREE_180 + degree);
        camera.applyToCanvas(canvas);
        camera.restore();
        canvas.translate(-currentX, -currentY);
        //FIXME super.dispatchDraw(canvas);
        flipAction.doSome();
        canvas.restore();
        //================上半部分=================//
    }
}
