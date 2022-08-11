package com.example.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.common.BaseResults;
import com.example.demo.entity.Geoobject;
import com.example.demo.repository.GeoobjectRepository;
import com.example.demo.response.ProvinceVO;
import com.example.demo.service.ParseService;
import com.example.demo.service.impl.ParseServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
/*        String s = "国内分布：产于安徽南部（歙县、休宁、祁门）、浙江南部和西部（龙泉、遂昌、丽水、泰顺、平阳、西天目山）、江西、福建、湖南（宁远、长沙、宜章、雪峰山、新宁、汝桂、酃县、东安、莽山、城步）、广东、广西（西部山区除外）、贵州（黎平）等地区";
        String[] split = s.split("除外）");
        String s1 = split[0];
        String[] split1 = s1.split("（");
        System.out.println(split1[split1.length -1]);*/
        String s = "3dfafdagag3";
        int d = s.indexOf("d");
        String substring = s.substring(d);
        System.out.println(substring);
        if (substring.contains("A")){
            System.out.println("4");
        }

    }

    @Autowired
    private ParseService parseService;

    @Autowired
    private GeoobjectRepository geoobjectRepository;

    @Test
    public void dd(){
        String s = "喜马拉雅山东部，浙江西部";
        JSON json = parseService.parseProvinceData(s);
        JSONObject object = JSONObject.parseObject(json.toString());
        JSONArray data = object.getJSONArray("data");
        System.out.println(object);
        for (Object datum : data) {
            ProvinceVO provinceVO = JSON.parseObject(datum.toString(), ProvinceVO.class);
            System.out.println(provinceVO);
        }
    }

    @Test
    public void dd1(){
        String s = "福建";
        System.out.println(s.contains("福建"));
       /* BaseResults baseResults = parseService.parseCountyData(s);
        System.out.println(baseResults.getData());*/
    }

    @Test
    public void dd2(){
        Set<Geoobject> objects = new HashSet<>();
        Geoobject byAdcode = geoobjectRepository.findByAdcode("340000");
        Geoobject byAdcode1 = geoobjectRepository.findByAdcode("340000");
        objects.add(byAdcode1);
        objects.add(byAdcode);
        System.out.println(objects.size());
        System.out.println(objects);

    }

    @Test
    public void de3(){
        // JSON json = parseService.parseLngAndLat("河北", "", "新华", "");
        //JSON json = parseService.parseLngAndLat("河北", "", "新华", "河北政法学院");
        //JSON json = parseService.parseLngAndLat("河北", "沧州", "新华", "");
        // JSON json = parseService.parseLngAndLat("河北", "石家庄", "新华", "");
         //JSON json = parseService.parseLngAndLat("河北", "唐山市", "新华", "");
        // JSON json = parseService.parseLngAndLat("河北", "石家庄", "无为", "");
        // JSON json = parseService.parseLngAndLat("河北", "石家庄", "无为", "");
         JSON json = parseService.parseLngAndLat("河r", "石家庄", "无为", "");
        //JSON json = parseService.parseLngAndLat("河北", "", "无为", "");
        System.out.println(json);

    }

    @Test
    public void de4(){
        BaseResults baseResults = parseService.parseLngAndLatFromData("国内分布：产于安徽南部（歙县、休宁、祁门）、浙江南部和西部（龙泉、遂昌、丽水、泰顺、平阳、西天目山）、江西、福建、湖南（宁远、长沙、宜章、雪峰山、新宁、汝桂、酃县、东安、莽山、城步）、广东、广西（西部山区除外）、贵州（黎平）等地区");
        System.out.println(baseResults.toString());
    }

    @Test
    public void de5(){
        // 县地名解析，无视省份
        BaseResults baseResults = parseService.parseCountyDataAll("西昌,会东");
        System.out.println(baseResults);
    }

    @Test
    public void de6(){
        // 县地名解析，只解析当前省份下的县
        BaseResults baseResults = parseService.parseCountyDataPart("安徽南部（歙县、休宁、祁门）、浙江南部和西部（龙泉、遂昌、丽水、泰顺、平阳、西天目山）、江西、福建、湖南（宁远、长沙、宜章、雪峰山、新宁、汝桂、酃县、东安、莽山、城步）、广东、广西（西部山区除外）、贵州（黎平）等地区", "浙江");
        System.out.println(baseResults);
    }
}
