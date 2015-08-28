package com.ipin.front.model;

import java.io.Serializable;
import java.util.Date;

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
 * ƥ��ģ��
 */
@Entity
@Table(name = "match_table")
public class MatchModel implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer id;
	
	@ManyToOne
	@JoinColumn(name="fromDistrict")
	public DistrictModel fromDistrict;	//��������
	
	@Column
	public String fromAddress;			//������ϸ��ַ
	
	@ManyToOne
	@JoinColumn(name="toDistrict")
	public DistrictModel toDistrict;	//��������
	
	@Column
	public String toAddress;			//������ϸ��ַ
	
	@ManyToOne
	@JoinColumn(name="user")
	public UserModel user;				//��ϵ��
	
	@ManyToOne
	@JoinColumn(name="temp_user")
	public TempUser temp_user;			//��ʱ�û�
	
	@Column
	public int type;					//0:���ҳ� 1:������
	
	@Column
	public String time;					//�������ʱ�� ƥ��ʱ����ǰ/�ӳٰ�Сʱɸѡ
	
	@Column
	public String car;				//�����ͺ�
	
	@Column
	public int people;				//ʣ��ɳ�����
	
	@Column
	public int price = 0;				//�۸�
	
	@Column
	public int msgcount = 0;		//�ѷ����Ŵ���

	/****ʱ��*****/
	@Column
	public int year;				//��
	@Column
	public int month;				//��
	@Column
	public int date;				//��
	@Column
	public int hour;				//ʱ��24Сʱ��
	
	@Column
	public Date update_time = new Date();		//¼��ʱ��
	
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

	public Date getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}

	public int getMsgcount() {
		return msgcount;
	}

	public void setMsgcount(int msgcount) {
		this.msgcount = msgcount;
	}

}
