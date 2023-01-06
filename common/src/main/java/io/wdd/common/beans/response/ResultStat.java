package io.wdd.common.beans.response;

public enum ResultStat {

    SUCCESS(1000, "success"),

    FAILED(5001, "failed"),

    VALIDATE_FAILED(1002, "参数校验失败"),

    PARAM_ERROR(1003, "请求参数错误！"),

    BAD(5001, "all error !");

    int code;

    String description;

    ResultStat(int code, String description){
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription(){
        return description;
    }
}
