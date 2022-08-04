package com.example.demo.response;

import lombok.Data;

import java.io.Serializable;

/**
 * <p><b>CountyNewVO</b></p>
 * <p> </p>
 * <p>Copyright: The Research Group of Biodiversity Informatics (BiodInfo Group) - 中国科学院动物研究所生物多样性信息学研究组</p>
 *
 * @Author NY
 * @Date: 2022/8/1 14:37
 * @Version V1.0
 * @since JDK 1.8.0_162
 */
@Data
public class CountyNewVO implements Serializable {

    private String id;

    private String valueContent;

    private String city;

    private String province;

    private String county;

    private String adcode;
}
