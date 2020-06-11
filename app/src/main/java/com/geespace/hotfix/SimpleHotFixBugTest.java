package com.geespace.hotfix;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by maozonghong
 * on 2020/6/10
 */
public class SimpleHotFixBugTest {
//    public void getBug(Context context) {
//        int i = 10;
//        int a = 0;
//        Toast.makeText(context, "Hello,I am CSDN_LQR:" + i / a, Toast.LENGTH_SHORT).show();
//    }


    public void getBug(Context context) {
        int i = 10;
        int a = 1;
        Toast.makeText(context, "Hello,bug  has fixed:" + i / a, Toast.LENGTH_SHORT).show();
    }
}
