package com.devliao.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Main menu的动画部分
 * <p>
 * Created by Jane on 2017/7/18.
 */

@SuppressLint("AppCompatCustomView")
public abstract class AnimationView extends BaseView {
    public final static String ANIM_RUNNING = "-1"; //动画播放中
    public final static String ANIM_END = "1";      //动画播放完毕
    public final static int ANIM_DURATION = 400;    //动画持续时长

    public AnimationView(Context context) {
        this(context, null);
    }

    public AnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * 带动画移动view
     *
     * @param view
     * @param fromXDelta
     * @param toXDelta
     * @param fromYDelta
     * @param toYDelta
     * @param durationMillis
     * @param delayMillis
     * @param startVisible
     * @param endVisible
     */
    protected void slideViewWithAnim(final View view, final float fromXDelta, final float toXDelta, final float fromYDelta, final float toYDelta,
                                     long durationMillis, long delayMillis,
                                     final boolean startVisible, final boolean endVisible) {
        //如果处在动画阶段则不允许再次运行动画
        if (view.getTag() != null && ANIM_RUNNING.equals(view.getTag().toString())) {
            return;
        }
        if (view.getVisibility() == View.GONE) {
            return;
        }

        TranslateAnimation animation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        animation.setDuration(durationMillis);
        animation.setStartOffset(delayMillis);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setClickable(false);
                if (startVisible) {
                    view.setVisibility(View.VISIBLE);
                } else {
                    view.setVisibility(View.INVISIBLE);
                }
                view.setTag(ANIM_RUNNING);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
                int left = view.getLeft() + (int) (toXDelta - fromXDelta);
                int top = view.getTop() + (int) (toYDelta - fromYDelta);
                int width = view.getWidth();
                int height = view.getHeight();
                //重新设置位置
                view.layout(left, top, left + width, top + height);
                if (endVisible) {
                    view.setClickable(true);
                    view.setVisibility(View.VISIBLE);
                    showView(ivBackground);
                } else {
                    view.setVisibility(View.INVISIBLE);
                    hideView(ivBackground);
                }
                view.setTag(ANIM_END);
            }
        });
        if (endVisible) {
            view.startAnimation(animation);
        } else {
            //如果关闭则加渐变效果
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.setDuration(durationMillis);
            animationSet.setStartOffset(delayMillis);
            animationSet.addAnimation(animation);

            AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
            animationSet.addAnimation(alphaAnimation);
            view.startAnimation(animationSet);
        }
    }

    /**
     * 将view设置为gone，会影响其他布局坐标变动。
     * 目前使用规避的方式：设置view为invisible，避免影响下层控件的操作，将布局移除显示界面之外。
     * layout不能立即生效，通过大小缩放触发。
     * 调整大小后，才会触发onlayout，因此先设置为1dp，在显示时再调整到需要的大小（以背景图允许的大小为标准）
     * <p>
     * 注意：requestLayout()方法 ：会导致调用measure()过程 和 layout()过程 。
     * 说明：只是对View树重新布局layout过程包括measure()和layout()过程，不会调用draw()过程，但不会重新绘制
     *
     * @param view
     */
    protected void hideView(final View view) {
        if (view == null) {
            return;
        }
        if (view.getVisibility() == View.VISIBLE) {
            //getIntrinsicWidth 像素宽度，类似dp，*1.5
            final int width = view.getBackground().getIntrinsicWidth();
            final int height = view.getBackground().getIntrinsicHeight();
            //重新设置位置或大小
            view.layout(2000, 2000, 2000 + width, 2000 + height);
            view.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 根据基准view，获取基准view中心点，并以此移动view
     *
     * @param view
     */
    protected void showView(final View view) {
        if (view == null) {
            return;
        }
        //getIntrinsicWidth 像素宽度，类似dp，*1.5
        if (view.getVisibility() == View.INVISIBLE) {
            final int width = view.getBackground().getIntrinsicWidth();
            final int height = view.getBackground().getIntrinsicHeight();
            final int baseLeft = getLeft() + getWidth() / 2 - width / 2;
            final int baseTop = getTop() + getHeight() / 2 - height / 2;

            //重新设置位置
            view.layout(baseLeft, baseTop, baseLeft + width, baseTop + height);
            view.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 合拢
     */
    protected void collapseSubViews() {
        int size = getSubMenuSize();
        if (size == 0) {
            return;
        }

        final ArrayList<View> subMenuList = getSubMenuList();
        final ImageView ivBg = getBgView();
        int buttonLeft = getLeft();
        int buttonTop = getTop();

        showRotateAnimation(this, 0, 0);
        for (View subMenu : subMenuList) {
            final int offset = getWidth() - subMenu.getWidth();
            int subMenuLeft = subMenu.getLeft();
            int subMenuTop = subMenu.getTop();
            slideViewWithAnim(subMenu, 0, buttonLeft - subMenuLeft + offset / 2, 0, buttonTop - subMenuTop + offset / 2, 500, 0, true, false);
        }
    }

    /**
     * 展开
     *
     * @param
     */
    protected void expandSubViews() {
        int size = getSubMenuSize();
        if (size == 0) {
            return;
        }

        final ArrayList<View> subMenuList = getSubMenuList();
        int radius = ScreenUtil.dip2px(getContext(), 70);

        //可拖拽 展开 subViews
        showRotateAnimation(this, 0, 0);
        double angle = 360.0 / 4;
        int randomDegree = 45;      //起始角度，影响各控件摆放位置
        for (int i = 0; i < size; i++) {
            View subMenu = subMenuList.get(i);
            slideViewWithAnim(subMenu, 0, radius * (float) Math.cos(Math.toRadians(randomDegree + angle * i)),
                    0, radius * (float) Math.sin(Math.toRadians(randomDegree + angle * i)), ANIM_DURATION, 0, true, true);
        }
    }

    /**
     * 移动Buttons展开或者关闭
     */
    protected void slideSubViewsWithAnim() {
        //当前视图可见
        if (isDraftable()) {
            //展开 subViews
            expandSubViews();
        } else {
            //关闭 subViews
            collapseSubViews();
        }
    }

    /**
     * 旋转的动画
     *
     * @param mView        需要选择的View
     * @param startDegress 初始的角度【从这个角度开始】
     * @param degrees      当前需要旋转的角度【转到这个角度来】
     */
    protected void showRotateAnimation(View mView, int startDegress, int degrees) {
        mView.clearAnimation();
        float centerX = mView.getWidth() / 2.0f;
        float centerY = mView.getHeight() / 2.0f;
        //这个是设置需要旋转的角度（也是初始化），我设置的是当前需要旋转的角度
        RotateAnimation rotateAnimation = new RotateAnimation(startDegress, degrees, centerX, centerY);//centerX和centerY是旋转View时候的锚点
        //这个是设置动画时间的
        rotateAnimation.setDuration(ANIM_DURATION);
        rotateAnimation.setInterpolator(new AccelerateInterpolator());
        //动画执行完毕后是否停在结束时的角度上
        rotateAnimation.setFillAfter(true);
        //启动动画
        mView.startAnimation(rotateAnimation);
    }


    /**
     * 是否可拖拽  一旦展开则不允许拖拽
     *
     * @return
     */
    protected boolean isDraftable() {
        for (View subMenu : subMenuList) {
            if (subMenu.getVisibility() == View.VISIBLE) {
                return false;
            }
        }
        return true;
    }
}
