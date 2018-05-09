package com.sj.flip.custom.view.online.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.sj.flip.custom.view.online.IAction;
import com.sj.flip.custom.view.online.IDegree;
import com.sj.flip.custom.view.online.IPage;
import com.sj.flip.custom.view.online.MoveHelper;
import com.sj.flip.custom.view.online.view.flip.FlipCur;
import com.sj.flip.custom.view.online.view.flip.FlipNext;
import com.sj.flip.custom.view.online.view.flip.FlipPre;
import com.sj.flip.custom.view.online.view.flip.IFlip;

/**
 * Created by SJ on 2018/4/26.
 */

public class FlipboardView extends FrameLayout {

    private final String TAG = "===" + this.getClass().getSimpleName();
    private final int DEFAULT_CUR_INDEX = -1;

    private int mCurViewIndex = DEFAULT_CUR_INDEX;
    private float mDegree = IDegree.DEGREE_0;
    /** */
    protected FlipboardViewProxy preOrNextView = null;
    /** up/down*/
    private IAction mAction = IAction.NONE;

    private BaseAdapter mAdapter;

    private boolean isFliping;

    public FlipboardView(@NonNull Context context) {
        super(context);
    }

    public FlipboardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FlipboardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public FlipboardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //
        MoveHelper.Move move = new MoveHelper.Move();

        if(!this.isFliping){
            move = MoveHelper.with().touch(ev);

            Log.e(TAG, move.toString());

            if(!move.isDispatch()){
                if(!move.isEnd()){ //移动
                    if(move.getAction() == IAction.UP){
                        this.flipUp(move.getDegress());
                    } else if(move.getAction() == IAction.DOWN){
                        this.flipDown(move.getDegress());
                    }
                } else { //动画结束
                    if(move.getAction() == IAction.UP){
                        this.endNextByAni(move.getDegress());
                    } else if(move.getAction() == IAction.DOWN){
                        this.endPreByAni(move.getDegress());
                    }
                }
            }
        }

        return move.isDispatch() ? super.dispatchTouchEvent(ev) : true;
    }

    private void bindView(){
        if(this.mAdapter != null){
            int count = this.mAdapter.getCount();

            for(int position = count - 1; position >= 0; position--){
                View child = this.mAdapter.getView(position, null, this);
                FlipboardViewProxy childRoot = new FlipboardViewProxy(this.getContext());
                childRoot.addView(child);

                this.addView(childRoot);
                //FIXME 设置Z轴层级
                childRoot.setTranslationZ((count - position) * 2);
            }

            //
            int childCount = this.getChildCount();
            for(int position = 0; position < childCount; position++){
                FlipboardViewProxy childRoot = (FlipboardViewProxy) this.getChildAt(position);

                if(position > 0){
                    childRoot.preView = (FlipboardViewProxy) this.getChildAt(position - 1);
                } else {
                    childRoot.preView = (FlipboardViewProxy) this.getChildAt(childCount - 1);
                }

                if(position < childCount - 1){
                    childRoot.nextView = (FlipboardViewProxy) this.getChildAt(position + 1);
                } else {
                    childRoot.nextView = (FlipboardViewProxy) this.getChildAt(0);
                }
            }

            //
            this.mCurViewIndex = childCount - 1;
        }
    }

    private void checkCurIndex(){
        if(this.mCurViewIndex > this.getChildCount() - 1
                || this.mCurViewIndex < 0){
            throw new IndexOutOfBoundsException("The 'mCurViewIndex' value is " + this.mCurViewIndex);
        }
    }

    private FlipboardViewProxy getCurView(){
        this.checkCurIndex();

        FlipboardViewProxy curView = (FlipboardViewProxy) this.getChildAt(this.mCurViewIndex);
        curView.setPage(IPage.CURRENT);
        curView.setFlip(FlipCur.getInstance());

        return curView;
    }

    private void initPreOrNextView(FlipboardViewProxy curView){
        FlipboardViewProxy tempView = null;
        IFlip tempFlip = null;
        IPage tempPage = IPage.NONE;

        if(this.preOrNextView == null && curView != null){
            if(this.mAction == IAction.UP){
                tempView = curView.preView;
                tempPage = IPage.NEXT;
                tempFlip = FlipNext.getInstance();

            } else if(this.mAction == IAction.DOWN){
                tempView = curView.nextView;
                tempPage = IPage.PRE;
                tempFlip = FlipPre.getInstance();
            }

            if(tempView != null && tempFlip != null && tempPage != IPage.NONE){
                tempView.setDegree(this.mDegree);
                tempView.setPage(tempPage);
                tempView.setFlip(tempFlip);

                this.preOrNextView = tempView;
            }
        }
    }

    private void resetCurIndex(){
        this.checkCurIndex();

        if(this.preOrNextView != null
                && this.preOrNextView.getPage() != IPage.NONE){
            if(this.preOrNextView.getPage() == IPage.NEXT){
                this.mCurViewIndex--;

                if(this.mCurViewIndex < 0){
                    this.mCurViewIndex = this.getChildCount() - 1;
                }
            } else {
                this.mCurViewIndex++;

                if(this.mCurViewIndex > this.getChildCount() - 1){
                    this.mCurViewIndex = 0;
                }
            }
        }
    }

    private void reset(){
        this.mDegree = IDegree.DEGREE_0;
        this.mAction = IAction.NONE;
        this.isFliping = false;
        this.preOrNextView = null;
    }

    public void up(float degree){
        this.mDegree = degree;
        this.mAction = IAction.UP;
    }

    public void down(float degree){
        this.mDegree = degree;
        this.mAction = IAction.DOWN;
    }

    private void flipUp(float degree){
        this.up(degree);

        FlipboardViewProxy curView = this.getCurView();
        this.initPreOrNextView(curView);

        if(curView != null && this.preOrNextView != null){
            curView.setAction(IAction.UP);
            curView.flip(this.mDegree);

            this.preOrNextView.setAction(IAction.UP);
            this.preOrNextView.flip(mDegree);
        }
    }

    private void flipDown(float degree){
        this.down(degree);

        FlipboardViewProxy curView = this.getCurView();
        this.initPreOrNextView(curView);

        if(curView != null && this.preOrNextView != null){
            curView.setAction(IAction.DOWN);
            curView.flip(this.mDegree);

            this.preOrNextView.setAction(IAction.DOWN);
            this.preOrNextView.flip(this.mDegree);
        }
    }

    public void setAdapter(BaseAdapter adapter){
        this.mAdapter = adapter;

        this.removeAllViews();
        this.bindView();
    }

    public void endNextByAni(float startDegree){
        if(this.isFliping){
            return;
        }

        this.startByAni(startDegree, IDegree.DEGREE_180, new FlipboardView.ICallBack() {
            @Override
            public void start() {}

            @Override
            public void refresh(float degree) {
                flipUp(degree);
            }

            @Override
            public void end() {
                resetCurIndex();
                reset();

                isFliping = false;
            }
        });
    }

    public void endPreByAni(float startDegree){
        if(this.isFliping){
            return;
        }

        this.startByAni(startDegree, IDegree.DEGREE_0, new FlipboardView.ICallBack() {
            @Override
            public void start() {}

            @Override
            public void refresh(float degree) {
                //test
                flipDown(degree);
            }

            @Override
            public void end() {
                resetCurIndex();
                reset();

                isFliping = false;
            }
        });
    }

    private void startByAni(float startDegree, float endDegree, final FlipboardView.ICallBack iCallBack) {
        this.isFliping = true;

        ValueAnimator animator = ValueAnimator.ofFloat(startDegree, endDegree);
        animator.setDuration(300);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float degree = (float) animation.getAnimatedValue();

                if(iCallBack != null){
                    iCallBack.refresh(degree);
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(iCallBack != null){
                    iCallBack.start();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(iCallBack != null){
                    iCallBack.end();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if(iCallBack != null){
                    iCallBack.end();
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private static interface ICallBack{
        void start();
        void refresh(float degree);
        void end();
    }
}
