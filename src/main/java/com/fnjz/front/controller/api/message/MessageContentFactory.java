package com.fnjz.front.controller.api.message;

/**
 * @Auther: yonghuizhao
 * @Date: 2018/11/13 15:29
 * @Description:
 */
public class MessageContentFactory {
    /**
     * 功能描述: 获取通知消息内容
     *
     * @param:
     * @return:
     * @auther: yonghuizhao
     * @date: 2018/11/13 16:46
     */
    public static String  getMessageContent(MessageType messageType,String accountType,String createName,String beforeMoney,String nowMoney){
        String messageContent="";
        //移除通知
        if (messageType.equals(MessageType.removeTheNotification)){
            messageContent="被移除通知：您已被管理员从 ["+accountType+"] 中移除。";
        }//修改预算通知
        else if (messageType.equals(MessageType.reviseBudgetNotification)){

            if (nowMoney.equals("-1")){
                messageContent = "提示: ["+accountType+"] 账本的预算已被  {"+createName+"} 删除 ";
            }else {
                messageContent = "提示: ["+accountType+"] 账本的预算已被  {"+createName+"} 由之前的 "+beforeMoney+" 元修改为 "+nowMoney+" 元" ;
            }

        }
        return  messageContent;
    }
}
