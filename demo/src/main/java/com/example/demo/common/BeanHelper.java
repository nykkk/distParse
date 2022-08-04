package com.example.demo.common;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class BeanHelper {

    private final static Logger log = LoggerFactory.getLogger(BeanHelper.class);

    public static <T> T copyProperties(Object source, Class<T> target) {
        try {
            T t = target.newInstance();
            BeanUtils.copyProperties(source, t);
            return t;
        } catch (Exception e) {
            log.error("【数据转换】数据转换出错，目标对象{}构造函数异常", target.getName(), e);
//            throw new LyException(ExceptionEnum.DATA_TRANSFER_ERROR);
            return null;
        }
    }

    public static <T> List<T> copyWithCollection(List<?> sourceList, Class<T> target) {
        try {
            return sourceList.stream().map(s -> copyProperties(s, target)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("【数据转换】数据转换出错，目标对象{}构造函数异常", target.getName(), e);
//            throw new LyException(ExceptionEnum.DATA_TRANSFER_ERROR);
            return null;
        }
    }

    public static <T> Set<T> copyWithCollection(Set<?> sourceList, Class<T> target) {
        try {
            return sourceList.stream().map(s -> copyProperties(s, target)).collect(Collectors.toSet());
        } catch (Exception e) {
            log.error("【数据转换】数据转换出错，目标对象{}构造函数异常", target.getName(), e);
            //throw new LyException(ExceptionEnum.DATA_TRANSFER_ERROR);
            return null;
        }
    }
}
