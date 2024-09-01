package com.questionbrushingplatform.common.exception;

/**
 * 用户未登录
 */
public class UserNotLoginException extends BaseException {

    public UserNotLoginException() {
    }

    public UserNotLoginException(String msg) {
        super(msg);
    }

}
