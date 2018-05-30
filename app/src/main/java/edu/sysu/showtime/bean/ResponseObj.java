package edu.sysu.showtime.bean;
//从服务器返回的数据结构体，这里因为不确定返回的data是单个对象，还是多对象的列表，因此采用泛型
public class ResponseObj {
    public final String CODE_SUCCESS = "SUCCESS";
    public final String CODE_FAIL = "Error";
    public String responseCode;
    public String errCode;
    public String errMsg;

    public String getCode(){
        return responseCode;
    }
    public String getErrCode(){
        return errCode;
    }
    public String getErrMsg(){
        return errMsg;
    }
}
