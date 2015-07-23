package com.ipin.front.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * @author Frankie
 *
 * ³ÇÊÐ
 */
@Entity
@Table(name = "city")
public class CityModel implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer id;
	
	@Column
	public String name;
	
	@ManyToOne
	@JoinColumn(name="province")
	public ProvinceModel province;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ProvinceModel getProvince() {
		return province;
	}

	public void setProvince(ProvinceModel province) {
		this.province = province;
	}

}
