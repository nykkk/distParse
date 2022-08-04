package com.example.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.common.BaseResults;
import com.example.demo.common.BaseResultsUtil;
import com.example.demo.common.UUIDUtils;
import com.example.demo.entity.Geoobject;
import com.example.demo.repository.GeoobjectRepository;
import com.example.demo.response.CountyNewVO;
import com.example.demo.response.CountyOldVO;
import com.example.demo.response.ProvinceVO;
import com.example.demo.service.ParseService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * <p><b>ParseServiceImpl</b></p>
 * <p> </p>
 * <p>Copyright: The Research Group of Biodiversity Informatics (BiodInfo Group) - 中国科学院动物研究所生物多样性信息学研究组</p>
 *
 * @Author NY
 * @Date: 2022/8/2 9:37
 * @Version V1.0
 * @since JDK 1.8.0_162
 */
@Service
@SuppressWarnings("all")
public class ParseServiceImpl implements ParseService {

    @Autowired
    private GeoobjectRepository geoobjectRepository;

    @Override
    public JSON parseProvinceData(String content) {

     /*   List<Geoobject> provinceList = geoobjectRepository.findByGeotypeAsProvince();
        // 返回体
        List<ProvinceVO> provinceVOList = new ArrayList<>();

        for (Geoobject geoobject : provinceList) {
            String shortName = geoobject.getShortName();
            if (content.contains(shortName)){
                ProvinceVO provinceVO = new ProvinceVO();
                provinceVO.setId(UUIDUtils.getUUID32());
                provinceVO.setProvince(geoobject.getCngeoname());
                provinceVO.setAdcode(geoobject.getAdcode());
                provinceVOList.add(provinceVO);
            }
        }*/
        // 返回JSON
        JSONObject response = new JSONObject();


        // 省份列表
        List<Geoobject> provinceList = geoobjectRepository.findByGeotypeAsProvince();
        // 返回体
        List<ProvinceVO> provinceVOList = new ArrayList<>();

        // 1.处理地名+方位名
        if (content.contains("部分")) {
            content = content.replace("部分", " 部分");
        }
        if (content.contains("东部")) {
            content = content.replace("东部", " 东部");
        }
        if (content.contains("西部")) {
            content = content.replace("西部", " 西部");
        }
        if (content.contains("南部")) {
            content = content.replace("南部", " 南部");
        }
        if (content.contains("北部")) {
            content = content.replace("北部", " 北部");
        }
        // 2.处理被网页符号隔开的地名
        if (content.contains("<br>")) {
            content = content.replace("<br>", "");
        }


        // 3.处理“除”情况(a. （...除外）; b. 除...外; c.（除...）; d.其他)
        if (content.contains("除")) {
            // 添加标识，表明此数据存在争议
            response.put("statu", "0");
            // "除"第一次出现的索引
            int indexOfChu = content.indexOf("除");
            // “除”后面的语句
            String contentEnd = content.substring(indexOfChu);

            // 处理“除外）”情况
            if (content.contains("除外）")) {
                // 若（...除外）中包含省份，则对省份取反，留下剩余省份；若不包含省份，以括号外为主
                String[] split = content.split("除外）");
                String[] split1 = split[0].split("（");
                // 取出括号中的内容
                String contentIn = split1[split1.length - 1];

                provinceVOList = this.findByContent(content, contentIn);
                response.put("data", provinceVOList);
                return response;
            } else if (contentEnd.contains("外")) {
                // 处理情况“除...外”,确保“外”在除之后
                String[] split = contentEnd.split("外");
                // “除”和“外”之间的部分
                String contentIn = split[0];
                provinceVOList = this.findByContent(content, contentIn);
                response.put("data", provinceVOList);
                return response;
            } else if (content.contains("（除")) {
                String[] split = content.split("（除");
                String[] split1 = split[1].split("）");
                // 括号内容
                String contentIn = split1[0];
                provinceVOList = this.findByContent(content, contentIn);
                response.put("data", provinceVOList);
                return response;
            } else {
                // 正常处理
                for (Geoobject geoobject : provinceList) {
                    String shortName = geoobject.getShortName();
                    if (content.contains(shortName)) {
                        ProvinceVO provinceVO = new ProvinceVO();
                        provinceVO.setProvince(geoobject.getCngeoname());
                        provinceVO.setAdcode(geoobject.getAdcode());
                        provinceVOList.add(provinceVO);
                    }
                }
                response.put("data", provinceVOList);
                return response;
            }

        } else if (content.contains("见于各省") || content.equals("全国") || content.equals("中国")
                || content.contains("全国各地") || content.contains("中国各地")
                || content.contains("遍布全国") || content.contains("中国广布")
                || content.contains("中国普遍") || content.contains("全国普遍")
                || content.contains("中国广泛") || content.contains("全国常见")
                || content.contains("栽培于全国") || content.contains("全国各省")
                || content.contains("各省区广泛") || content.contains("全国广泛")
                || content.contains("全国广布") || content.contains("栽培于全中国")
                || content.contains("中国栽培") || content.contains("遍布中国")) {
            // 全取情况
            response.put("statu", "1");
            for (Geoobject geoobject : provinceList) {
                ProvinceVO provinceVO = new ProvinceVO();
                provinceVO.setProvince(geoobject.getCngeoname());
                provinceVO.setAdcode(geoobject.getAdcode());
                provinceVOList.add(provinceVO);
            }
        } else {
            response.put("statu", "1");
            // 查找出现的省份
            for (Geoobject geoobject : provinceList) {
                String shortName = geoobject.getShortName();
                if (content.contains(shortName)) {
                    ProvinceVO provinceVO = new ProvinceVO();
                    provinceVO.setProvince(geoobject.getCngeoname());
                    provinceVO.setAdcode(geoobject.getAdcode());
                    provinceVOList.add(provinceVO);
                } else {
                    // 拉丁名
                    if (content.contains(geoobject.getEngeoname())) {
                        ProvinceVO provinceVO = new ProvinceVO();
                        provinceVO.setProvince(geoobject.getCngeoname());
                        provinceVO.setAdcode(geoobject.getAdcode());
                        provinceVOList.add(provinceVO);
                    }
                }
            }
        }
        response.put("data", provinceVOList);

        return response;
    }


    // 输入整条语句content，和取反部分contentIn
    // 若取反部分包含省名，则返回剩余省份信息；若不包含，返回整条语句其他部分出现的省份
    public List<ProvinceVO> findByContent(String content, String contentIn) {
        // 省份列表
        List<Geoobject> provinceList = geoobjectRepository.findByGeotypeAsProvince();
        // 返回体
        List<ProvinceVO> provinceVOList = new ArrayList<>();
        Boolean flag = false;
        for (int i = 0; i < provinceList.size() - 1; i++) {
            String shortName = provinceList.get(i).getShortName();
            if (contentIn.contains(shortName)) {
                flag = true;
                provinceList.remove(provinceList.get(i));
                i--;
            }
        }

        if (flag) {
            // 括号内容包含省份
            for (Geoobject geoobject : provinceList) {
                ProvinceVO provinceVO = new ProvinceVO();
                provinceVO.setProvince(geoobject.getCngeoname());
                provinceVO.setAdcode(geoobject.getAdcode());
                provinceVOList.add(provinceVO);
            }
            return provinceVOList;
        } else {
            // 括号内容不包含省份
            for (Geoobject geoobject : provinceList) {
                String shortName = geoobject.getShortName();
                if (content.contains(shortName)) {
                    ProvinceVO provinceVO = new ProvinceVO();
                    provinceVO.setProvince(geoobject.getCngeoname());
                    provinceVO.setAdcode(geoobject.getAdcode());
                    provinceVOList.add(provinceVO);
                }
            }
        }
        return provinceVOList;
    }

    /*else if (content.contains("除外")){
            // 取出现的省份
            for (Geoobject geoobject : provinceList) {
                String cngeoname = geoobject.getCngeoname();
                if (content.contains(cngeoname)){
                    ProvinceVO provinceVO = new ProvinceVO();
                    provinceVO.setProvince(geoobject.getCngeoname());
                    provinceVO.setAdcode(geoobject.getAdcode());
                    provinceVOList.add(provinceVO);
                }
            }
            response.put("data",provinceVOList);
            return response;

        }*/
    @Override
    public BaseResults parseCountyData(String content) {
        List<ProvinceVO> provinceVOList = new ArrayList<>();
        List<CountyNewVO> countyNewVOList = new ArrayList<>();
        List<CountyOldVO> countyOldVOList = new ArrayList<>();

        // 1.处理地名+方位名
        if (content.contains("部分")) {
            content = content.replace("部分", " 部分");
        }
        if (content.contains("东部")) {
            content = content.replace("东部", " 东部");
        }
        if (content.contains("西部")) {
            content = content.replace("西部", " 西部");
        }
        if (content.contains("南部")) {
            content = content.replace("南部", " 南部");
        }
        if (content.contains("北部")) {
            content = content.replace("北部", " 北部");
        }
        // 2.处理被网页符号隔开的地名
        if (content.contains("<br>")) {
            content = content.replace("<br>", "");
        }

        // 解析省份
        JSON json = this.parseProvinceData(content);
        JSONObject object1 = JSONObject.parseObject(json.toString());
        JSONArray data = object1.getJSONArray("data");
        for (Object datum : data) {
            ProvinceVO provinceVO = JSON.parseObject(datum.toString(), ProvinceVO.class);
            String adcode = provinceVO.getAdcode();
            // 当前省份下县名列表
            List<Geoobject> countyNewList = geoobjectRepository.findByGeotypeAsCountyAndProAdcodeNew(adcode.substring(0, 2));
            List<Geoobject> countyOldList = geoobjectRepository.findByGeotypeAsCountyAndProAdcodeOld(adcode.substring(0, 2));

            // 新县名
            for (Geoobject geoobject : countyNewList) {
                String cngeoname = geoobject.getCngeoname();
                String shortName = geoobject.getShortName();
                if (content.contains(cngeoname)) {
                    CountyNewVO countyNewVO = new CountyNewVO();
                    countyNewVO.setId(UUIDUtils.getUUID32());
                    countyNewVO.setValueContent(cngeoname);
                    countyNewVO.setProvince(provinceVO.getProvince());
                    countyNewVO.setCounty(cngeoname);
                    countyNewVO.setAdcode(geoobject.getAdcode());

                    // 查询市名
                    Geoobject geoobjectCity = geoobjectRepository.findByAdcode(geoobject.getPid());
                    if ("市".equals(geoobjectCity.getGeotype())) {
                        countyNewVO.setCity(geoobjectCity.getCngeoname());
                    }
                    countyNewVOList.add(countyNewVO);
                } else if (content.contains(shortName)) {
                    CountyNewVO countyNewVO = new CountyNewVO();
                    countyNewVO.setId(UUIDUtils.getUUID32());
                    countyNewVO.setValueContent(shortName);
                    countyNewVO.setProvince(provinceVO.getProvince());
                    countyNewVO.setCounty(cngeoname);
                    countyNewVO.setAdcode(geoobject.getAdcode());

                    // 查询市名
                    Geoobject geoobjectCity = geoobjectRepository.findByAdcode(geoobject.getPid());
                    if ("市".equals(geoobjectCity.getGeotype())) {
                        countyNewVO.setCity(geoobjectCity.getCngeoname());
                    }
                    countyNewVOList.add(countyNewVO);
                } else if (content.contains(geoobject.getEngeoname())) {
                    // 英文县名
                    CountyNewVO countyNewVO = new CountyNewVO();
                    countyNewVO.setId(UUIDUtils.getUUID32());
                    countyNewVO.setValueContent(geoobject.getEngeoname());
                    countyNewVO.setProvince(provinceVO.getProvince());
                    countyNewVO.setCounty(cngeoname);
                    countyNewVO.setAdcode(geoobject.getAdcode());

                    // 查询市名
                    Geoobject geoobjectCity = geoobjectRepository.findByAdcode(geoobject.getPid());
                    if ("市".equals(geoobjectCity.getGeotype())) {
                        countyNewVO.setCity(geoobjectCity.getCngeoname());
                    }
                    countyNewVOList.add(countyNewVO);
                }
            }

            // 旧县名
            if (!CollectionUtils.isEmpty(countyOldList)) {
                for (Geoobject geoobject : countyOldList) {
                    String cngeoname = geoobject.getCngeoname();
                    String shortName = geoobject.getShortName();
                    if (content.contains(cngeoname)) {
                        CountyOldVO countyOldVO = new CountyOldVO();
                        countyOldVO.setId(UUIDUtils.getUUID32());
                        countyOldVO.setValueContent(cngeoname);
                        countyOldVO.setProvince(provinceVO.getProvince());
                        countyOldVO.setOldAdcode(geoobject.getAdcode());
                        countyOldVO.setOldCounty(geoobject.getCngeoname());
                        countyOldVO.setOldVersion(geoobject.getVersion());
                        countyOldVO.setAdcode(geoobject.getRelation());

                        Geoobject newgeoobject = geoobjectRepository.findByAdcode(geoobject.getRelation());
                        countyOldVO.setNewCounty(newgeoobject.getCngeoname());

                        // 查询市名
                        Geoobject geoobjectCity = geoobjectRepository.findByAdcode(newgeoobject.getPid());
                        if ("市".equals(geoobjectCity.getGeotype())) {
                            countyOldVO.setCity(geoobjectCity.getCngeoname());
                        }
                        countyOldVOList.add(countyOldVO);
                    } else if (content.contains(shortName)) {
                        CountyOldVO countyOldVO = new CountyOldVO();
                        countyOldVO.setId(UUIDUtils.getUUID32());
                        countyOldVO.setValueContent(shortName);
                        countyOldVO.setProvince(provinceVO.getProvince());
                        countyOldVO.setOldAdcode(geoobject.getAdcode());
                        countyOldVO.setOldCounty(geoobject.getCngeoname());
                        countyOldVO.setOldVersion(geoobject.getVersion());
                        countyOldVO.setAdcode(geoobject.getRelation());

                        Geoobject newgeoobject = geoobjectRepository.findByAdcode(geoobject.getRelation());
                        countyOldVO.setNewCounty(newgeoobject.getCngeoname());

                        // 查询市名
                        Geoobject geoobjectCity = geoobjectRepository.findByAdcode(newgeoobject.getPid());
                        if ("市".equals(geoobjectCity.getGeotype())) {
                            countyOldVO.setCity(geoobjectCity.getCngeoname());
                        }
                        countyOldVOList.add(countyOldVO);
                    } else if (content.contains(geoobject.getEngeoname())) {
                        // 英文县名
                        CountyOldVO countyOldVO = new CountyOldVO();
                        countyOldVO.setId(UUIDUtils.getUUID32());
                        countyOldVO.setValueContent(geoobject.getEngeoname());
                        countyOldVO.setProvince(provinceVO.getProvince());
                        countyOldVO.setOldAdcode(geoobject.getAdcode());
                        countyOldVO.setOldCounty(geoobject.getCngeoname());
                        countyOldVO.setOldVersion(geoobject.getVersion());
                        countyOldVO.setAdcode(geoobject.getRelation());

                        Geoobject newgeoobject = geoobjectRepository.findByAdcode(geoobject.getRelation());
                        countyOldVO.setNewCounty(newgeoobject.getCngeoname());

                        // 查询市名
                        Geoobject geoobjectCity = geoobjectRepository.findByAdcode(newgeoobject.getPid());
                        if ("市".equals(geoobjectCity.getGeotype())) {
                            countyOldVO.setCity(geoobjectCity.getCngeoname());
                        }
                        countyOldVOList.add(countyOldVO);
                    }
                }
            }
        }

        JSONObject object = new JSONObject();
        object.put("countyNewList", countyNewVOList);
        object.put("countyOldList", countyOldVOList);

        return BaseResultsUtil.success(object);
    }

    @Override
    public BaseResults parseLngAndLat(String province, String city, String county, String locality) {
        // 匹配到的县adcode列表
        Set<String> adcodeSet = new HashSet<>();

        // 省名校正
        List<Geoobject> provinceList = geoobjectRepository.findByGeotypeAsProvince();
        List<Geoobject> pList = new ArrayList<>();
        for (Geoobject geoobject : provinceList) {
            String shortName = geoobject.getShortName();
            if (province.contains(shortName)) {
                pList.add(geoobject);
            }
        }
        if (pList.size() == 0) {
            return BaseResultsUtil.error(500, "未匹配到相应省名");
        } else if (pList.size() > 1) {
            return BaseResultsUtil.error(500, "出现多个省名");
        }
        // 省份确认 size == 1
        Geoobject pGeoobject = pList.get(0);


        // 县名校正

        // 查询该省下所有匹配的县
        List<Geoobject> countyList = geoobjectRepository.findAllCountyByProAndName(pGeoobject.getAdcode().substring(0, 2), county);
        if (countyList.size() == 0) {
            return BaseResultsUtil.error(500, "县未匹配");
        } else if (countyList.size() == 1) {
            // 合适，不用校正

        } else {
            // 匹配到多个县，需去除不符合的
            // 1.消除旧版影响
            for (Geoobject geoobject : countyList) {
                String version = geoobject.getVersion();
                if (version.equals("2020")) {
                    adcodeSet.add(geoobject.getAdcode());
                } else {
                    adcodeSet.add(geoobject.getRelation());
                }
            }

            if (adcodeSet.size() == 1) {
                // 说明找到对应县名，可处理


            } else {
                // 还有影响数据，用市名处理
                if (StringUtils.isBlank(city)) {
                    // 市名为空，解决不了问题
                    return BaseResultsUtil.error(500, "对应多个县名，无法确认");
                } else {
                    // 市名不为空，可用来找出对应县名
                    List<Geoobject> cityList = geoobjectRepository.findNewCityByProAndName(pGeoobject.getAdcode().substring(0, 2), city);
                    if (cityList.size() == 0) {
                        cityList = geoobjectRepository.findOldCityByProAndName(pGeoobject.getAdcode().substring(0, 2), city);
                        if (cityList.size() == 0) {
                            return BaseResultsUtil.error(500, "对应多个县名；且市名未匹配，无法辨别");
                        }
                    }

                    if (cityList.size() == 1) {
                        // 匹配到一个市，可以用来做县的排除
                        Geoobject cityGeoobject = cityList.get(0);
                        if (!"2020".equals(cityGeoobject.getVersion())) {
                            cityGeoobject = geoobjectRepository.findByAdcode(cityGeoobject.getRelation());
                        }
                        String cityAdcode = cityGeoobject.getAdcode();
                        // 合适的县表
                        List<Geoobject> countys = new ArrayList<>();
                        for (String s : adcodeSet) {
                            // 取出对应县
                            Geoobject geoobject = geoobjectRepository.findByAdcode(s);
                            if (cityAdcode.equals(geoobject.getPid())) {
                                countys.add(geoobject);
                            }
                        }

                        if (countys.size() == 0) {
                            return BaseResultsUtil.error(500, "对应多个县名，都与市名不匹配");
                        } else if (countys.size() == 1) {
                            // 通过校正，找到合适结果
                            // 新县名
                            String newCounty = countys.get(0).getCngeoname();
                            // 新市名
                            String newCity = cityGeoobject.getCngeoname();
                            // 高德api经纬度查询语句
                            if (StringUtils.isBlank(locality)){
                                // 小地名为空，用县名查询经纬度

                            }else {

                            }


                        } else {
                            // 市下有多个县合适（几乎不可能）
                            return BaseResultsUtil.error(500, "对应多个县名，都与市名匹配，无法确认唯一的县");
                        }

                    } else {
                        // 匹配到多个市（几乎不可能）
                        return BaseResultsUtil.error(500, "对应多个县；且对应多个市，无法辨别");
                    }


                }

            }

        }
    /*    // 用2020年县全称查询
        List<Geoobject> cNewCnList = geoobjectRepository.findNewCountyByProAndCngeoname(pGeoobject.getAdcode().substring(0, 2), county);
        if (cNewCnList.size() == 0){
            // 通过2020年县全称没查到，用2020年县简称查找
            List<Geoobject> cNewShList = geoobjectRepository.findNewCountyByProAndShortName(pGeoobject.getAdcode().substring(0, 2), county);
            if (cNewShList.size() == 0){
                // 用2020年县简称没查到，用2020年前的县全称查找
                List<Geoobject> cOldList = geoobjectRepository.findOldCountyByProAndCngeoname(pGeoobject.getAdcode().substring(0, 2), county);

            }else if (cNewShList.size() == 1){
                // 符合要求，不需要校正

            }else {

            }

        }else if (cNewCnList.size() == 1){
            // 符合要求，不需要校正

        }else {
            // 匹配到多个县，需要通过市名确认
        }*/


        return null;
    }

  /*  private List<Distribution> formatDisData() {
        Map<String, String> notFindMap = new HashMap<String, String>();
        List<Distributiondata> list = distributiondataRepository.findDistributionList();
        System.out.println("Size：" + list.size());
        List<Distribution> disList = new ArrayList<>();
        try {
            for (Distributiondata distribute : list) {
                String distributionBack = distribute.getDistributionBack();

                Distribution distribution = new Distribution();
                distribution.setRecordId(distribute.getRecordId());
                distribution.setNameCode(distribute.getNameCode());
                distribution.setDistributionBack(distributionBack);

                if (distributionBack.contains("黑龙江西")) {
                    distributionBack = distributionBack.replace("黑龙江西", "黑龙江");
                } else if (distributionBack.contains("浙江西")) {
                    distributionBack = distributionBack.replace("浙江西", "浙江");
                } else if (distributionBack.contains("上海南")) {
                    distributionBack = distributionBack.replace("上海南", "上海");
                } else if (distributionBack.contains("青海南")) {
                    distributionBack = distributionBack.replace("青海南", "青海");
                } else if (distributionBack.contains("黄海南")) {
                    distributionBack = distributionBack.replace("黄海南", "黄海");
                } else if (distributionBack.contains("渤海南")) {
                    distributionBack = distributionBack.replace("渤海南", "渤海");
                } else if (distributionBack.contains("南海南")) {
                    distributionBack = distributionBack.replace("南海南", "南海");
                } else if (distributionBack.contains("东海南")) {
                    distributionBack = distributionBack.replace("东海南", "东海");
                } else if (distributionBack.contains("除") && distributionBack.contains("外")) {
                    String provinceStr1 = "宁夏、江西、山东、湖南、湖北、辽宁、贵州、江苏、青海、黑龙江、河南、安徽、广东、海南、天津、陕西、香港、西藏、河北、浙江、四川、云南、内蒙、新疆、广西、山西、吉林、北京、重庆、澳门、甘肃、台湾、上海、福建";
                    int start = distributionBack.indexOf("除");
                    int end = distributionBack.indexOf("外");
                    //String exceptStr = distributionBack.replace("除", "").replace("外", "").replace("见于各省", "");
                    distributionBack = distributionBack.substring(start, end + 1);


                    String[] exceptArr = distributionBack.split("、");
                    String[] provinceArr = provinceStr1.split("、");
                    for (String province : provinceArr) {
                        for (String except : exceptArr) {
                            if (except.contains(province)) {
                                provinceStr1 = provinceStr1.replace(province, "");
                            }
                        }
                    }
                    distributionBack = provinceStr1;
                } else if (distributionBack.contains("见于各省") || distributionBack.equals("全国")
                        || distributionBack.contains("全国各地") || distributionBack.contains("中国各地")
                        || distributionBack.contains("遍布全国") || distributionBack.contains("中国广布")
                        || distributionBack.contains("中国普遍") || distributionBack.contains("全国普遍")
                        || distributionBack.contains("中国广泛") || distributionBack.contains("全国常见")
                        || distributionBack.contains("栽培于全国") || distributionBack.contains("全国各省")
                        || distributionBack.contains("各省区广泛") || distributionBack.contains("全国广泛")
                        || distributionBack.contains("全国广布") || distributionBack.contains("栽培于全中国")
                        || distributionBack.contains("中国栽培") || distributionBack.contains("遍布中国")) {
                    distributionBack = provinceStr2;
                } else if (distributionBack.equals("中国")) {
                    distributionBack = provinceStr2;
                }
                System.out.println("替换后：" + distributionBack);
                distributionBack = distributionBack.replace("分布", "").trim().
                        replace(" ", "").trim().
                        replace(",", "、").trim().
                        replace("，", "、").trim().
                        replace("\\", "、").trim().
                        replace("//", "、").trim().
                        replace("|", "、").trim().
                        replace("。", "、").trim().
                        replace(".", "、").trim().
                        replace("：", "、").trim().
                        replace(":", "、").trim().
                        replace("？", "").trim().
                        replace("?", "").trim().
                        replace(";", "、").trim();

                String[] areaArr = distributionBack.split("、");
                StringBuffer enbf = new StringBuffer();
                StringBuffer chbf = new StringBuffer();
                StringBuffer level4 = new StringBuffer();
                Set<String> enbfSet = new HashSet<>();
                Set<String> chbfSet = new HashSet<>();
                Set<String> level4Set = new HashSet<>();

                distribute.setDistributionC(distributionBack);
                for (String area : areaArr) {
                    String ename = PinYinUtils.getPinYinOfSea(area);
                    String level = PinYinUtils.getLevel4OfSea(area);
                    String cname = PinYinUtils.getCNameOfProvince(area);
                    if (StringUtils.isNotBlank(ename)) {
                        enbfSet.add(ename);
                    }
                    if (StringUtils.isNotBlank(cname)) {
                        chbfSet.add(cname);
                    }
                    if (StringUtils.isNotBlank(level)) {
                        level4Set.add(level);
                    }
                }
                for (String ename : enbfSet) {
                    enbf.append(ename);
                    enbf.append(",");
                }
                for (String cname : chbfSet) {
                    chbf.append(cname);
                    chbf.append(",");
                }
                for (String level : level4Set) {
                    level4.append(level);
                    level4.append(",");
                }

                if (enbf.length() > 0) {
                    //distribute.setDistribution(enbf.toString().substring(0, enbf.toString().length() - 1));
                    distribution.setDistribution(enbf.toString().substring(0, enbf.toString().length() - 1));
                }
                if (chbf.length() > 0) {
                    //distribute.setDistributionBack(chbf.toString().substring(0, chbf.toString().length() - 1));
                    //distribute.setDistributionC(chbf.toString().substring(0, chbf.toString().length() - 1));
                    distribution.setDistributionC(chbf.toString().substring(0, chbf.toString().length() - 1));
                } else {
                    distribution.setDistributionC(distributionBack);
                }
                if (level4.length() > 0) {
                    //distribute.setLevel4(level4.toString().substring(0, level4.toString().length() - 1));
                    distribution.setLevel4(level4.toString().substring(0, level4.toString().length() - 1));
                }

                disList.add(distribution);
                System.out.println("结果中文：" + distribution.getDistributionC());
                System.out.println("结果英文：" + distribution.getDistribution());
                System.out.println("结果Level：" + distribution.getLevel4());

            }
            Set<Entry<String, String>> entrySet = notFindMap.entrySet();
            for (Entry<String, String> entry : entrySet) {
                System.out.println(entry.getKey());
            }
            return disList;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("空指针异常，无待拷贝数据，查询结果为空");
            return null;
        }
    }*/
}
