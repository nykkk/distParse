package com.example.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.common.*;
import com.example.demo.entity.CountyCentroid;
import com.example.demo.entity.Geoobject;
import com.example.demo.repository.CountyCentroidRepository;
import com.example.demo.repository.GeoobjectRepository;
import com.example.demo.response.CountyNewVO;
import com.example.demo.response.CountyOldVO;
import com.example.demo.response.LngAndLatVO;
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

    @Autowired
    private CountyCentroidRepository countyCentroidRepository;

    @Override
    public JSON parseProvinceData(String content) {

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

        if(content.contains("黑龙江西")){
            content = content.replace("黑龙江西","黑龙江 西");
        }
        if(content.contains("浙江西")){
            content = content.replace("浙江西","浙江 西");
        }
        if(content.contains("上海南")){
            content = content.replace("上海南","上海 南");
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
        for (int i = 0; i < provinceList.size(); i++) {
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
            content = content.replace("南部", " 南 部");
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
        if (CollectionUtils.isEmpty(data)){
            return BaseResultsUtil.error(500,"不包含省份，故无法进行县的解析");
        }
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
    public BaseResults parseCountyDataAll(String content) {
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
            content = content.replace("南部", " 南 部");
        }
        if (content.contains("北部")) {
            content = content.replace("北部", " 北部");
        }

        // 2.处理被网页符号隔开的地名
        if (content.contains("<br>")) {
            content = content.replace("<br>", "");
        }

        // 获取所有省份
        List<Geoobject> proList = geoobjectRepository.findByGeotypeAsProvince();
        for (Geoobject proGeoobject : proList) {
            String proAdcode = proGeoobject.getAdcode();
            String province = proGeoobject.getCngeoname();
            // 当前省份下县名列表
            List<Geoobject> countyNewList = geoobjectRepository.findByGeotypeAsCountyAndProAdcodeNew(proAdcode.substring(0, 2));
            List<Geoobject> countyOldList = geoobjectRepository.findByGeotypeAsCountyAndProAdcodeOld(proAdcode.substring(0, 2));

            // 新县名
            for (Geoobject geoobject : countyNewList) {
                String cngeoname = geoobject.getCngeoname();
                String shortName = geoobject.getShortName();
                if (content.contains(cngeoname)) {
                    CountyNewVO countyNewVO = new CountyNewVO();
                    countyNewVO.setValueContent(cngeoname);
                    countyNewVO.setProvince(province);
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
                    countyNewVO.setValueContent(shortName);
                    countyNewVO.setProvince(province);
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
                    countyNewVO.setValueContent(geoobject.getEngeoname());
                    countyNewVO.setProvince(province);
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
                        countyOldVO.setValueContent(cngeoname);
                        countyOldVO.setProvince(province);
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
                        countyOldVO.setValueContent(shortName);
                        countyOldVO.setProvince(province);
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
                        countyOldVO.setValueContent(geoobject.getEngeoname());
                        countyOldVO.setProvince(province);
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
    public BaseResults parseCountyDataPart(String content, String provinces) {
        // 返回体
        List<CountyNewVO> countyNewVOList = new ArrayList<>();
        List<CountyOldVO> countyOldVOList = new ArrayList<>();
        JSONObject object = new JSONObject();
        // 涉及的省份列表
        List<Geoobject> proList = new ArrayList<>();

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
            content = content.replace("南部", " 南 部");
        }
        if (content.contains("北部")) {
            content = content.replace("北部", " 北部");
        }

        // 2.处理被网页符号隔开的地名
        if (content.contains("<br>")) {
            content = content.replace("<br>", "");
        }

        List<Geoobject> allProList = geoobjectRepository.findByGeotypeAsProvince();
        for (Geoobject proGeoobject : allProList) {
            String shortName = proGeoobject.getShortName();
            if (provinces.contains(shortName)){
                proList.add(proGeoobject);
            }
        }

        if (!CollectionUtils.isEmpty(proList)){
            for (Geoobject proGeoobject : proList) {
                String proAdcode = proGeoobject.getAdcode();
                String province = proGeoobject.getCngeoname();
                // 当前省份下县名列表
                List<Geoobject> countyNewList = geoobjectRepository.findByGeotypeAsCountyAndProAdcodeNew(proAdcode.substring(0, 2));
                List<Geoobject> countyOldList = geoobjectRepository.findByGeotypeAsCountyAndProAdcodeOld(proAdcode.substring(0, 2));

                // 新县名
                for (Geoobject geoobject : countyNewList) {
                    String cngeoname = geoobject.getCngeoname();
                    String shortName = geoobject.getShortName();
                    if (content.contains(cngeoname)) {
                        CountyNewVO countyNewVO = new CountyNewVO();
                        countyNewVO.setValueContent(cngeoname);
                        countyNewVO.setProvince(province);
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
                        countyNewVO.setValueContent(shortName);
                        countyNewVO.setProvince(province);
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
                        countyNewVO.setValueContent(geoobject.getEngeoname());
                        countyNewVO.setProvince(province);
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
                            countyOldVO.setValueContent(cngeoname);
                            countyOldVO.setProvince(province);
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
                            countyOldVO.setValueContent(shortName);
                            countyOldVO.setProvince(province);
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
                            countyOldVO.setValueContent(geoobject.getEngeoname());
                            countyOldVO.setProvince(province);
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
        }else {
            return BaseResultsUtil.error(500,"检测不出省名");
        }

        object.put("countyNewList", countyNewVOList);
        object.put("countyOldList", countyOldVOList);
        return BaseResultsUtil.success(object);
    }


    @Override
    public JSON parseLngAndLat(String province, String city, String county, String locality) {
        // 返回JSON
        JSONObject response = new JSONObject();
        // 匹配到的县adcode列表
        Set<String> adcodeSet = new HashSet<>();
        String countyAdcode = "";
        // 高德api查询地址
        String place = "";
        // 返回经纬度表
        LngAndLatVO lngAndLatVO = new LngAndLatVO();
        // 返回经纬度列表
        List<LngAndLatVO> list = new ArrayList<>();


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
            response.put("msg","未匹配到相应省名");
            response.put("size",0);
            return response;
        } else if (pList.size() > 1) {
            response.put("msg","出现多个省名");
            response.put("size",0);
            return response;
        }
        // 省份确认 size == 1
        Geoobject pGeoobject = pList.get(0);
        province = pGeoobject.getCngeoname();
        lngAndLatVO.setProvince(province);

        // 县名校正

        // 查询该省下所有匹配的县
        List<Geoobject> countyList = geoobjectRepository.findAllCountyByProAndName(pGeoobject.getAdcode().substring(0, 2), county);
        if (countyList.size() == 0) {
            response.put("msg","县未匹配");
            response.put("size",0);
            return response;
        } else if (countyList.size() == 1) {
            // 县名合适，不用校正
            Geoobject cGeoobject = countyList.get(0);
            if (!cGeoobject.getVersion().equals("2020")){
                cGeoobject = geoobjectRepository.findByAdcode(cGeoobject.getRelation());
            }
            lngAndLatVO.setCounty(cGeoobject.getCngeoname());
            lngAndLatVO.setAdcode(cGeoobject.getAdcode());
            Geoobject cityGeoobject = geoobjectRepository.findByAdcode(cGeoobject.getPid());
            if (cityGeoobject.getGeotype().equals("市")){
                lngAndLatVO.setCity(cityGeoobject.getCngeoname());
            }

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
                for (String s : adcodeSet) {
                    countyAdcode = s;
                }
                Geoobject cGeoobject = geoobjectRepository.findByAdcode(countyAdcode);
                lngAndLatVO.setCounty(cGeoobject.getCngeoname());
                lngAndLatVO.setAdcode(cGeoobject.getAdcode());
                Geoobject cityGeoobject = geoobjectRepository.findByAdcode(cGeoobject.getPid());
                if (cityGeoobject.getGeotype().equals("市")){
                    lngAndLatVO.setCity(cityGeoobject.getCngeoname());
                }



            } else {
                // 还有影响数据，用市名处理
                if (StringUtils.isBlank(city)) {
                    // 市名为空，返回列表数据
                    response.put("msg","对应多个县名，且市名为空，返回列表数据");

                    for (String countadcode : adcodeSet) {
                        LngAndLatVO lngAndLatVO1 = BeanHelper.copyProperties(lngAndLatVO, LngAndLatVO.class);

                        // 取出对应县
                        Geoobject countyGeo = geoobjectRepository.findByAdcode(countadcode);
                        lngAndLatVO1.setCounty(countyGeo.getCngeoname());
                        lngAndLatVO1.setAdcode(countadcode);
                        // 取出对应市名
                        Geoobject cityGeoobject = geoobjectRepository.findByAdcode(countyGeo.getPid());
                        if (cityGeoobject.getGeotype().equals("市")) {
                            lngAndLatVO1.setCity(cityGeoobject.getCngeoname());
                        }


                        // 判断小地名是否存在
                        if (StringUtils.isBlank(locality)) {
                            // 小地名为空，先用县adcode查询经纬度对照表
                            List<CountyCentroid> ccList = countyCentroidRepository.findByAdcode(countadcode);
                            if (ccList.size() == 1) {
                                // 数据匹配
                                CountyCentroid countyCentroid = ccList.get(0);
                                lngAndLatVO1.setLng(countyCentroid.getWgs84X());
                                lngAndLatVO1.setLat(countyCentroid.getWgs84Y());
                                list.add(lngAndLatVO1);
                                continue;
                            } else {
                                // 没匹配到数据或者对应多个结果，可调用高德api
                                if (StringUtils.isBlank(lngAndLatVO1.getCity())) {
                                    place = lngAndLatVO1.getProvince() + lngAndLatVO1.getCounty();
                                } else {
                                    place = lngAndLatVO1.getProvince() + lngAndLatVO1.getCity() + lngAndLatVO1.getCounty();
                                }

                            }
                        } else {
                            // 小地名不为空,拼接api查询地址
                            if (StringUtils.isBlank(lngAndLatVO1.getCity())){
                                place = lngAndLatVO1.getProvince() + lngAndLatVO1.getCounty() + locality;
                            }else {
                                place = lngAndLatVO1.getProvince()+lngAndLatVO1.getCity()+lngAndLatVO1.getCounty()+locality;
                            }
                        }

                        if(!StringUtils.isBlank(place)){
                            // 调用高德api
                            try {
                                Map<String, String> parameters = new HashMap<String, String>();
                                parameters.put("key", "3dbaea5ec9bd068f52b52ef58b8b1e5c");
                                parameters.put("address", place);
                                String data = HttpUtil.sendGet("https://restapi.amap.com/v3/geocode/geo", parameters);
                                JSONObject result = JSONObject.parseObject(data);
                                String status = result.getString("status");
                                if ("1".equals(status)) {
                                    JSONArray geocodes = result.getJSONArray("geocodes");
                                    if (geocodes.size() == 1) {
                                        JSONObject object = (JSONObject) geocodes.get(0);
                                        String formatted_address = object.getString("formatted_address");
                                        lngAndLatVO1.setApiaddresss(formatted_address);
                                        String location = object.getString("location");
                                        String[] split = location.split(",");
                                        lngAndLatVO1.setLng(Double.parseDouble(split[0]));
                                        lngAndLatVO1.setLat(Double.parseDouble(split[1]));
                                        list.add(lngAndLatVO1);
                                    }

                                }

                            } catch (Exception e) {
                                response.put("size", 0);
                                response.put("msg", "对应多个县名，且市名为空，返回列表数据 - 高德api查询异常");
                                return response;
                            }
                        }

                    }

                    response.put("size",list.size());
                    response.put("data",list);
                    return response;
                } else {
                    // 市名不为空，可用来找出对应县名
                    List<Geoobject> cityList = geoobjectRepository.findNewCityByProAndName(pGeoobject.getAdcode().substring(0, 2), city);
                    if (cityList.size() == 0) {
                        cityList = geoobjectRepository.findOldCityByProAndName(pGeoobject.getAdcode().substring(0, 2), city);
                        if (cityList.size() == 0) {
                            response.put("size",0);
                            response.put("msg","对应多个县名；且市名未匹配，无法辨别");
                            return response;
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
                            response.put("size",0);
                            response.put("msg","对应多个县名，都与市名不匹配");
                            return response;
                        } else if (countys.size() == 1) {
                            // 通过校正，找到合适结果
                            // 新县名
                            String newCounty = countys.get(0).getCngeoname();
                            lngAndLatVO.setCounty(newCounty);
                            // 新市名
                            String newCity = cityGeoobject.getCngeoname();
                            lngAndLatVO.setCity(newCity);
                            lngAndLatVO.setAdcode(countys.get(0).getAdcode());


                        } else {
                            // 市下有多个县合适（几乎不可能）
                            response.put("size",0);
                            response.put("msg","对应多个县名，都与市名匹配，无法确认唯一的县");
                            return response;
                        }
                    } else {
                        // 匹配到多个市（几乎不可能）
                        response.put("size",0);
                        response.put("msg","对应多个县；且对应多个市，无法辨别");
                        return response;
                    }


                }

            }

        }
        if (!StringUtils.isBlank(lngAndLatVO.getAdcode())){
            // 判断小地名是否存在
            if (StringUtils.isBlank(locality)){
                // 小地名为空，先用县adcode查询经纬度对照表
                String adcode = lngAndLatVO.getAdcode();
                List<CountyCentroid> ccList = countyCentroidRepository.findByAdcode(adcode);
                if (ccList.size() == 1){
                    // 数据匹配
                    CountyCentroid countyCentroid = ccList.get(0);
                    lngAndLatVO.setLng(countyCentroid.getWgs84X());
                    lngAndLatVO.setLat(countyCentroid.getWgs84Y());
                    list.add(lngAndLatVO);
                    response.put("size",list.size());
                    response.put("data",list);
                    response.put("msg","获取经纬度成功");
                    return response;
                }else {
                    // 没匹配到数据或者对应多个结果，可调用高德api
                    if (StringUtils.isBlank(lngAndLatVO.getCity())){
                        place = lngAndLatVO.getProvince() + lngAndLatVO.getCounty();
                    }else {
                        place = lngAndLatVO.getProvince()+lngAndLatVO.getCity()+lngAndLatVO.getCounty();
                    }
                }
            }else {
                if (StringUtils.isBlank(lngAndLatVO.getCity())){
                    place = lngAndLatVO.getProvince() + lngAndLatVO.getCounty() + locality;
                }else {
                    place = lngAndLatVO.getProvince()+lngAndLatVO.getCity()+lngAndLatVO.getCounty()+locality;
                }
            }
        }

        if (!StringUtils.isBlank(place)){
            // 调用高德api
            try {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("key", "3dbaea5ec9bd068f52b52ef58b8b1e5c");
                parameters.put("address", place);
                String data = HttpUtil.sendGet("https://restapi.amap.com/v3/geocode/geo", parameters);
                JSONObject result = JSONObject.parseObject(data);
                String status = result.getString("status");
                if ("1".equals(status)) {
                    JSONArray geocodes = result.getJSONArray("geocodes");
                    if (geocodes.size() == 1){
                        JSONObject object = (JSONObject)geocodes.get(0);
                        String formatted_address = object.getString("formatted_address");
                        lngAndLatVO.setApiaddresss(formatted_address);
                        String location = object.getString("location");
                        String[] split = location.split(",");
                        lngAndLatVO.setLng(Double.parseDouble(split[0]));
                        lngAndLatVO.setLat(Double.parseDouble(split[1]));
                        list.add(lngAndLatVO);
                        response.put("size",list.size());
                        response.put("data",list);
                        response.put("msg","获取经纬度成功");
                        return response;
                    }

                }else {
                    response.put("msg",result.getString("info"));
                    response.put("size",0);
                    return response;
                }

            }catch (Exception e){
                response.put("msg",e.toString());
                response.put("size",0);
                return response;
            }
        }
        response.put("msg","未知错误");
        response.put("size",0);
        return response;
    }

    @Override
    public BaseResults parseLngAndLatFromData(String content) {
        BaseResults baseResults = this.parseCountyData(content);
        // 返回列表
        List<LngAndLatVO> rList = new ArrayList<>();
        List<LngAndLatVO> list = new ArrayList<>();
        // adcode合集
        String allAdcode = "";
        // 高德api查询语句
        String place = "";
        Integer code = baseResults.getCode();
        if (code != 200){
            return BaseResultsUtil.error(500,baseResults.getMessage());
        }
        JSONObject data = (JSONObject) baseResults.getData();
        JSONArray countyNewList = data.getJSONArray("countyNewList");
        JSONArray countyOldList = data.getJSONArray("countyOldList");
        if (!CollectionUtils.isEmpty(countyNewList)){
            // 新县表有值
            for (Object o : countyNewList) {
                CountyNewVO countyNewVO = (CountyNewVO) o;
                allAdcode =  allAdcode + countyNewVO.getAdcode() + ", ";
                LngAndLatVO lngAndLatVO = new LngAndLatVO();
                lngAndLatVO.setProvince(countyNewVO.getProvince());
                lngAndLatVO.setCity(countyNewVO.getCity());
                lngAndLatVO.setCounty(countyNewVO.getCounty());
                lngAndLatVO.setAdcode(countyNewVO.getAdcode());
                list.add(lngAndLatVO);
            }
        }

        if (!CollectionUtils.isEmpty(countyOldList)){
            // 旧县表有值
            for (Object o : countyOldList) {
                CountyOldVO countyOldVO = (CountyOldVO) o;
                // 通过adcode判断县名是否重复，只用处理重复的县名
                if (!allAdcode.contains(countyOldVO.getAdcode())){
                    allAdcode =  allAdcode + countyOldVO.getAdcode() + ", ";
                    LngAndLatVO lngAndLatVO = new LngAndLatVO();
                    lngAndLatVO.setProvince(countyOldVO.getProvince());
                    lngAndLatVO.setCity(countyOldVO.getCity());
                    lngAndLatVO.setCounty(countyOldVO.getNewCounty());
                    lngAndLatVO.setAdcode(countyOldVO.getAdcode());
                    list.add(lngAndLatVO);
                }
            }
        }

        if (!CollectionUtils.isEmpty(list)){
            for (LngAndLatVO lngAndLatVO : list) {
                // 没有小地名，可先查县级经纬度对照表
                String adcode = lngAndLatVO.getAdcode();
                List<CountyCentroid> ccList = countyCentroidRepository.findByAdcode(adcode);
                if (ccList.size() == 1){
                    // 数据匹配
                    CountyCentroid countyCentroid = ccList.get(0);
                    lngAndLatVO.setLng(countyCentroid.getWgs84X());
                    lngAndLatVO.setLat(countyCentroid.getWgs84Y());
                    rList.add(lngAndLatVO);
                }else {
                    // 没匹配到数据或者对应多个结果，可调用高德api
                    if (StringUtils.isBlank(lngAndLatVO.getCity())){
                        place = lngAndLatVO.getProvince() + lngAndLatVO.getCounty();
                    }else {
                        place = lngAndLatVO.getProvince()+lngAndLatVO.getCity()+lngAndLatVO.getCounty();
                    }
                    // 调用高德api
                    try {
                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("key", "3dbaea5ec9bd068f52b52ef58b8b1e5c");
                        parameters.put("address", place);
                        String data1 = HttpUtil.sendGet("https://restapi.amap.com/v3/geocode/geo", parameters);
                        JSONObject result = JSONObject.parseObject(data1);
                        String status = result.getString("status");
                        if ("1".equals(status)) {
                            JSONArray geocodes = result.getJSONArray("geocodes");
                            if (geocodes.size() == 1){
                                JSONObject object = (JSONObject)geocodes.get(0);
                                String formatted_address = object.getString("formatted_address");
                                lngAndLatVO.setApiaddresss(formatted_address);
                                String location = object.getString("location");
                                String[] split = location.split(",");
                                lngAndLatVO.setLng(Double.parseDouble(split[0]));
                                lngAndLatVO.setLat(Double.parseDouble(split[1]));
                                rList.add(lngAndLatVO);
                            }

                        }

                    }catch (Exception e){
                        return BaseResultsUtil.error(500,e.toString());
                    }
                }
            }
        }

        return BaseResultsUtil.success(rList);
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
