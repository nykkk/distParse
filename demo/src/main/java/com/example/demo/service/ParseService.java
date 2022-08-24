package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.example.demo.common.BaseResults;
import com.example.demo.response.ProvinceVO;

import java.util.List;

/**
 * <p><b>ParseService</b></p>
 * <p> </p>
 * <p>Copyright: The Research Group of Biodiversity Informatics (BiodInfo Group) - 中国科学院动物研究所生物多样性信息学研究组</p>
 *
 * @Author NY
 * @Date: 2022/8/2 9:37
 * @Version V1.0
 * @since JDK 1.8.0_162
 */
public interface ParseService {

    /**
     * <p><b>地名解析(省级)<b><p>
     * <p>参数：解析语句<p>
     * @Author: Ny
     * @date  2022/8/2
     */
    JSON parseProvinceData(String content);

    /**
     * <p><b>地名解析（县级）（先查省份后查县）<b><p>
     * <p>参数：解析语句<p>
     * @Author: Ny
     * @date  2022/8/2
     */
    BaseResults parseCountyData(String content);

    /**
     * <p><b>地名解析（县级）（无视省份直接查县）<b><p>
     * <p>参数：解析语句<p>
     * @Author: Ny
     * @date  2022/8/10
     */
    BaseResults parseCountyDataAll(String content);

    /**
     * <p><b>地名解析（县级）（在规定的省列表中查询对应县）<b><p>
     * <p>参数：解析语句,省份合集<p>
     * @Author: Ny
     * @date  2022/8/10
     */
    BaseResults parseCountyDataPart(String content,String provinces);

    /**
     * <p><b>经纬度查询与参数修正<b><p>
     * <p>参数：省、市、县、小地名<p>
     * @Author: Ny
     * @date  2022/8/4
     */
    BaseResults parseLngAndLat(String province,String city,String county,String locality);

    /**
     * <p><b>经纬度查询与参数修正（限定高德查询）<b><p>
     * <p>参数：省、市、县、小地名<p>
     * @Author: Ny
     * @date  2022/8/24
     */
    BaseResults parseLngAndLatByGd(String province,String city,String county,String locality);

    /**
     * <p><b>经纬度查询<b><p>
     * <p>参数：解析语句<p>
     * @Author: Ny
     * @date  2022/8/5
     */
    BaseResults parseLngAndLatFromData(String content);

    /**
     * <p><b>经纬度查询(限定高德查询)<b><p>
     * <p>参数：解析语句<p>
     * @Author: Ny
     * @date  2022/8/24
     */
    BaseResults parseLngAndLatFromDataByGd(String content);
}
