package com.zccl.ruiqianqi.move;

/**
 * Created by ruiqianqi on 2017/3/20 0020.
 */

public class MoveException extends Exception {
    public MoveException() {
        super();
    }

    public MoveException(String message) {
        super(message);
    }

    public MoveException(String message, Throwable cause) {
        super(message, cause);
    }

    public MoveException(Throwable cause) {
        super(cause);
    }
}
