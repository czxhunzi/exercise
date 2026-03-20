package com.example.springboot.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//统一法返回接口，对象在前端的表示就是json数据
public class Result {



    private String code;
    private Object data;
    private String msg;//包含请求失败的原因


    public static Result success(){//因为是静态方法，所以调用时可以直接通过类名访问
        Result result = new Result();
        result.setCode(ResultCodeEnum.SUCCESS.code);
        result.setMsg(ResultCodeEnum.SUCCESS.msg);
        return result;
    }
    public static Result success(Object obj){//因为是静态方法，所以调用时可以直接通过类名访问
        Result result = new Result();
        result.setCode(ResultCodeEnum.SUCCESS.code);
        result.setMsg(ResultCodeEnum.SUCCESS.msg);
        result.setData(obj);
        return result;
    }

    public static Result error(){
        Result result = new Result();
        result.setCode(ResultCodeEnum.ERROR.code);
        result.setMsg(ResultCodeEnum.ERROR.msg);
        return result;
    }

    public static Result error(String code, String msg){
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    //这才是最关键的方法
    public static Result error(ResultCodeEnum resultCodeEnum) {
        Result tResult = new Result();
        tResult.setCode(resultCodeEnum.code);
        tResult.setMsg(resultCodeEnum.msg);
        return tResult;
    }

}
