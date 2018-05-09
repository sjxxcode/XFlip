package com.sj.flip.custom.view.online.view;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.sj.flip.custom.view.online.IAction;
import com.sj.flip.custom.view.online.IDegree;
import com.sj.flip.custom.view.online.IPage;
import com.sj.flip.custom.view.online.view.flip.IFlip;


/**
 * Created by SJ on 2018/4/25.
 */

public class FlipboardViewProxy extends LinearLayout{

    /**
     * 当前View是哪一页
     */
    private IPage mPage = IPage.NONE;
    /**
     * up/down
     */
    private IAction mAction = IAction.NONE;

    private Camera mCamera;

    private float mDegree = IDegree.DEGREE_0;

    /** 前一页*/
    protected FlipboardViewProxy preView = null;
    /** 后一页*/
    protected FlipboardViewProxy nextView = null;

    private IFlip mFlip;

    public FlipboardViewProxy(Context context) {
        super(context);

        this.init();
    }

    public FlipboardViewProxy(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.init();
    }

    public FlipboardViewProxy(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.init();
    }

    public FlipboardViewProxy(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        this.init();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if(this.checkState()){
            super.dispatchDraw(canvas);
            return;
        }

        this.flip(canvas);
    }

    private void init(){
        this.mCamera = new Camera();
        this.mCamera.setLocation(this.mCamera.getLocationX(),
                                 this.mCamera.getLocationY(),
                              this.mCamera.getLocationZ() - 50);
    }

    private boolean checkState(){
        boolean flag = false;

        if(this.mAction == IAction.NONE){
            flag = true;
        }

        return flag;
    }

    private boolean checkTranslateZ(int translateZ){
        return this.getTranslationZ() != translateZ ? true : false;
    }

    private void drawChild(Canvas canvas){
        super.dispatchDraw(canvas);
    }

    private void flip(final Canvas canvas){
        if(this.mFlip != null){
            if(this.mPage == IPage.PRE){
                this.flipPre();
            } else if(this.mPage == IPage.NEXT){
                this.flipNext();
            }

            this.mFlip.onFlip(canvas, this.mCamera, this.mAction, this.mDegree, new IFlip.IFlipAction() {
                @Override
                public void doSome() {
                    drawChild(canvas);
                }
            });
        }
    }

    private void flipNext() {
        int tempZ = 0;
        if(this.mDegree >= IDegree.DEGREE_90 && this.mDegree <= IDegree.DEGREE_180){
            tempZ = (int) this.nextView.getTranslationZ() + 1;
        } else {
            tempZ = (int) this.getTranslationZ();
        }
        if(this.checkTranslateZ(tempZ)){
            this.setTranslationZ(tempZ);
        }
    }

    private void flipPre() {
        int tempZ = 0;
        if(this.mDegree <= IDegree.DEGREE_90 && this.mDegree >= IDegree.DEGREE_0){
            tempZ = (int) this.preView.getTranslationZ() + 1;
        } else {
            tempZ = (int) this.getTranslationZ();
        }
        if(this.checkTranslateZ(tempZ)){
            this.setTranslationZ(tempZ);
        }
    }

    protected void flip(float degree){
        this.mDegree = degree;

        this.invalidate();
    }

    public void setDegree(float degree) {
        this.mDegree = degree;
    }

    public void setPage(IPage page) {
        this.mPage = page;
    }

    protected void setAction(IAction action){
        this.mAction = action;
    }

    public void setFlip(IFlip flip) {
        this.mFlip = flip;
    }

    public IPage getPage() {
        return mPage;
    }
}


