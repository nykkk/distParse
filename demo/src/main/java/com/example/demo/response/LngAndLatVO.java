package com.example.demo.response;

import lombok.Data;

import java.io.Serializable;

/**
 * <p><b>LngAndLatVO</b></p>
 * <p> </p>
 * <p>Copyright: The Research Group of Biodiversity Informatics (BiodInfo Group) - 中国科学院动物研究所生物多样性信息学研究组</p>
 *
 * @Author NY
 * @Date: 2022/8/5 10:42
 * @Version V1.0
 * @since JDK 1.8.0_162
 */
@Data
public class LngAndLatVO implements Serializable {

    // 修正后的省名
    private String province;

    // 修正后的市名
    private String city;

    // 修正后的县名
    private String county;

    private String adcode;

    // 经度
    private Double lng;

    // 纬度
    private Double lat;

    // 高德api解析出的完整地址
    private String apiaddresss;
}
