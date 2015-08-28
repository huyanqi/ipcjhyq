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
 * «¯”Ú
 */
@Entity
@Table(name = "district")
public class DistrictModel implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer id;
	
	@Column
	public String name;
	
	@ManyToOne
	@JoinColumn(name="city")
	public CityModel city;
	
	@Column
	public int weight;//»®÷ÿ

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

	public CityModel getCity() {
		return city;
	}

	public void setCity(CityModel city) {
		this.city = city;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

}
