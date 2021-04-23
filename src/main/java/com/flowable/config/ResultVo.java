package com.flowable.config;



import lombok.Data;
import java.io.Serializable;

/**
 * @author zhs
 * @Description 前端结果类型
 * @createTime 2020/4/23 0023 下午 8:39
 */
@Data
public class ResultVo<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1282981091525006494L;

    /**
     * 操作结果 true:成功 false:失败
     */
    private String result = Boolean.TRUE.toString();

    /**
     * 结果描述
     */
    private String resultDesc = "操作成功";

    /**
     * 结果对象
     */
    private T data;

    /**
     * 状态码
     */
    private String statusCode;


    private String errorMsg;

    /**
     * 设置返回结果为false
     */
    public void setResultToFalse() {
        resultDesc = "操作失败";
        result = Boolean.FALSE.toString();
    }



}