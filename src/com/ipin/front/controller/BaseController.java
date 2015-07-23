package com.ipin.front.controller;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ipin.front.model.CityModel;
import com.ipin.front.model.DistrictModel;
import com.ipin.front.model.ProvinceModel;
import com.ipin.front.service.BaseService;
import com.ipin.front.util.BaseData;
import com.ipin.front.util.Tools;

@Controller
@RequestMapping
public class BaseController extends BaseData {

	@Autowired
	private BaseService baseService;

	@RequestMapping("/auth_token")
	@ResponseBody
	public String weixin(String signature, String timestamp, String nonce,String echostr) {
		String tmpStr;
		try {
			tmpStr = Tools.getSHA1(WX_TOKEN, timestamp, nonce);
			if (tmpStr.equals(signature)) {
				return echostr;
			} else {
				return null;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取省份信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getProvinces")
	public ModelAndView getProvinces() {
		ModelAndView view = new ModelAndView("/json");
		List<ProvinceModel> provinces = baseService.getProvinces();
		JSONObject jObj = new JSONObject();
		jObj.put(RESULT, OK);
		jObj.put(DATA, provinces);
		view.addObject("models", jObj);
		return view;
	}

	/**
	 * 更新省份id获取城市信息
	 * 
	 * @param provinceId
	 * @return
	 */
	@RequestMapping(value = "/getCityByProvince")
	public ModelAndView getCityByProvince(int provinceId) {
		ModelAndView view = new ModelAndView("/json");
		List<CityModel> citys = baseService.getCityByProvince(provinceId);
		JSONObject jObj = new JSONObject();
		jObj.put(RESULT, OK);
		jObj.put(DATA, citys);
		view.addObject("models", jObj);
		return view;
	}

	@RequestMapping(value = "/getDistrictByCity")
	public ModelAndView getDistrictByCity(int cityId) {
		ModelAndView view = new ModelAndView("/json");
		List<DistrictModel> district = baseService.getDistrictByCity(cityId);
		JSONObject jObj = new JSONObject();
		jObj.put(RESULT, OK);
		jObj.put(DATA, district);
		view.addObject("models", jObj);
		return view;
	}
	
}
