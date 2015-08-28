package com.ipin.front.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.ipin.front.model.DistrictModel;
import com.ipin.front.model.MatchModel;

public class Tools {

	public static String matchModelIsWhole(MatchModel model){
		if(model.fromDistrict == null || model.fromDistrict.city == null || model.fromDistrict.city.id == 0 || model.fromDistrict.city.province == null || model.fromDistrict.city.province.id == 0){
			return "��ѡ�������";
		}
		if(model.toDistrict == null || model.toDistrict.city == null || model.toDistrict.city.id == 0 || model.toDistrict.city.province == null || model.toDistrict.city.province.id == 0){
			return "��ѡ��Ŀ�ĵ�";
		}
		if(model.fromDistrict.id .equals( model.toDistrict.id)){
			return "�����ز�����ͬһ�����е�ͬһ������";
		}
		if(model.user == null){
			return "����д��ϵ����Ϣ";
		}
		if(model.time == null || "".equals(model.time)){
			return "���ṩ�������ʱ��";
		}
		if(model.type == 1 && model.people == 0){
			return "���ṩʣ���������";
		}
		if(model.user == null && model.temp_user == null){
			return "���ṩ��ϵ����Ϣ";
		}
		if(model.user != null && (model.user.mobile == null || "".equals(model.user.mobile))){
			return "���ṩ��ϵ���ֻ���";
		}
		if(model.user != null && model.user.mobile.length() != 11){
			return "����д������11λ�ֻ���";
		}
		if(model.temp_user != null && model.temp_user.mobile.length() != 11){
			return "����д������11λ�ֻ���";
		}
		return "";
	}
	
	/**
     * ��SHA1�㷨���ɰ�ȫǩ��
     * @param token Ʊ��
     * @param timestamp ʱ���
     * @param nonce ����ַ���
     * @param encrypt ����
     * @return ��ȫǩ��
     * @throws NoSuchAlgorithmException 
     * @throws AesException 
     */
    public static String getSHA1(String token, String timestamp, String nonce) throws NoSuchAlgorithmException  {
            String[] array = new String[] { token, timestamp, nonce };
            StringBuffer sb = new StringBuffer();
            // �ַ�������
            Arrays.sort(array);
            for (int i = 0; i < 3; i++) {
                sb.append(array[i]);
            }
            String str = sb.toString();
            // SHA1ǩ������
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            byte[] digest = md.digest();
 
            StringBuffer hexstr = new StringBuffer();
            String shaHex = "";
            for (int i = 0; i < digest.length; i++) {
                shaHex = Integer.toHexString(digest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexstr.append(0);
                }
                hexstr.append(shaHex);
            }
            return hexstr.toString();
    }
    
    /**
     * ��timeװ���������
     * @param model
     */
    public static void parseTime2YTD(MatchModel model){
    	String[] baseDateArray = model.time.split(" ")[0].split("/");
    	model.year = Integer.parseInt(baseDateArray[0]);
    	model.month = Integer.parseInt(baseDateArray[1]);
    	model.date = Integer.parseInt(baseDateArray[2]);
    	baseDateArray = model.time.split(" ")[1].split(":");
    	model.hour = Integer.parseInt(baseDateArray[0]);
    }
    
    public static String getAddress(DistrictModel model,String address){
    	String city = "";
    	String district = "";
    	if(model.city != null)
    		city = model.city.name;
    	district = model.name;
    	return city + " " + district + " " +address;
    }
    
    public static String ary2Str(String[] datas){
		StringBuffer sb = new StringBuffer();
		for(String data : datas){
			sb.append(data+",");
		}
		if(sb.length() > 0)
			return sb.substring(0,sb.length()-1);
		return "";
	}
    
}
