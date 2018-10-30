package com.fnjz.front.utils;

import org.junit.Test;

import java.util.HashMap;
import java.util.Random;

import static java.lang.Math.pow;

/**
 * Created by sqwang-work on 2018/1/30.
 * 生成自定义长度的邀请码
 * 可以自定义进制、自定义补位码及自定码表
 */
public class ShareCodeUtil {

    /**
     * 获取一个随机码表（0~9）
     * @return
     */
    public static String getCodeTable(){
        HashMap hm = new HashMap();
        Random ran1 = new Random(10);
        System.out.println("使用种子为10的Random对象生成[0,10)内随机整数序列: ");
        String strRandom = "";
        for (int i = 0; i < 10; i++) {
            strRandom = ran1.nextInt(10)+"";
            if (hm.containsKey(strRandom)){
                i--;
                continue;
            }
            hm.put(strRandom,strRandom);
            System.out.print(strRandom+" ");
        }
        return strRandom;
    }

    //九进制码表
    public static final String CodeTable_NineDecimal = "306781452";
    //public static final String CodeTable_NineDecimal   = "012345678";
    //9进制（注意这里的进制和补位字符的关系，补位字符不要造成）
    public static final int XDecimal = 9;
    //邀请码长度
    public static final int ShareCodeLength = 8;
    //邀请码补位字符
    public static final char ShareCodePatch = '9';

    /**
     * 通过9进制对用户id进行7位数字编码，不足7位首位补9
     * @param id
     * @return
     */
    public static String id2sharecode(int id)
    {
        //获取一个随机码表
        //String codeTable = getCodeTable();
        String sharecode = "";
        int num = id;
        int mod = 0;
        while (num > 0)
        {
            mod = num % XDecimal;
            num = num / XDecimal;
            sharecode = sharecode + CodeTable_NineDecimal.charAt(mod);
        }
        //不足7位进行补位，首位补9
        while(sharecode.length() < ShareCodeLength){
            sharecode = ShareCodePatch + sharecode;
        }
        return sharecode;
    }

    /**
     * @param shareCode 邀请码
     * @return id
     */
    public static int sharecode2id(String shareCode)
    {
        if (shareCode==null || shareCode.length()<=0){
            return 0;
        }
        //去除首位可能的补位符
        while(shareCode.charAt(0) == ShareCodePatch){
            shareCode = shareCode.substring(1, shareCode.length());
        }
        //
        int len = shareCode.length();
        int num = 0;
        for (int i=0; i<len; i++) {
            char charAt = shareCode.charAt(i);
            int indexOf = CodeTable_NineDecimal.lastIndexOf(charAt);//码表位置，表示数字 0 1 2 3 4 5 6 7 8
            int value = (int)pow(XDecimal, i);
            num += indexOf * value;
        }

        return num;
    }


//    /**
//     *  思考：
//     *
//     *  一个10进制的数字短还是一个16进制的数字短？
//        肯定是16进制相对短一些，所以我们可以直接把用户id转成10+26=36进制的不就可以了吗？具体代码如下：
//        邀请码保证了唯一性，并且长度不会太长，用户id也能够根据邀请码反推出来，但是有一点不好的是，
//        别人也可以根据邀请码去反推出user_id，因此，我们需要做一些优化。
//     * @param user_id
//     * @return
//     */
//    public static String createCode(int user_id)
//    {
//        String source_string = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
//        int num = user_id;
//        int mod =0;
//        String code = "";
//        while(num>0)
//        {
//            mod = num % 36;
//            num = (num - mod) / 36;
//            code = code + source_string.charAt(mod);
//        }
//        return code;
//    }
//
//    /**
//     * 把0剔除，当做补位符号，比如小于四位的邀请码在高位补0，这样36进制就变成了35进制，
//     * 然后把字符串顺序打乱，这样，在不知道$source_string的情况下，是没办法解出正确的user_id的。
//       代码如下：
//        0 - o; 1 - I
//     * @param user_id
//     * @return
//     */
//    public static String encode(int user_id) {
//        String source_string = "E5FCDG3HQA4B1NOPIJ2RSTUV67MWX89KLYZ";
//        int num = user_id;
//        int mod =0;
//        String code = "";
//        while(num>0){
//            mod = num % 35;
//            num = (num - mod) / 35;
//            code = code + source_string.charAt(mod);
//        }
//        if(code.length()<=3)
//            code = "0" + code;
//        return code;
//    }
//    /**
//     * 这样，对应user_id的唯一邀请码就生成了，再附一个解码函数：
//     * */
//    public static String decode(String code) {
//        String source_string = "E5FCDG3HQA4B1NOPIJ2RSTUV67MWX89KLYZ";
//
//        if (code==null || code.length()<=0){
//            return "";
//        }
//        if (code.charAt(0)=='0'){
//            code = code.substring(1, code.length()-1);
//        }
//
////        int len = code.length();
////        $code = strrev($code);
////        String num = "";
////        for (int i=0; i < len; i++) {
////            num += strpos($source_string, source_string.charAt(i) * pow(35, i));
////        }
////        return num+"";
//        return "";
//    }
//
//    public static void testRandom() {
//        //创建一张表table，每次生成一条之后，查询一下表中是否已存在
//        //这样的话，数据越来越多，后面就挺麻烦的，判断多次才能取到一个表中不存在的
//
//        String result = getRandom();
//        System.out.println(result);
//
//        //建议一次性在数据库表中插入多条数据（如10000条），
//        //表名table、主键自增长id、随机数：randoms,编写程序
//        //执行insert 语句插入
//        //然后创建一个sequence，从1开始，步数为1
//        //select randoms from table where id = (select sequence.nextval from dual);
//    }
//    public static String getRandom(){
//        String result = "";
//        //下面的6改成8就是8位随机数字
//        while (result.length() < 7) {
//            String str = String.valueOf((int)(Math.random()*10));
//            if(result.indexOf(str) == -1){
//                result += str;
//            }
//
//            //判断该字符串是否存在
////            if( ! checkExists($code))
////                return $code;
//
//        }
//        return result;
//    }




    public static void test(){
//        String str0 = getRandom();
//        String str1 = createCode(10028);
        //String str2 = createCode2(10028);

        String str2 = id2sharecode(10267);
        int str3 = sharecode2id(str2);
        int a = 10;
    }

    @Test
    public void  main(){
        String str2 = id2sharecode(3427);
        System.out.println(str2);
        int str3 = sharecode2id("99997648");
        System.out.println(str3);
        int str4 = sharecode2id("99993451");
        System.out.println(str4);
    }

}
