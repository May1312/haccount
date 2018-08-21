package com.fnjz.front.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 昵称过滤敏感词汇
 * Created by yhang on 2018/8/9.
 */
public class FilterCensorWordsUtils {

    private static final Logger logger = Logger.getLogger(FilterCensorWordsUtils.class);

    private static List<String> list;
    //初始化敏感词库
    static {
        BufferedReader bufferedReader;
        list = new ArrayList<>();
        try {
            InputStream in = FilterCensorWordsUtils.class.getResourceAsStream("/fnjz/NameCensorWords.txt");
            InputStreamReader reader=new InputStreamReader(in,"utf8");
            bufferedReader = new BufferedReader(reader);
            String tempString ;
            // 一次读入一行
            while ((tempString = bufferedReader.readLine()) != null) {
                list.add(tempString);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
    }

    public static boolean checkNickName(String nickName){
        for (String s : list) {
            if(StringUtils.contains(nickName,s)){
                return false;
            }
        }
        if(nickName.startsWith("蜂鸟")){
            return false;
        }
        return true;
    }

    public static String checkWechatNickName(String nickName){
        if(nickName.startsWith("蜂鸟")){
            nickName = nickName.replace("蜂鸟","**");
            return nickName;
        }
        return nickName;
    }

    public static void main(String[] args){
        System.out.println(checkWechatNickName("蜂鸟快飞"));
    }
}
