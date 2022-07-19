package com.f0x1d.dogbin.ui.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.f0x1d.dogbin.R;
import com.f0x1d.dogbin.utils.ResourcesUtils;

public class CoolCodeView extends ViewGroup {

    private static final float MINIMUM_SCALE = 5f;
    private static final float MAXIMUM_SCALE = 30f;

    private ViewConfiguration mViewConfiguration;

    private TextView mCodeTextView;
    private TextView mLinesTextView;

    private float mScale = -1;
    private float mLastDistanceForScale = 0;
    private long mLastTimeScaled = 0;

    private final Coordinates mStartCoordinates = new Coordinates();
    private final Coordinates mPreviousMovementData = new Coordinates();
    private final Coordinates mCoordinates = new Coordinates();
    private final Coordinates mPreviousTouchCoordinates = new Coordinates();

    private float mLastInterceptedDistance = 0;
    private final Coordinates mPreviouslyInterceptedCoordinates = new Coordinates();

    private boolean mScrolling = false;
    private boolean mInMomentum = false;
    private long mTimeStartedScroll = 0;
    private long mTimeStartedMomentum = 0;
    private ValueAnimator mValueAnimator;

    private float mTenDp = 0;
    private float mFiveDp = 0;

    private boolean mWrapText = false;

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
        mViewConfiguration = ViewConfiguration.get(getContext());

        mTenDp = ResourcesUtils.dpToPx(10);
        mFiveDp = mTenDp / 2;

        int textColor = ResourcesUtils.getColorFromAttr(getContext(), R.attr.colorOnSurface);

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

    public void setWrapText(boolean wrapText) {
        this.mWrapText = wrapText;
        mLinesTextView.setVisibility(wrapText ? View.INVISIBLE : View.VISIBLE);

        requestLayout();
        returnToBounds();
    }

    public void setText(String text) {
        mCodeTextView.setText(text);

        int lines = text.split("\\n").length;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < lines; i++) {
            stringBuilder.append(i + 1);
            if (i != lines - 1)
                stringBuilder.append("\n");
        }

        mLinesTextView.setText(stringBuilder.toString());
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mCodeTextView.measure(mWrapText ? MeasureSpec.makeMeasureSpec((int) (getWidth() - mTenDp), MeasureSpec.EXACTLY) : 0, 0);
        mLinesTextView.measure(0, 0);

        if (!mWrapText) {
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
        } else {
            mCodeTextView.layout(
                    (int) (mCoordinates.getX() + mTenDp),
                    (int) mCoordinates.getY(),
                    (int) (mCoordinates.getX() + mCodeTextView.getMeasuredWidth() + mTenDp),
                    (int) (mCoordinates.getY() + mCodeTextView.getMeasuredHeight())
            );
        }
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
                mCodeTextView.measure(mWrapText ? MeasureSpec.makeMeasureSpec((int) (getWidth() - mTenDp), MeasureSpec.EXACTLY) : 0, 0);

                mLinesTextView.setTextSize(mScale);
                mLinesTextView.measure(0, 0);

                returnToBounds();

                requestLayout();
            }
            mLastDistanceForScale = distance;
            mLastTimeScaled = System.currentTimeMillis();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            stopScrolling();
            return true;
        }

        // moving
        if (event.getAction() == MotionEvent.ACTION_MOVE && mScrolling && System.currentTimeMillis() - mLastTimeScaled > 100) {
            float eventX = event.getX();
            float eventY = event.getY();

            float xMovement = 0;
            if (mPreviousTouchCoordinates.getX() > eventX) {
                xMovement = mPreviousTouchCoordinates.getX() - eventX;
                mCoordinates.decreaseXBy(xMovement);
            }
            if (mPreviousTouchCoordinates.getX() < eventX) {
                xMovement = eventX - mPreviousTouchCoordinates.getX();
                mCoordinates.increaseXBy(xMovement);
            }
            mPreviousMovementData.setX(xMovement);

            float yMovement = 0;
            if (mPreviousTouchCoordinates.getY() > eventY) {
                yMovement = mPreviousTouchCoordinates.getY() - eventY;
                mCoordinates.decreaseYBy(yMovement);
            }
            if (mPreviousTouchCoordinates.getY() < eventY) {
                yMovement = eventY - mPreviousTouchCoordinates.getY();
                mCoordinates.increaseYBy(yMovement);
            }
            mPreviousMovementData.setY(yMovement);

            mPreviousTouchCoordinates.set(eventX, eventY);

            returnToBounds();

            requestLayout();

            return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (event.getX() > mCodeTextView.getMeasuredWidth() || event.getY() > mCodeTextView.getMeasuredHeight()) {
            if (!mScrolling) startScrolling(event);
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mPreviouslyInterceptedCoordinates.set(event.getX(), event.getY());

            if (mInMomentum) {
                long timeDiff = System.currentTimeMillis() - mTimeStartedMomentum;

                if (timeDiff > 200) {
                    mValueAnimator.cancel();
                    return false;
                } else {
                    startScrolling(event);
                    return true;
                }
            }
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (mScrolling)
                return true;
            else {
                float distance = distance(
                        event.getX(),
                        event.getY(),
                        mPreviouslyInterceptedCoordinates.getX(),
                        mPreviouslyInterceptedCoordinates.getY()
                );

                if (mLastInterceptedDistance != 0) {
                    if (Math.abs(mLastInterceptedDistance - distance) > mViewConfiguration.getScaledTouchSlop()) {
                        startScrolling(event);
                        return true;
                    } else
                        return false;
                }
                mLastInterceptedDistance = distance;

                mPreviouslyInterceptedCoordinates.set(event.getX(), event.getY());
            }
        }
        return false;
    }

    private void startMomentumAnimator(long timeTravelled, float xDistance, float yDistance) {
        float xSpeed = xDistance / (timeTravelled == 0 ? 1 : timeTravelled) / 1.3f;
        float ySpeed = yDistance / (timeTravelled == 0 ? 1 : timeTravelled);

        if (mPreviousMovementData.getX() <= 1.3 && mPreviousMovementData.getY() <= 1.3) return; // like random touches

        mInMomentum = true;

        float maxSpeed = Math.max(Math.abs(xSpeed), Math.abs(ySpeed));

        mValueAnimator = new ValueAnimator();
        mValueAnimator.setFloatValues(maxSpeed * 5f, 1f);
        mValueAnimator.setDuration((long) (maxSpeed * 500L));
        mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mValueAnimator.addUpdateListener(animation -> {
            float multiplier = (float) animation.getAnimatedValue();

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
                mTimeStartedMomentum = 0;
                mInMomentum = false;
            }
        });

        mTimeStartedMomentum = System.currentTimeMillis();
        mValueAnimator.start();
    }

    private void startScrolling(MotionEvent event) {
        if (mInMomentum)
            mValueAnimator.cancel();

        mScrolling = true;

        mTimeStartedScroll = System.currentTimeMillis();

        mStartCoordinates.set(mCoordinates);
        mPreviousTouchCoordinates.set(event.getX(), event.getY());
    }

    private void stopScrolling() {
        if (mScrolling) {
            mScrolling = false;

            startMomentumAnimator(
                    System.currentTimeMillis() - mTimeStartedScroll,
                    mCoordinates.getX() - mStartCoordinates.getX(),
                    mCoordinates.getY() - mStartCoordinates.getY()
            );
            mTimeStartedScroll = 0;

            mLastDistanceForScale = 0;
            mLastInterceptedDistance = 0;
        }
    }

    private void returnToBounds() {
        int widthBound = mWrapText ? (int) (getWidth() - mCodeTextView.getMeasuredWidth() - mTenDp) :
                (int) (getWidth() - mCodeTextView.getMeasuredWidth() - mLinesTextView.getMeasuredWidth() - mTenDp);
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
        int widthBound = mWrapText ? (int) (getWidth() - mCodeTextView.getMeasuredWidth() - mTenDp) :
                (int) (getWidth() - mCodeTextView.getMeasuredWidth() - mLinesTextView.getMeasuredWidth() - mTenDp);
        return !(mCoordinates.getX() < widthBound);
    }

    private boolean ySuitsBounds() {
        if (mCoordinates.getY() > 0) {
            return false;
        }
        return !(mCoordinates.getY() < getHeight() - mCodeTextView.getMeasuredHeight());
    }

    private float distance(double x1, double y1, double x2, double y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public static class Coordinates {
        private float mX;
        private float mY;

        public Coordinates() {
            set(0, 0);
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
