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

    private static final Logger logger = Logger.getLogger(WeChatUtils.class);

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
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = bufferedReader.readLine()) != null) {
                list.add(tempString);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.toString());
        }
    }

    public static boolean checkNickName(String nickNname){
        for (String s : list) {
            if(StringUtils.contains(nickNname,s)){
                return false;
            }
        }
        if(nickNname.startsWith("蜂鸟")){
            return false;
        }
        return true;
    }

    public static String checkWechatNickName(String nickNname){
        if(nickNname.startsWith("蜂鸟")){
            nickNname = nickNname.replace("蜂鸟","**");
            return nickNname;
        }
        return nickNname;
    }

    public static void main(String[] args){
        System.out.println(checkWechatNickName("蜂鸟快跑"));
    }
}
