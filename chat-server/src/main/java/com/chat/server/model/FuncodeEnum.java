package com.chat.server.model;

/**
 * @author 王战伟
 * @email wangzhanwei@lumlord.com
 * @date 2020/3/25  9:10
 */
public enum FuncodeEnum {
//    HEART_BEAT("心跳","01"),
//    AUTH_USER("用户认证","02"),
//    MESSAGE_SEND("消息发送",(byte)3),
//    MESSAGE_BROAD("消息广播",(byte)11),
//    ERROR_INFO("错误信息",(byte)4),
//    TOPIC_SUBSCRIBE("消息订阅",(byte)5),
//    TOPIC_UNSUBSCRIBE("取消订阅",(byte)6),
//    NOTICE_SUBSCRIBE_OK("订阅成功通知",(byte)7),
//    NOTICE_UNSUBSCRIBE_OK("取消订阅通知",(byte)8),
//    NOTICE_AUTH_OK("认证成功通知",(byte)9),
//    NOTICE_AUTH_FAIL("认证失败通知",(byte)10),

    //客户端类型
      USERTYPE_NORMAL("聊天用户","01"),
      USERTYPE_SERVER("服务器类型","02"),

    //消息类型
      OPERATETYPE_LOGIN("客户端登录","11"),
      OPERATETYPE_SINGLECHAT("客户端普通消息","12"),
      OPERATETYPE_BROADCAST("广播","13");


    private String desc;//功能描述
    private String code;  //功能码


    private FuncodeEnum(String desc, String code) {
        this.desc = desc;
        this.code = code;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 帮助类 跟据code字段获取eum实例
     * @param code
     * @return
     */
    public static FuncodeEnum getEumInstanceByType(String code){
        for(FuncodeEnum funcodeEnum:FuncodeEnum.values()){
            if(funcodeEnum.getCode().equals(code)){
                return funcodeEnum;
            }
        }
        return  null;
    }
}
