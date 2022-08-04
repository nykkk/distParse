package com.example.demo.common;

/**
 * <p><b>BaseResultsUtil</b></p>
 * <p> </p>
 * <p>Copyright: The Research Group of Biodiversity Informatics (BiodInfo Group) - 中国科学院动物研究所生物多样性信息学研究组</p>
 *
 * @Author NY
 * @Date: 2020/5/27 9:54
 * @Version V1.0
 * @since JDK 1.8.0_162
 */
public class BaseResultsUtil {

    /**
     * 成功且带数据
     **/
    public static BaseResults success(Object object) {
        BaseResults BaseResult = new BaseResults();
        BaseResult.setCode(BaseResultsEnum.SUCCESS.getCode());
        BaseResult.setMessage(BaseResultsEnum.SUCCESS.getMessage());
        BaseResult.setData(object);
        return BaseResult;
    }

    /**
     * 成功但不带数据
     **/
    public static BaseResults success() {
        String[] zero = new String[0];
        return success(zero);
    }

    /**
     * 失败
     **/
    public static BaseResults error(Integer code, String msg) {
        BaseResults BaseResult = new BaseResults();
        String[] zero = new String[0];
        BaseResult.setCode(code);
        BaseResult.setMessage(msg);
        BaseResult.setData(zero);
        return BaseResult;
    }

}
