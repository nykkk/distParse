package com.example.demo.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *<p><b>Geoobject的Entity类</b></p>
 *<p> Geoobject的Entity类</p>
 * @author BINZI
 *<p>Created date: 2018/4/8 17:35</p>
 *<p>Copyright: The Research Group of Biodiversity Informatics (BiodInfo Group) - 中国科学院动物研究所生物多样性信息学研究组</p>
 * @version: 0.1
 * @since JDK 1.80_144
 */
@Entity
@Table(name = "geoobject")
@Data
public class Geoobject implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	private double centerx;

	private double centery;

	private String cngeoname;

	private String engeoname;
	private String geodata;

	private String geotype;

	private String inputer;

	@Temporal(TemporalType.TIMESTAMP)
	private Date inputtime;

	private String pid;

	private String relation;

	private int status;

	@Temporal(TemporalType.TIMESTAMP)
	private Date synchdate;

	private int synchstatus;

	private String version;

	private String adcode;

	private String citycode;

	@Column(name = "shortname")
	private String shortName;

}