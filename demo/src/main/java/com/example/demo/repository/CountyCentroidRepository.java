package com.example.demo.repository;

import com.example.demo.entity.CountyCentroid;
import com.example.demo.repository.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p><b>CountyCentroidRepository</b></p>
 * <p> </p>
 * <p>Copyright: The Research Group of Biodiversity Informatics (BiodInfo Group) - 中国科学院动物研究所生物多样性信息学研究组</p>
 *
 * @Author NY
 * @Date: 2022/8/5 10:00
 * @Version V1.0
 * @since JDK 1.8.0_162
 */
@Repository
public interface CountyCentroidRepository extends BaseRepository<CountyCentroid,String> {

    @Query(value = "select c from CountyCentroid c where c.adcode = ?1")
    List<CountyCentroid> findByAdcode(String adcode);
}
