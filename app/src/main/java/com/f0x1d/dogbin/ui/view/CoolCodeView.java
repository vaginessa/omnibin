package com.f0x1d.dogbin.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.utils.Utils;

public class CoolCodeView extends ViewGroup {

    private static final float MINIMUM_SCALE = 5f;
    private static final float MAXIMUM_SCALE = 30f;

    private TextView mCodeTextView;
    private TextView mLinesTextView;

    private float mScale = -1;
    private float mLastDistanceForScale = 0;
    private long mLastTimeScaled = 0;

    private final Coordinates mStartCoordinates = new Coordinates();
    private final Coordinates mPreviousMovementCoordinates = new Coordinates();
    private final Coordinates mCoordinates = new Coordinates();
    private final Coordinates mPreviousTouchCoordinates = new Coordinates();

    private float mLastInterceptedDistance = 0;
    private final Coordinates mPreviouslyInterceptedCoordinates = new Coordinates();

    private boolean mScrolling = false;
    private boolean mInMomentum = false;
    private long mTimeStartedScroll = 0;
    private ValueAnimator mValueAnimator;

    private float mTenDp = 0;
    private float mFiveDp = 0;

    public CoolCodeView(@NonNull Context context) {
        super(context);
        init();
    }

    public CoolCodeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CoolCodeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTenDp = Utils.dpToPx(10);
        mFiveDp = Utils.dpToPx(5);

        int textColor = Utils.getColorFromAttr(getContext(), R.attr.textColor);

        mCodeTextView = new TextView(getContext());
        mCodeTextView.setTypeface(Typeface.MONOSPACE);
        mCodeTextView.setTextColor(textColor);
        mCodeTextView.setTextIsSelectable(true);
        addView(mCodeTextView);

        mLinesTextView = new TextView(getContext());
        mLinesTextView.setTextColor(textColor);
        mLinesTextView.setGravity(Gravity.CENTER);
        mLinesTextView.setTypeface(Typeface.DEFAULT_BOLD);
        addView(mLinesTextView);
    }

    public void setText(String text) {
        mCodeTextView.setText(text);
        mCodeTextView.measure(0, 0);

        int lines = text.split("\\n").length;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < lines; i++) {
            stringBuilder.append(i + 1);
            if (i != lines - 1)
                stringBuilder.append("\n");
        }

        mLinesTextView.setText(stringBuilder.toString());
        mLinesTextView.measure(0, 0);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mLinesTextView.layout(
                (int) (mCoordinates.getX() + mFiveDp),
                (int) mCoordinates.getY(),
                (int) (mCoordinates.getX() + mLinesTextView.getMeasuredWidth() + mFiveDp),
                (int) (mCoordinates.getY() + mLinesTextView.getMeasuredHeight())
        );

        mCodeTextView.layout(
                (int) (mCoordinates.getX() + mTenDp + mLinesTextView.getMeasuredWidth()),
                (int) mCoordinates.getY(),
                (int) (mCoordinates.getX() + mCodeTextView.getMeasuredWidth() + mTenDp + mLinesTextView.getMeasuredWidth()),
                (int) (mCoordinates.getY() + mCodeTextView.getMeasuredHeight())
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // scaling
        if (event.getAction() == MotionEvent.ACTION_MOVE && event.getPointerCount() > 1) {
            float distance = distance(event.getX(), event.getY(), event.getX(1), event.getY(1));
            if (mLastDistanceForScale != 0) {
                if (mScale == -1) mScale = mCodeTextView.getTextSize() / getResources().getDisplayMetrics().scaledDensity;
                mScale *= distance / mLastDistanceForScale;

                if (mScale > MAXIMUM_SCALE) mScale = MAXIMUM_SCALE;
                else if (mScale < MINIMUM_SCALE) mScale = MINIMUM_SCALE;

                mCodeTextView.setTextSize(mScale);
                mCodeTextView.measure(0, 0);

                mLinesTextView.setTextSize(mScale);
                mLinesTextView.measure(0, 0);

                returnToBounds();

                requestLayout();
            }
            mLastDistanceForScale = distance;
            mLastTimeScaled = System.currentTimeMillis();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startScrolling();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            stopScrolling();
        }

        // moving
        if (event.getAction() == MotionEvent.ACTION_MOVE && mScrolling && System.currentTimeMillis() - mLastTimeScaled > 100) {
            float eventX = event.getX();
            float eventY = event.getY();
            if (mPreviousTouchCoordinates.getX() != 0) {
                float movement = 0;
                if (mPreviousTouchCoordinates.getX() > eventX) {
                    movement = mPreviousTouchCoordinates.getX() - eventX;
                    mCoordinates.decreaseXBy(movement);
                }
                if (mPreviousTouchCoordinates.getX() < eventX) {
                    movement = eventX - mPreviousTouchCoordinates.getX();
                    mCoordinates.increaseXBy(movement);
                }
                mPreviousMovementCoordinates.setX(movement);
            }
            if (mPreviousTouchCoordinates.getY() != 0) {
                float movement = 0;
                if (mPreviousTouchCoordinates.getY() > eventY) {
                    movement = mPreviousTouchCoordinates.getY() - eventY;
                    mCoordinates.decreaseYBy(movement);
                }
                if (mPreviousTouchCoordinates.getY() < eventY) {
                    movement = eventY - mPreviousTouchCoordinates.getY();
                    mCoordinates.increaseYBy(movement);
                }
                mPreviousMovementCoordinates.setY(movement);
            }

            returnToBounds();

            requestLayout();

            mPreviousTouchCoordinates.set(eventX, eventY);

            return true;
        }

        return super.onTouchEvent(event);
    }

    private void startMomentumAnimator(long timeTravelled, float xDistance, float yDistance) {
        float xSpeed = xDistance / timeTravelled;
        float ySpeed = yDistance / timeTravelled;

        if (mPreviousMovementCoordinates.getX() <= 0.3 || mPreviousMovementCoordinates.getY() <= 0.3) return; // random touches

        mInMomentum = true;

        mValueAnimator = new ValueAnimator();
        mValueAnimator.setIntValues(20, 0); // some random values that i like
        mValueAnimator.setDuration(2000);
        mValueAnimator.addUpdateListener(animation -> {
            int multiplier = (int) animation.getAnimatedValue();
            boolean xSuitsBounds = xSuitsBounds();
            boolean ySuitsBounds = ySuitsBounds();

            if (!xSuitsBounds && !ySuitsBounds) {
                mValueAnimator.cancel();
                returnToBounds();
            }

            if (xSuitsBounds) {
                mCoordinates.increaseXBy(xSpeed * multiplier);
            }
            if (ySuitsBounds) {
                mCoordinates.increaseYBy(ySpeed * multiplier);
            }

            returnToBounds();

            requestLayout();
        });
        mValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mInMomentum = false;
            }
        });
        mValueAnimator.start();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            stopScrolling();
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (mScrolling)
                return true;
            else {
                if (mPreviouslyInterceptedCoordinates.getX() != 0 && mPreviouslyInterceptedCoordinates.getY() != 0) {
                    float distance = distance(
                            event.getX(),
                            event.getY(),
                            mPreviouslyInterceptedCoordinates.getX(),
                            mPreviouslyInterceptedCoordinates.getY()
                    );

                    if (mLastInterceptedDistance != 0) {
                        if (Math.abs(mLastInterceptedDistance - distance) > ViewConfiguration.getTouchSlop()) {
                            startScrolling();
                            return true;
                        } else
                            return false;
                    }
                    mLastInterceptedDistance = distance;
                }

                mPreviouslyInterceptedCoordinates.set(event.getX(), event.getY());
            }
        }
        return false;
    }

    private void startScrolling() {
        if (mInMomentum)
            mValueAnimator.cancel();

        mScrolling = true;
        mStartCoordinates.set(mCoordinates);
        mTimeStartedScroll = System.currentTimeMillis();
    }

    private void stopScrolling() {
        if (mScrolling) {
            mScrolling = false;
            mPreviousTouchCoordinates.set(0, 0);

            startMomentumAnimator(
                    System.currentTimeMillis() - mTimeStartedScroll,
                    mCoordinates.getX() - mStartCoordinates.getX(),
                    mCoordinates.getY() - mStartCoordinates.getY()
            );
            mTimeStartedScroll = 0;
        }

        mLastDistanceForScale = 0;

        mLastInterceptedDistance = 0;
        mPreviouslyInterceptedCoordinates.set(0, 0);
    }

    private void returnToBounds() {
        int widthBound = (int) (getWidth() - mCodeTextView.getMeasuredWidth() - mLinesTextView.getMeasuredWidth() - mTenDp);
        if (mCoordinates.getX() < widthBound) {
            mCoordinates.setX(widthBound);
        }
        int heightBound = getHeight() - mCodeTextView.getMeasuredHeight();
        if (mCoordinates.getY() < heightBound) {
            mCoordinates.setY(heightBound);
        }

        if (mCoordinates.getX() > 0) {
            mCoordinates.setX(0);
        }
        if (mCoordinates.getY() > 0) {
            mCoordinates.setY(0);
        }
    }

    private boolean xSuitsBounds() {
        if (mCoordinates.getX() > 0) {
            return false;
        }
        return !(mCoordinates.getX() < getWidth() - mCodeTextView.getMeasuredWidth() - mLinesTextView.getMeasuredWidth() - mTenDp);
    }

    private boolean ySuitsBounds() {
        if (mCoordinates.getY() > 0) {
            return false;
        }
        return !(mCoordinates.getY() < getHeight() - mCodeTextView.getMeasuredHeight());
    }

    private float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    // in the nearest future
    public enum TextWrap {
        NO_WRAP, WRAP_SENTENCES
    }

    public static class Coordinates {
        private float mX;
        private float mY;

        public Coordinates() {
            mX = 0;
            mY = 0;
        }

        public Coordinates(float x, float y) {
            set(x, y);
        }

        public void set(float x, float y) {
            mX = x;
            mY = y;
        }

        public void set(Coordinates coordinates) {
            set(coordinates.getX(), coordinates.getY());
        }

        public float getX() {
            return mX;
        }

        public void setX(float x) {
            this.mX = x;
        }

        public float getY() {
            return mY;
        }

        public void setY(float y) {
            this.mY = y;
        }

        public void increaseXBy(float how) {
            mX += how;
        }

        public void decreaseXBy(float how) {
            mX -= how;
        }

        public void increaseYBy(float how) {
            mY += how;
        }

        public void decreaseYBy(float how) {
            mY -= how;
        }
    }
}
