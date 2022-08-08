package com.example.demo.response;

import lombok.Data;

import java.io.Serializable;

/**
 * <p><b>CountyOldVO</b></p>
 * <p> </p>
 * <p>Copyright: The Research Group of Biodiversity Informatics (BiodInfo Group) - 中国科学院动物研究所生物多样性信息学研究组</p>
 *
 * @Author NY
 * @Date: 2022/8/1 14:39
 * @Version V1.0
 * @since JDK 1.8.0_162
 */
@Data
public class CountyOldVO implements Serializable {

    private String valueContent;

    private String province;

    private String city;

    private String newCounty;

    private String adcode;

    private String oldCounty;

    private String oldAdcode;

    private String oldVersion;
}
