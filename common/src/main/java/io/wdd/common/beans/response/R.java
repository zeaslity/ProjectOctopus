package io.wdd.common.beans.response;


import lombok.Data;

@Data
public class R<T> {


    int code;

    String msg;

    T data;

    private R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public R() {

    }

    public static <T> R<T> ok(T data) {
        return resetResult(data, ResultStat.SUCCESS);
    }

    public static <T> R<T> okNoData() {
        return new R();
    }

    public static <T> R<T> failed(T data) {
        return resetResult(data, ResultStat.FAILED);
    }

    // access from inner
    private static <T> R<T> resetResult(T data, ResultStat resultStat) {
        return new R(resultStat.getCode(), resultStat.getDescription(), data);
    }

    // access to public
    public static <T> R<T> resetResult(int code, String msg, T data) {
        return new R<>(code
                , msg, data);
    }

}
