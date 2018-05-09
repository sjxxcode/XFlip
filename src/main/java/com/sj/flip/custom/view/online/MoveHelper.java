package com.sj.flip.custom.view.online;

import android.view.MotionEvent;

/**
 * Created by SJ on 2018/5/6.
 */

public class MoveHelper {

    private static final MoveHelper INSTANCE = new MoveHelper();

    private final int DEFAULT_DEGREE_RADIUS = 400;

    /** 从起始角度到终止角度最大移动距离*/
    private int mDegreeRadius;
    /** 手指id*/
    private int mPointerId;
    /** 手指按下的y*/
    private float mDownY;
    /** */
    private float mLastDegree;

    /** 在手指按下并滑动的一霎那,决定是要"翻下/上页"*/
    private IAction mAction = IAction.NONE;

    private MoveHelper(){
        this.mDegreeRadius = DEFAULT_DEGREE_RADIUS;
    }

    public static MoveHelper with(){
        return INSTANCE;
    }

    private void savePointId(MotionEvent ev) {
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        if (pointerId == this.mPointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;

            this.mPointerId = ev.getPointerId(newPointerIndex);
        }
    }

    public MoveHelper degreeRadius(int degreeRadius){
        this.mDegreeRadius = degreeRadius;

        return this;
    }

    /**
     * @param event MotionEvent
     * @return true:需要把事件继续传递, false:拦截
     */
    public Move touch(MotionEvent event){
        Move move = new Move();

        switch (event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                this.mPointerId = event.getPointerId(event.getActionIndex());
                this.mDownY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                int pointIndex = event.findPointerIndex(this.mPointerId);
                if(pointIndex < 0){
                    pointIndex = 0;
                }

                float moveY = event.getY(pointIndex);
                float moveDis = Math.abs(moveY - this.mDownY) * 0.8F;

                if(moveDis == 0){
                    move.dispatch = true;
                    break;
                }

                if(moveDis > this.mDegreeRadius * 2){
                    move.dispatch = false;
                    move.end = true;
                    break;
                }

                if(this.mAction == IAction.NONE){
                    this.mAction = moveY > this.mDownY ? IAction.DOWN : IAction.UP;
                }

                float degree = IDegree.DEGREE_0;
                if(this.mAction == IAction.UP){
                    degree = IDegree.DEGREE_180 * (moveDis / (this.mDegreeRadius * 2.0F));
                } else {
                    degree = IDegree.DEGREE_180 * (1F - moveDis / (this.mDegreeRadius * 2.0F));
                }

                this.mLastDegree = degree;

                move.degress = degree;
                move.dispatch = false;
                move.action = this.mAction;

                break;

            case MotionEvent.ACTION_POINTER_UP:
                this.savePointId(event);
                break;

            case MotionEvent.ACTION_UP:
                move.dispatch = false;
                move.end = true;
                move.degress = this.mLastDegree;
                move.action = this.mAction;

                this.reset();

                break;
        }

        return move;
    }

    private void reset(){
        this.mPointerId = 0;
        this.mDownY = 0;
        this.mAction = IAction.NONE;
        this.mLastDegree = IDegree.DEGREE_0;
    }

    public static class Move{
        boolean dispatch = true;
        boolean end = false;
        float degress = IDegree.DEGREE_0;
        IAction action = IAction.NONE;

        public boolean isDispatch() {
            return dispatch;
        }

        public boolean isEnd() {
            return end;
        }

        public float getDegress() {
            return degress;
        }

        public IAction getAction() {
            return action;
        }

        @Override
        public String toString() {
            return "Move{" +
                    "dispatch=" + dispatch +
                    ", end=" + end +
                    ", degress=" + degress +
                    ", action=" + action +
                    '}';
        }
    }
}
