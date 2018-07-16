package com.fnjz.front.utils;

import com.vdurmont.emoji.EmojiParser;
import org.junit.Test;

/**
 * emoji表情转化类
 * Created by yhang on 2018/7/3.
 */
public class EmojiUtils {

    /**
     * emoji转字符串
     *
     * @param emoji
     * @return
     */
    public static String emojiToAlias(String emoji) {
        //判断是否为emoji
        //if (EmojiManager.isEmoji(emoji)) {
            return EmojiParser.parseToAliases(emoji);
        //}
        //return emoji;
    }

    /**
     * 字符串转emoji
     *
     * @param alias
     * @return
     */
    public static String aliasToEmoji(String alias) {
        return EmojiParser.parseToUnicode(alias);
    }

    @Test
    public void  emojiToAlias() {
        //🤣
        String emoji = " ";
        String alias = EmojiUtils.emojiToAlias(emoji);
        System.out.println(alias);
        System.out.println(EmojiUtils.aliasToEmoji(alias));
    }
}
