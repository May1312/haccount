package com.fnjz.commonbean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 小程序服务通知 封装类
 * Created by yhang on 2018/11/28.
 */
public class WXAppletMessageBean implements Serializable {

    private Map<String,String> keyword1 = new HashMap<>();

    private Map<String,String> keyword2 = new HashMap<>();

    private Map<String,String> keyword3 = new HashMap<>();

    private Map<String,String> keyword4 = new HashMap<>();

    private Map<String,String> keyword5 = new HashMap<>();

    public Map<String, String> getKeyword1() {
        return keyword1;
    }

    public Map<String, String> getKeyword2() {
        return keyword2;
    }

    public Map<String, String> getKeyword3() {
        return keyword3;
    }

    public Map<String, String> getKeyword4() {
        return keyword4;
    }

    public Map<String, String> getKeyword5() {
        return keyword5;
    }
}
