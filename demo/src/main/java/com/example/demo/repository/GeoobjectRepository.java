package com.example.demo.repository;

import com.example.demo.entity.Geoobject;
import com.example.demo.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p><b>GeoobjectRepository</b></p>
 * <p> </p>
 * <p>Copyright: The Research Group of Biodiversity Informatics (BiodInfo Group) - 中国科学院动物研究所生物多样性信息学研究组</p>
 *
 * @Author NY
 * @Date: 2022/8/1 14:19
 * @Version V1.0
 * @since JDK 1.8.0_162
 */
@Repository
public interface GeoobjectRepository extends BaseRepository<Geoobject,String > {

    /**
     * <p><b>省份列表<b><p>
     * <p><p>
     * @Author: Ny
     * @date  2022/8/1
     */
    @Query(value = "select g from Geoobject g where g.geotype = '省'")
    List<Geoobject> findByGeotypeAsProvince();

    /**
     * <p><b>县名列表<b><p>
     * <p>在对应省份下的所有县（2020年新版）<p>
     * @Author: Ny
     * @date  2022/8/1
     */
    @Query(value = "select g from Geoobject g where g.geotype = '县' and g.adcode like ?1% and g.version = '2020'")
    List<Geoobject> findByGeotypeAsCountyAndProAdcodeNew(String proadcode);

    /**
     * <p><b>县名列表<b><p>
     * <p>在对应省份下的所有县（老版）<p>
     * @Author: Ny
     * @date  2022/8/1
     */
    @Query(value = "select g from Geoobject g where g.geotype = '县' and g.adcode like ?1% and g.version < '2020'")
    List<Geoobject> findByGeotypeAsCountyAndProAdcodeOld(String proadcode);

    /**
     * <p><b>查找最新数据<b><p>
     * <p><p>
     * @Author: Ny
     * @date  2022/8/2
     */
    @Query(value = "select g from Geoobject g where g.adcode = ?1 and g.version = '2020'")
    Geoobject findByAdcode(String adcode);

    /**
     * <p><b>查找县名数据（2020年新版）<b><p>
     * <p><p>
     * @Author: Ny
     * @date  2022/8/4
     */
    @Query(value = "select g from Geoobject g where  g.geotype = '县' and g.adcode like ?1% and g.version = '2020' and g.cngeoname = ?2")
    List<Geoobject> findNewCountyByProAndCngeoname(String proAdcode,String name);



    /**
     * <p><b>查找县名数据（2020年新版）<b><p>
     * <p><p>
     * @Author: Ny
     * @date  2022/8/4
     */
    @Query(value = "select g from Geoobject g where  g.geotype = '县' and g.adcode like ?1% and g.version = '2020' and g.shortName = ?2")
    List<Geoobject> findNewCountyByProAndShortName(String proAdcode,String name);

    /**
     * <p><b>查找县名数据（旧版）<b><p>
     * <p><p>
     * @Author: Ny
     * @date  2022/8/4
     */
    @Query(value = "select g from Geoobject g where  g.geotype = '县' and g.adcode like ?1% and g.version < '2020' and g.cngeoname = ?2")
    List<Geoobject> findOldCountyByProAndCngeoname(String proAdcode,String name);

    /**
     * <p><b>查找县名数据（旧版）<b><p>
     * <p><p>
     * @Author: Ny
     * @date  2022/8/4
     */
    @Query(value = "select g from Geoobject g where  g.geotype = '县' and g.adcode like ?1% and g.version < '2020' and g.shortName = ?2")
    List<Geoobject> findOldCountyByProAndShortName(String proAdcode,String name);

    /**
     * <p><b>查找县名数据（全部）<b><p>
     * <p><p>
     * @Author: Ny
     * @date  2022/8/4
     */
    @Query(value = "select g from Geoobject g where  g.geotype = '县' and g.adcode like ?1% and (g.shortName = ?2 or g.cngeoname = ?2)")
    List<Geoobject> findAllCountyByProAndName(String proAdcode,String name);

    /**
     * <p><b>查找市名数据（新版）<b><p>
     * <p><p>
     * @Author: Ny
     * @date  2022/8/4
     */
    @Query(value = "select g from Geoobject g where g.geotype = '市' and g.adcode like ?1% and g.version = '2020' and (g.shortName = ?2 or g.cngeoname = ?2)")
    List<Geoobject> findNewCityByProAndName(String proAdcode,String name);

    /**
     * <p><b>查找市名数据（旧版）<b><p>
     * <p><p>
     * @Author: Ny
     * @date  2022/8/4
     */
    @Query(value = "select g from Geoobject g where g.geotype = '市' and g.version < '2020' and g.adcode like ?1% and (g.shortName = ?2 or g.cngeoname = ?2)")
    List<Geoobject> findOldCityByProAndName(String proAdcode,String name);
}
