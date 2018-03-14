package com.mmall.util;

import com.mmall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        // 将对象的所有字段全部列入
        objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
        // 取消默认转换timestamps的形式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);
        //忽略空bean转json的错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        //所有的日期都统一为"yyyy-MM-dd HH:mm:ss"的格式
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));

        //  --反序列化--
        //忽略在json字符串中存在，而在java对象中不存在对应属性的情况。防止错误
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static <T> String obj2String(T obj){
        if (obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to String error.",e);
            return null;
        }
    }

    public static <T> String obj2StringPretty(T obj){
        if (obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("Parse Object to String error.",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str,Class<T> clazz){
        if (StringUtils.isEmpty(str) || clazz == null){
            return null;
        }
        try {
            return clazz.equals(String.class) ? (T)str : objectMapper.readValue(str,clazz);
        } catch (Exception e) {
            log.warn("Parse String to Object error.",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str, TypeReference<T> typeReference){
        if (StringUtils.isEmpty(str) || typeReference == null){
            return null;
        }
        try {
            return (T)(typeReference.getType().equals(str)? str  : objectMapper.readValue(str,typeReference));
        } catch (Exception e) {
            log.warn("Parse String to Object error.",e);
            return null;
        }
    }

    public static <T> T string2Obj(String str, Class<?> collectionClass, Class<?>... elementClass){
        if (StringUtils.isEmpty(str) || collectionClass == null || elementClass == null){
            return null;
        }
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass,elementClass);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (Exception e) {
            log.warn("Parse String to Object error.",e);
            return null;
        }
    }

    public static void main(String[] args) {
        User u1 = new User();
        u1.setId(122);
        u1.setUsername("luferzz");
        u1.setEmail("lufer@lufernew.com");

        User u2 = new User();
        u2.setId(123);
        u2.setUsername("luferxx");
        u2.setEmail("luferxx@lufernew.com");

        List<User> userList = new ArrayList<>();
        userList.add(u1);
        userList.add(u2);

        String userJson1 = obj2StringPretty(u1);
        log.info(userJson1);
        log.info("------------------------------");
        String userListJson = obj2StringPretty(userList);
        log.info("---------userListJson---------");
        log.info(userListJson);

        User userDe = string2Obj(userJson1,User.class);  // 单例反序列化
        List<User> userList1 = string2Obj(userListJson,List.class,User.class);
        List<User> userListDeTest = string2Obj(userListJson,List.class);
        List<User> userListDe = string2Obj(userListJson, new TypeReference<List<User>>() {
        });
        System.out.println("__end___");
    }
}
