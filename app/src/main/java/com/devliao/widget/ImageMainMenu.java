package com.devliao.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.devliao.R;
import com.devliao.model.AssistantMenuBean;

import java.util.ArrayList;
import java.util.List;


/**
 * 小助手 main menu
 * touch部分
 */
@SuppressLint("AppCompatCustomView")
public class ImageMainMenu extends AnimationView {
    protected int lastX, lastY;           //记录上一个取样点的距离，以此取得移动距离
    protected int originX, originY;       //记录按下与松开的距离，判定为移动还是点击
    //sub menu数据
    private List<AssistantMenuBean> AssistantMenuBeanList = new ArrayList<AssistantMenuBean>();

    //贴边效果
    private boolean isLeft = false; //是否是左半屏
    private int scrollTop;        //保存顶部，底部界限
    private int scrollBottom;

    public ImageMainMenu(Context context) {
        this(context, null);
    }

    public ImageMainMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageMainMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }


    /**
     * 根据基准view的偏移量，移动view,目前只适用MainMenu，subMenu使用slideSubViewsToBaseView移动
     *
     * @param baseView
     * @param dx       相对左上角，基准view在x轴上挪动了多少像素点
     * @param dy       相对左上角，基准view在y轴上挪动了多少像素点
     */
    //当被拖拽后menu改变位置
    protected void slideMainView(View baseView, View view, int dx, int dy) {
        //以view为基准，移动时，偏移量是baseView和view 两个宽度差的一半。
        int offset = (baseView.getWidth() - view.getWidth()) / 2;       //同一个view时，offset=0
        int l = view.getLeft() + dx;
        int b = view.getBottom() + dy;
        int r = view.getRight() + dx;
        int t = view.getTop() + dy;
        // 是否挪到了最左边，基准view默认是MainMenu
        if (l < offset) {
            l = offset;
            r = l + view.getWidth();
        }
        if (t < statusBarHeight) {
            t = statusBarHeight;
            b = t + view.getHeight();
        }
        if (r > (screenWidth - offset)) {
            r = (screenWidth - offset);
            l = r - view.getWidth();
        }
        if (b > (screenHeight - toolBarHeight)) {
            b = (screenHeight - toolBarHeight);
            t = b - view.getHeight();
        }
        //移动该view
        view.layout(l, t, r, b);
    }

    /**
     * 以baseView为基准，挪动其他view到这个view的中心点
     */
    protected void slideSubViewsToBaseView() {
        for (View subMenu : subMenuList) {
            int offsetX = (getWidth() - subMenuWidth) / 2;
            int offsetY = (getHeight() - subMenuHeight) / 2;
            subMenu.layout(getLeft() + offsetX, getTop() + offsetY, getLeft() + offsetX + subMenuWidth, getTop() + offsetY + subMenuHeight);
        }
        this.postInvalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //展开状态，不允许挪动view
        if (!isDraftable()) {
            return false;
        }
        ViewGroup viewGroup = (ViewGroup) v.getParent();

        int ea = event.getAction();
        switch (ea) {
            case MotionEvent.ACTION_DOWN:
                //取得相对屏幕左上角的x y轴坐标（像素点）
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                originX = lastX;
                originY = lastY;
                break;
            case MotionEvent.ACTION_MOVE:
                viewGroup.requestDisallowInterceptTouchEvent(true);
                //与上次挪动或按下的点做比对，获取挪动的像素点
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                slideMainView(this, v, dx, dy);
                slideSubViewsToBaseView();
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                v.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                viewGroup .requestDisallowInterceptTouchEvent(false);
                int distance = (int) event.getRawX() - originX + (int) event.getRawY() - originY;
                if (Math.abs(distance) < 10) {
                    //当变化太小的时候什么都不做 OnClick执行
                    slideSubViewsToBaseView();
                    //弹出动态Button
                    slideSubViewsWithAnim();
                } else {
                    startScroll(this);
                    return true;
                }
                break;
        }
        return true;
    }

    protected void startScroll(View view) {
        //播放动画时，getLeft数据是变动的，以此次数据为准
        final int left = view.getLeft();
        //总的需要移动距离
        final int distance;

        if ((left + getWidth() / 2) <= (screenWidth / 2)) {
            isLeft = true;
            distance = left + getWidth() / 2;
        } else {
            isLeft = false;
            distance = screenWidth - left - getWidth() / 2;
        }

        //避免越界，不显示
        scrollTop = getTop();
        scrollBottom = getBottom();
        if (scrollTop < backgroundHeight / 2) {
            scrollTop = backgroundHeight / 2;
            scrollBottom = scrollTop + mainMenuHeight;
        } else if (scrollBottom > (screenHeight - toolBarHeight - backgroundHeight / 2)) {
            scrollBottom = (screenHeight - toolBarHeight - backgroundHeight / 2);
            scrollTop = (scrollBottom - mainMenuHeight);
        }

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, distance).setDuration(ANIM_DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float fractionDistanceX = 0; //移动距离，随变化百分百变化。
                if (isLeft) {
                    //从left 递减到 - width/2
                    fractionDistanceX = distance * (1 - animation.getAnimatedFraction()) - getWidth() / 2;
                } else {
                    //从left 递增 screenWidth - width/2
                    fractionDistanceX = left + distance * (animation.getAnimatedFraction());
                }
                layout((int) fractionDistanceX, scrollTop, (int) fractionDistanceX + getWidth(), scrollBottom);
                if (animation.getAnimatedFraction() == 1) {
                    slideSubViewsToBaseView();
                }
            }
        });
        valueAnimator.start();
    }

    /**
     * 更新小助手数据，并显示
     * 游客身份，没有数据不显示
     *
     * @param data
     */
    public void updateAssistantMenuBean(List<AssistantMenuBean> data) {
        AssistantMenuBeanList.clear();
        AssistantMenuBeanList.addAll(data);
        for (int i = 0; i < subMenuList.size(); i++) {
            if (AssistantMenuBeanList.size() >= i) {
                AssistantMenuBean bean = AssistantMenuBeanList.get(i);
                View view = subMenuList.get(i);
                final ImageButton ib = (ImageButton) view.findViewById(R.id.ib_submenu);
                final TextView tv = (TextView) view.findViewById(R.id.tv_submenu);
                if (null != tv) {
                    tv.setText(bean.getName());
                    tv.setTextColor(getResources().getColor(R.color.color_cyan));
                }
                if (null != bean.getLogo()) {
                    Glide.with(getContext()) // could be an issue!
                            .load(bean.getLogo())
                            .asBitmap()   //强制转换Bitmap
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                                    //这里我们拿到回掉回来的bitmap，可以加载到我们想使用到的地方
                                    Log.i("test", "success");
                                    ib.setBackground(convertBitmap(bitmap));
                                }
                            });
                }

                //隐藏不应该显示的项
                if (!bean.isDisplay()) {
                    // GONE则不会再次显示。 对应 slideViewWithAnim
                    subMenuList.get(i).setVisibility(View.GONE);
                }
            }
        }
    }
}