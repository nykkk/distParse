package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p><b>CountyCentroid</b></p>
 * <p> </p>
 * <p>Copyright: The Research Group of Biodiversity Informatics (BiodInfo Group) - 中国科学院动物研究所生物多样性信息学研究组</p>
 *
 * @Author NY
 * @Date: 2022/5/13 11:28
 * @Version V1.0
 * @since JDK 1.8.0_162
 */
@Entity
@Table(name = "county_centroid")
public class CountyCentroid {

    private static final long serialVersionUID = 1L;

    @Id
    private String adcode;

    @Column(name = "wgs84_x")
    private Double wgs84X;

    @Column(name = "wgs84_y")
    private Double wgs84Y;

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public Double getWgs84X() {
        return wgs84X;
    }

    public void setWgs84X(Double wgs84X) {
        this.wgs84X = wgs84X;
    }

    public Double getWgs84Y() {
        return wgs84Y;
    }

    public void setWgs84Y(Double wgs84Y) {
        this.wgs84Y = wgs84Y;
    }
}
