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
 * 匹配模型
 */
@Entity
@Table(name = "match_table")
public class MatchModel implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name="fromDistrict")
	public DistrictModel fromDistrict;	//出发区域
	
	@Column
	public String fromAddress;			//出发详细地址
	
	@ManyToOne
	@JoinColumn(name="toDistrict")
	public DistrictModel toDistrict;	//到达区域
	
	@Column
	public String toAddress;			//到达详细地址
	
	@ManyToOne
	@JoinColumn(name="user")
	public UserModel user;				//联系人
	
	@ManyToOne
	@JoinColumn(name="temp_user")
	public TempUser temp_user;			//临时用户
	
	@Column
	public int type;					//0:人找车 1:车找人
	
	@Column
	public String time;					//出发大概时间 匹配时会提前/延迟半小时筛选
	
	@Column
	public String car;				//汽车型号
	
	@Column
	public int people;				//剩余可乘人数
	
	@Column
	public int price = 0;				//价格

	/****时间*****/
	@Column
	public int year;				//年
	@Column
	public int month;				//月
	@Column
	public int date;				//日
	@Column
	public int hour;				//时，24小时制
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public DistrictModel getFromDistrict() {
		return fromDistrict;
	}

	public void setFromDistrict(DistrictModel fromDistrict) {
		this.fromDistrict = fromDistrict;
	}

	public DistrictModel getToDistrict() {
		return toDistrict;
	}

	public void setToDistrict(DistrictModel toDistrict) {
		this.toDistrict = toDistrict;
	}

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getCar() {
		return car;
	}

	public void setCar(String car) {
		this.car = car;
	}

	public int getPeople() {
		return people;
	}

	public void setPeople(int people) {
		this.people = people;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDate() {
		return date;
	}

	public void setDate(int date) {
		this.date = date;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public TempUser getTemp_user() {
		return temp_user;
	}

	public void setTemp_user(TempUser temp_user) {
		this.temp_user = temp_user;
	}

}
