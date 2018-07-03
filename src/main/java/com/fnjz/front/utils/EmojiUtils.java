package com.fnjz.front.utils;

import com.github.binarywang.java.emoji.EmojiConverter;

/**
 * emoji表情转化类
 * Created by yhang on 2018/7/3.
 */
public class EmojiUtils {

    private static EmojiConverter emojiConverter = EmojiConverter.getInstance();

    public static boolean containsEmoji(String source) {
        int len = source.length();
        boolean isEmoji = false;
        for (int i = 0; i < len; i++) {
            char hs = source.charAt(i);
            if (0xd800 <= hs && hs <= 0xdbff) {
                if (source.length() > 1) {
                    char ls = source.charAt(i + 1);
                    int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
                    if (0x1d000 <= uc && uc <= 0x1f77f) {
                        return true;
                    }
                }
            } else {
                // non surrogate
                if (0x2100 <= hs && hs <= 0x27ff && hs != 0x263b) {
                    return true;
                } else if (0x2B05 <= hs && hs <= 0x2b07) {
                    return true;
                } else if (0x2934 <= hs && hs <= 0x2935) {
                    return true;
                } else if (0x3297 <= hs && hs <= 0x3299) {
                    return true;
                } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d
                        || hs == 0x3030 || hs == 0x2b55 || hs == 0x2b1c
                        || hs == 0x2b1b || hs == 0x2b50 || hs == 0x231a) {
                    return true;
                }
                if (!isEmoji && source.length() > 1 && i < source.length() - 1) {
                    char ls = source.charAt(i + 1);
                    if (ls == 0x20e3) {
                        return true;
                    }
                }
            }
        }
        return isEmoji;
    }

    /**
     * emoji转字符串
     * @param emoji
     * @return
     */
    public static String emojiToAlias(String emoji){
        //判断是否为emoji
        if(containsEmoji(emoji)){
            return emojiConverter.toAlias(emoji);
        }
        return emoji;
    }

    /**
     * 字符串转emoji
     * @param alias
     * @return
     */
    public static String aliasToEmoji(String alias){

        return emojiConverter.toUnicode(alias);
    }
}
