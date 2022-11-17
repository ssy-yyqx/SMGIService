package cn.netty.server.components;



import cn.netty.server.web.enums.HttpStatus;

import java.util.HashMap;

/**
 * 操作消息提醒
 *
 * @author htht
 */
public class ResponseEntity extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    public static final String CODE_TAG = "code";

    /**
     * 返回内容
     */
    public static final String MSG_TAG = "msg";

    /**
     * 数据对象
     */
    public static final String DATA_TAG = "data";

    /**
     * 初始化一个新创建的 ResponseEntity 对象，使其表示一个空消息。
     */
    public ResponseEntity() {
    }

    /**
     * 初始化一个新创建的 ResponseEntity 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public ResponseEntity(int code, String msg) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 ResponseEntity 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    public ResponseEntity(int code, String msg, Object data) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
//        if (StringUtils.isNotNull(data)) {
//            super.put(DATA_TAG, data);
//        }
        super.put(DATA_TAG, data);
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static ResponseEntity success() {
        return ResponseEntity.success("成功");
    }


    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static ResponseEntity success(Object data) {
        return ResponseEntity.success("成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static ResponseEntity success(String msg, Object data) {
        return new ResponseEntity(HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static ResponseEntity error() {
        return ResponseEntity.error("Failure");
    }

    /**
     * 返回警告消息
     *
     * @param msg 返回提示信息
     * @return
     */
    public static ResponseEntity warn(String msg) {
        return new ResponseEntity(HttpStatus.BUSINESS_CODE, msg, null);
    }

    /**
     * 返回警告消息
     *
     * @param msg 返回内容
     * @return
     */
    public static ResponseEntity warn(String msg, Object data) {
        return new ResponseEntity(HttpStatus.BUSINESS_CODE, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static ResponseEntity error(String msg) {
        return ResponseEntity.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static ResponseEntity error(String msg, Object data) {
        return new ResponseEntity(HttpStatus.ERROR, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg  返回内容
     * @return 警告消息
     */
    public static ResponseEntity error(int code, String msg) {
        return new ResponseEntity(code, msg, null);
    }
}
