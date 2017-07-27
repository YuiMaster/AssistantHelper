package com.devliao.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.devliao.R;
import com.devliao.utils.ScreenUtils;

import java.util.ArrayList;

/**
 * Main menu的基本宽高数据部分
 * <p>
 * Created by Jane on 2017/7/18.
 */

@SuppressLint("AppCompatCustomView")
public class BaseView extends ImageView implements View.OnTouchListener {
    //屏幕大小
    protected final int screenWidth;
    protected final int screenHeight;

    protected final int toolBarHeight;
    protected final int statusBarHeight;

    //背景view
    protected ImageView ivBackground;
    //底层背景与大小
    protected Drawable backgroundBg;
    protected final int backgroundWidth;      //200dp
    protected final int backgroundHeight;

    //主menu 背景与大小
    protected Drawable mainMenuBg;
    protected final int mainMenuWidth;   //dp
    protected final int mainMenuHeight;   //280/3dp

    //单个sub menu 背景与大小
    protected Drawable subMenuBg;
    protected final int subMenuWidth;     //50dp
    protected final int subMenuHeight;

    //subMenus
    protected ArrayList<View> subMenuList = new ArrayList<View>();

    public BaseView(Context context) {
        this(context, null);
    }

    public BaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        screenWidth = ScreenUtil.getScreenWidth(context);
        screenHeight = ScreenUtil.getContentHeight(context);

        toolBarHeight = ScreenUtils.getActionBarSize(getContext());
        statusBarHeight = ScreenUtils.getStatusBarHeight(getContext());

        //icon_content_n_little_helper
        mainMenuBg = getResources().getDrawable(R.mipmap.icon_content_n_li_ttle_helper);
        subMenuBg = getResources().getDrawable(R.mipmap.icon_content_n_sign);
        backgroundBg = getResources().getDrawable(R.mipmap.icon_content_little_helperbackground_map);

        mainMenuWidth = mainMenuBg.getIntrinsicWidth();
        mainMenuHeight = mainMenuBg.getIntrinsicHeight();

        final int subTextViewHeight = 13; //13dp

        subMenuHeight = ScreenUtil.dip2px(getContext(), 50) + ScreenUtil.dip2px(getContext(), subTextViewHeight);
        subMenuWidth = ScreenUtil.dip2px(getContext(), 50);
        backgroundWidth = backgroundBg.getIntrinsicWidth();
        backgroundHeight = backgroundBg.getIntrinsicHeight();
    }


    /**
     * 取得sub menu 的list
     *
     * @return
     */
    protected ArrayList<View> getSubMenuList() {
        return subMenuList;
    }

    /**
     * 取得所有的Menu
     *
     * @return
     */
    protected int getSubMenuSize() {
        return subMenuList.size();
    }


    protected ImageView getBgView() {
        return ivBackground;
    }

    protected Drawable convertBitmap(Bitmap bitmap) {
        return new BitmapDrawable(getResources(), bitmap);
    }


    //注册归属的subView
    private void registerSubView(View subView) {
        subView.setOnTouchListener(this);
        subMenuList.add(subView);
    }

    /**
     * 统一添加，映射AssistantMenuBeanList 的id。
     * 确认 AssistantMenuBeanList id与subMenuList id一致。
     *
     * @param viewList
     */
    public void registerSubViewList(ArrayList<View> viewList) {
        subMenuList.clear();
        for (View view : viewList) {
            registerSubView(view);
        }
    }

    //注册 background view
    public void registerBgView(ImageView iv) {
        ivBackground = iv;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
