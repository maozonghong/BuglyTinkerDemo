package com.geespace.hotfix;

/**
 * Created by maozonghong
 * on 2020/6/11
 */
public class LoadBugClass {
    /**
     * 获取bug字符串.
     *
     * @return 返回bug字符串
     */
    public static String getBugString() {
        BugClass bugClass = new BugClass();
        return bugClass.bug();
    }
}
