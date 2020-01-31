package com.htt.kon.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * json 工具类, 使用jackson库
 *
 * @author dou
 * @date 2019/3/22 14:25
 */
public class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        // 忽略未知属性
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 将对象转换成json字符串
     */
    public static String bean2Json(Object data) {
        try {
            return MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 将json字符串转换成对象
     */
    public static <T> T json2Bean(String jsonData, Class<T> beanType) {
        try {
            return MAPPER.readValue(jsonData, beanType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将json字符串转换成bean list
     */
    public static <T> List<T> json2List(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            return MAPPER.readValue(jsonData, javaType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
