package com.mymall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;


/**
 * Created by lamZe on 2017/11/17.<br>
 *
 */


@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable{

    private int status; //成功为0，失败为1
    private String msg; //返回的响应信息
    private T data;     //返回对象信息

    private ServerResponse(int status){
        this.status=status;
    }

    private ServerResponse(int status,T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status,String msg,T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status,String msg) {
        this.status = status;
        this.msg = msg;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    //返回的属性要设置get
    public int getStatus() {
        return this.status;
    }

    public T getData() {
        return this.data;
    }

    public String getMsg() {return this.msg;}


    public static <T> ServerResponse<T> createBySuccess() {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }


    public static <T> ServerResponse<T> createBySuccessMessage(String msg) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg,T data) {
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> ServerResponse<T> createByError() {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode
        .ERROR.getDesc());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String msg) {
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), msg);
    }

    public static<T> ServerResponse<T> createByErrorCodeMessage(int errorCode,String msg) {
        return new ServerResponse<T>(errorCode,msg);
    }



}
