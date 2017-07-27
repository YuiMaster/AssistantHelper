package com.devliao.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.devliao.R;
import com.devliao.model.AssistantMenuBean;

import java.util.ArrayList;
import java.util.List;


/**
 * 小助手
 * <p>
 * <p>
 * Created by liao on 17-7-13.
 */

public class AssistantLayout extends RelativeLayout implements View.OnClickListener {
    //    protected H5ActionCommandImp h5ActionCommandImp;
    //小助手 main menu
    public ImageMainMenu mainMenu;
    //小助手 subMenus
    private ArrayList<View> subMenuList = new ArrayList<View>();

    private ArrayList<AssistantMenuBean> assistantMenuList = new ArrayList<>();

    public AssistantLayout(Context context) {
        this(context, null);
    }

    public AssistantLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AssistantLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.include_assistant_view, null);
        addView(layout);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initViews();
        setOnClickListener(this);
    }

    public void initViews() {
        mainMenu = (ImageMainMenu) findViewById(R.id.fdb_main_menu);
        View subMenu1 = findViewById(R.id.ll_submenu1);
        View subMenu2 = findViewById(R.id.ll_submenu2);
        View subMenu3 = findViewById(R.id.ll_submenu3);
        View subMenu4 = findViewById(R.id.ll_submenu4);
        ImageView ivBg = (ImageView) findViewById(R.id.iv_bg);
        subMenuList.clear();
        subMenuList.add(subMenu1);
        subMenuList.add(subMenu2);
        subMenuList.add(subMenu3);
        subMenuList.add(subMenu4);

        mainMenu.registerSubViewList(subMenuList);
        mainMenu.registerBgView(ivBg);
    }

    /**
     * 更新数据
     * 注意：小可爱4个sub menu，为2个时，重复添加。
     *
     * @param data
     */
    public void updateAssistantInfo(List<AssistantMenuBean> data) {
        if (data == null)
            return;
        if (data.size() == 0) {
            return;
        }

        assistantMenuList.clear();
        addAssistantInfo(data);
        //默认小助手4个，2个时，重复添加一下到4个
        if (assistantMenuList.size() == 2) {
            addAssistantInfo(data);
        }

        if (mainMenu != null) {
            mainMenu.setVisibility(View.VISIBLE);
        }
        mainMenu.updateAssistantMenuBean(assistantMenuList);
    }

    /**
     * 添加属性display = true的数据到 assistantMenuList
     * 清空数据需要额外调用clear
     *
     * @param data
     */
    private void addAssistantInfo(List<AssistantMenuBean> data) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).isDisplay()) {
                assistantMenuList.add(data.get(i));
            }
        }
    }

    /**
     * 设置监听
     *
     * @param listener
     */
    public void setOnClickListener(OnClickListener listener) {
        mainMenu.setOnClickListener(listener);
        for (View v : subMenuList) {
            v.setOnClickListener(listener);
        }
    }

    /**
     * 收缩所有submenu
     */
    public void collapseSubViews() {
        if (mainMenu != null) {
            mainMenu.collapseSubViews();
        }
    }

    @Override
    public void onClick(View view) {
        for (int i = 0; i < subMenuList.size(); i++) {
            if (view.equals(subMenuList.get(i))) {
                Toast.makeText(getContext(), "click " + i, Toast.LENGTH_SHORT).show();
            }
        }
        //弹出动态Button
        mainMenu.collapseSubViews();
    }
}
