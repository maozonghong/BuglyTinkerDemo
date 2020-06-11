package com.geespace.hotfix;

import android.util.Log;

/**
 * Created by maozonghong
 * on 2020/6/11
 */
public class BugClass {
    public String bug() {
        // 这段代码会报空指针异常
        String str = null;
//        Log.e("BugClass", "get String length:" + str.length());
        return "This is a bug fix class";
    }
}
