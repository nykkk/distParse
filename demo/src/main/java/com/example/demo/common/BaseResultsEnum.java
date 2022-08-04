package com.example.demo.common;

/**
 * <p><b>BaseResultsEnum</b></p>
 * <p> </p>
 * <p>Copyright: The Research Group of Biodiversity Informatics (BiodInfo Group) - 中国科学院动物研究所生物多样性信息学研究组</p>
 *
 * @Author NY
 * @Date: 2020/5/27 9:25
 * @Version V1.0
 * @since JDK 1.8.0_162
 */

public enum BaseResultsEnum {

    /**/
    SUCCESS(200, "OK"),
    API_ERROR(401, "API密钥错误"),
    Api_num_error(402, "API请求次数达到上限"),
    NOT_FOUND(404, "未找到相关数据"),
    TOKEN_NOT_FOUND(405,"缺少API密钥"),
    UNKNOWN_ERROR(500, "未知错误");


    private Integer code;
    private String message;

    BaseResultsEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}

