package io.wdd.common.handler;

import io.wdd.common.beans.response.ResultStat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyRuntimeException extends RuntimeException {

    private Object data;
    private ResultStat status;

    private Object[] params;

    public MyRuntimeException(String msg) {
        super(msg);
    }

    public MyRuntimeException(String msg, Object... params) {
        super(msg);
        this.params = params;
    }

    public MyRuntimeException(ResultStat status, Object data, String msg, Object... params) {
        super(msg == null ? status.getDescription() : msg);
        this.data = data;
        this.status = status;
        this.params = params;
    }

    public MyRuntimeException(Throwable cause) {
        super(cause);
    }

    public MyRuntimeException(Throwable cause, String msg) {
        super(msg, cause);
    }

}

