package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.common.BaseResults;
import com.example.demo.common.BaseResultsUtil;
import com.example.demo.service.ParseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p><b>ApiController</b></p>
 * <p> </p>
 * <p>Copyright: The Research Group of Biodiversity Informatics (BiodInfo Group) - 中国科学院动物研究所生物多样性信息学研究组</p>
 *
 * @Author NY
 * @Date: 2022/8/15 15:06
 * @Version V1.0
 * @since JDK 1.8.0_162
 */
@RestController
@RequestMapping("/v1/distparse")
public class ApiController {

    @Autowired
    private ParseService parseService;

    /**
     * <p><b>地名解析(省级)<b><p>
     * <p>参数：解析语句<p>
     *
     * @Author: Ny
     * @date 2022/8/15
     */
    @GetMapping("/parseProvinceData")
    public BaseResults parseProvinceData(@RequestParam("content") String content) {
        JSON json = parseService.parseProvinceData(content);
        return BaseResultsUtil.success(json);
    }

    /**
     * <p><b>地名解析（县级）（先查省份后查县）<b><p>
     * <p>参数：解析语句<p>
     *
     * @Author: Ny
     * @date 2022/8/15
     */
    @GetMapping("/parseCountyData")
    public BaseResults parseCountyData(@RequestParam("content") String content) {
        BaseResults baseResults = parseService.parseCountyData(content);
        return baseResults;
    }

    /**
     * <p><b>地名解析（县级）（无视省份直接查县）<b><p>
     * <p>参数：解析语句<p>
     *
     * @Author: Ny
     * @date 2022/8/15
     */
    @GetMapping("/parseCountyDataAll")
    public BaseResults parseCountyDataAll(@RequestParam("content") String content) {
        BaseResults baseResults = parseService.parseCountyDataAll(content);
        return baseResults;
    }

    /**
     * <p><b>地名解析（县级）（在规定的省列表中查询对应县）<b><p>
     * <p>参数：解析语句,省份合集<p>
     *
     * @Author: Ny
     * @date 2022/8/15
     */
    @GetMapping("/parseCountyDataPart")
    public BaseResults parseCountyDataPart(@RequestParam("content") String content, @RequestParam("provinces") String provinces) {
        BaseResults baseResults = parseService.parseCountyDataPart(content, provinces);
        return baseResults;
    }

    /**
     * <p><b>经纬度查询与参数修正<b><p>
     * <p>参数：省、市、县、小地名<p>
     *
     * @Author: Ny
     * @date 2022/8/15
     */
    @GetMapping("/parseLngAndLat")
    public BaseResults parseLngAndLat(@RequestParam("province") String province, @RequestParam("city") String city, @RequestParam("county") String county, @RequestParam("locality") String locality) {
        BaseResults baseResults = parseService.parseLngAndLat(province, city, county, locality);
        return baseResults;
    }

    /**
     * <p><b>经纬度查询与参数修正（限定高德查询）<b><p>
     * <p>参数：省、市、县、小地名<p>
     *
     * @Author: Ny
     * @date 2022/8/24
     */
    @GetMapping("/parseLngAndLatByGd")
    public BaseResults parseLngAndLatByGd(@RequestParam("province") String province, @RequestParam("city") String city, @RequestParam("county") String county, @RequestParam("locality") String locality) {
        BaseResults baseResults = parseService.parseLngAndLatByGd(province, city, county, locality);
        return baseResults;
    }

    /**
     * <p><b>解析语句经纬度查询<b><p>
     * <p>参数：解析语句<p>
     *
     * @Author: Ny
     * @date 2022/8/15
     */
    @GetMapping("/parseLngAndLatFromData")
    public BaseResults parseLngAndLatFromData(@RequestParam("content") String content) {
        BaseResults baseResults = parseService.parseLngAndLatFromData(content);
        return baseResults;
    }

    /**
     * <p><b>解析语句经纬度查询（限定高德查询）<b><p>
     * <p>参数：解析语句<p>
     *
     * @Author: Ny
     * @date 2022/8/24
     */
    @GetMapping("/parseLngAndLatFromDataByGd")
    public BaseResults parseLngAndLatFromDataByGd(@RequestParam("content") String content) {
        BaseResults baseResults = parseService.parseLngAndLatFromDataByGd(content);
        return baseResults;
    }

}
