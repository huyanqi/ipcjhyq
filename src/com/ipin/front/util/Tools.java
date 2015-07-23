package com.ipin.front.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.ipin.front.model.DistrictModel;
import com.ipin.front.model.MatchModel;

public class Tools {

	public static String matchModelIsWhole(MatchModel model){
		if(model.fromDistrict == null || model.fromDistrict.city == null || model.fromDistrict.city.id == 0 || model.fromDistrict.city.province == null || model.fromDistrict.city.province.id == 0){
			return "请选择出发地";
		}
		if(model.toDistrict == null || model.toDistrict.city == null || model.toDistrict.city.id == 0 || model.toDistrict.city.province == null || model.toDistrict.city.province.id == 0){
			return "请选择目的地";
		}if(model.user == null){
			return "请填写联系人信息";
		}
		if(model.time == null || "".equals(model.time)){
			return "请提供意向出发时间";
		}
		if(model.type == 1 && model.people == 0){
			return "请提供剩余可载人数";
		}
		return "";
	}
	
	/**
     * 用SHA1算法生成安全签名
     * @param token 票据
     * @param timestamp 时间戳
     * @param nonce 随机字符串
     * @param encrypt 密文
     * @return 安全签名
     * @throws NoSuchAlgorithmException 
     * @throws AesException 
     */
    public static String getSHA1(String token, String timestamp, String nonce) throws NoSuchAlgorithmException  {
            String[] array = new String[] { token, timestamp, nonce };
            StringBuffer sb = new StringBuffer();
            // 字符串排序
            Arrays.sort(array);
            for (int i = 0; i < 3; i++) {
                sb.append(array[i]);
            }
            String str = sb.toString();
            // SHA1签名生成
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
     * 将time装配成年月日
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
    
}
