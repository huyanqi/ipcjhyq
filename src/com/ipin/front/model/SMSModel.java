package com.ipin.front.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Frankie
 *
 * ���ż�¼
 */
@Entity
@Table(name = "sms_model")
public class SMSModel {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer id;
	
	@Column
	public String content;	//��������
	
	@Column
	public String statusCode;	//״̬��
	
	@Column
	public String statusMsg;//״̬��������
	
	@Column
	public String mobile;	//���ն��ŵ��ֻ�����

	@Column
	public Date timestamp = new Date();
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
}
