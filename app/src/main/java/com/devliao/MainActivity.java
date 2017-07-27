package com.devliao;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.devliao.model.AssistantMenuBean;
import com.devliao.widget.AssistantLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AssistantLayout assistantLayout = (AssistantLayout) findViewById(R.id.assistant_layout);
        List<AssistantMenuBean> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            AssistantMenuBean bean = new AssistantMenuBean();
            bean.setDisplay(true);
            bean.setId(i);
            bean.setLogo("http://img4.imgtn.bdimg.com/it/u=3032418550,1205236134&fm=26&gp=0.jpg");
            bean.setName("控件" + i);
            bean.setUrl("https://www.baidu.com/");
            list.add(bean);
        }
        assistantLayout.updateAssistantInfo(list);

    }
}
