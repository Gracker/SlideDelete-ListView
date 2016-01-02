package com.performance.slidedeletelistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;

public class SlideDeleteListView extends ListView {
    private static final int SNAP_VELOCITY = 600;
    /**
     * ListView position
     */
    private int slidePosition;
    /**
     * x position when touch
     */
    private int downY;
    /**
     * y position when touch
     */
    private int downX;
    /**
     * screen width
     */
    private int screenWidth;
    /**
     * ListView item
     */
    private View itemView;
    /**
     * Scroller
     */
    private Scroller scroller;
    /**
     * VelocityTracker
     */
    private VelocityTracker velocityTracker;
    /**
     * is slide state,default is false
     */
    private boolean isSlide = false;
    /**
     * min slop width
     */
    private int mTouchSlop;
    /**
     * RemoveListener
     */
    private RemoveListener mRemoveListener;
    /**
     * RemoveDirection
     */
    private RemoveDirection removeDirection;
    private int mTotalItemCount;

    public SlideDeleteListView(Context context) {
        this(context, null);
    }


    public SlideDeleteListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideDeleteListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        screenWidth = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        scroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mTotalItemCount = getHeaderViewsCount() + getCount() + getFooterViewsCount();
    }

    /**
     * set slop interface
     *
     * @param removeListener
     */
    public void setRemoveListener(RemoveListener removeListener) {
        this.mRemoveListener = removeListener;
    }

    /**
     * dispatch touch event
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                addVelocityTracker(event);

                if (!scroller.isFinished()) {
                    return super.dispatchTouchEvent(event);
                }
                downX = (int) event.getX();
                downY = (int) event.getY();

                slidePosition = pointToPosition(downX, downY);

                if (slidePosition == AdapterView.INVALID_POSITION
                        || isHeaderOfFoot(slidePosition)) {//with header or footer
                    isSlide = false;
                    return super.dispatchTouchEvent(event);
                }

                // get touch item view
                itemView = getChildAt(slidePosition - getFirstVisiblePosition());
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (Math.abs(getScrollVelocity()) > SNAP_VELOCITY
                        || (Math.abs(event.getX() - downX) > mTouchSlop && Math
                        .abs(event.getY() - downY) < mTouchSlop)) {
                    isSlide = true;
                }
                break;
            }
            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isSlide || super.onInterceptTouchEvent(ev);
    }

    /**
     * scrollRight
     */
    private void scrollRight() {
        removeDirection = RemoveDirection.RIGHT;
        final int delta = (screenWidth + itemView.getScrollX());
        scroller.startScroll(itemView.getScrollX(), 0, -delta, 0,
                Math.abs(delta));
        postInvalidate();
    }

    /**
     * scrollLeft
     */
    private void scrollLeft() {
        removeDirection = RemoveDirection.LEFT;
        final int delta = (screenWidth - itemView.getScrollX());
        scroller.startScroll(itemView.getScrollX(), 0, delta, 0,
                Math.abs(delta));
        postInvalidate();
    }

    /**
     * scrollByDistanceX
     */
    private void scrollByDistanceX() {
        if (itemView.getScrollX() >= screenWidth / 3) {
            scrollLeft();
        } else if (itemView.getScrollX() <= -screenWidth / 3) {
            scrollRight();
        } else {
            itemView.scrollTo(0, 0);
        }

    }

    /**
     * onTouchEvent
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isSlide && slidePosition != AdapterView.INVALID_POSITION
                && !isHeaderOfFoot(slidePosition)) {
            addVelocityTracker(ev);
            final int action = ev.getAction();
            int x = (int) ev.getX();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    int deltaX = downX - x;
                    downX = x;

                    itemView.scrollBy(deltaX, 0);
                    break;
                case MotionEvent.ACTION_UP:
                    int velocityX = getScrollVelocity();
                    if (velocityX > SNAP_VELOCITY) {
                        scrollRight();
                    } else if (velocityX < -SNAP_VELOCITY) {
                        scrollLeft();
                    } else {
                        scrollByDistanceX();
                    }

                    recycleVelocityTracker();
                    isSlide = false;
                    break;
            }

            return true;
        }

        return super.onTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());

            postInvalidate();

            if (scroller.isFinished()) {
                if (mRemoveListener == null) {
                    throw new NullPointerException("RemoveListener is null, we should called setRemoveListener()");
                }

                mRemoveListener.removeItem(removeDirection, slidePosition);

                //reset the item position
                itemView.scrollTo(0, scroller.getCurrY());
            }
        }
    }

    /**
     * add VelocityTracker
     *
     * @param event
     */
    private void addVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }

        velocityTracker.addMovement(event);
    }

    /**
     * remove VelocityTracker
     */
    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    /**
     * get ScrollVelocity
     *
     * @return
     */
    private int getScrollVelocity() {
        velocityTracker.computeCurrentVelocity(1000);
        int velocity = (int) velocityTracker.getXVelocity();
        return velocity;
    }

    private boolean isHeaderOfFoot(int itemPosition) {
        int headerCount = getHeaderViewsCount();
        int footerCount = getFooterViewsCount();

        if (headerCount == 0 && footerCount == 0) {
            return false;
        }

        if (headerCount != 0) {
            if (itemPosition + 1 <= headerCount) {
                return true;
            }
        }

        if (footerCount != 0) {
            if (itemPosition + 1 - headerCount - getCount() > footerCount) {
                return true;
            }
        }

        return false;
    }

    // direction
    public enum RemoveDirection {
        RIGHT, LEFT
    }

    /**
     * remove the listener
     *
     * @author Gracker
     */
    public interface RemoveListener {
        void removeItem(RemoveDirection direction, int position);
    }

}

