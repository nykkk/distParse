package com.example.demo.response;

import lombok.Data;

import java.io.Serializable;

/**
 * <p><b>CityNewVO</b></p>
 * <p> </p>
 * <p>Copyright: The Research Group of Biodiversity Informatics (BiodInfo Group) - 中国科学院动物研究所生物多样性信息学研究组</p>
 *
 * @Author NY
 * @Date: 2022/8/1 14:36
 * @Version V1.0
 * @since JDK 1.8.0_162
 */
@Data
public class CityNewVO implements Serializable {

    private String id;

    private String valueContent;

    private String province;

    private String city;

    private String adcode;

}
