package com.ipin.front.controller;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ipin.front.model.CityModel;
import com.ipin.front.model.DistrictModel;
import com.ipin.front.model.MatchModel;
import com.ipin.front.model.ProvinceModel;
import com.ipin.front.model.SMSModel;
import com.ipin.front.service.BaseService;
import com.ipin.front.service.MatchService;
import com.ipin.front.util.BaseData;
import com.ipin.front.util.Tools;

@Controller
@RequestMapping
public class BaseController extends BaseData {

	@Autowired
	private BaseService baseService;
	
	@Autowired
	private MatchService matchService;

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
	
	@RequestMapping(value = "/insertDistrict")
	public ModelAndView insertDistrict(@RequestBody DistrictModel model) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		boolean result = baseService.insertDistrict(model);
		if(result){
			jObj.put(RESULT, OK);
		}else{
			jObj.put(RESULT, NO);
		}
		view.addObject("models", jObj);
		return view;
	}
	
	@RequestMapping(value = "/removeDistrictById")
	public ModelAndView removeDistrictById(int id) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		boolean result = baseService.removeDistrictById(id);
		if(result){
			jObj.put(RESULT, OK);
		}else{
			jObj.put(RESULT, NO);
		}
		view.addObject("models", jObj);
		return view;
	}
	
	@RequestMapping(value = "/updateDistrictWeight")
	public ModelAndView updateDistrictWeight(int id,int weight) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		boolean result = baseService.updateDistrictWeight(id,weight);
		if(result){
			jObj.put(RESULT, OK);
		}else{
			jObj.put(RESULT, NO);
		}
		view.addObject("models", jObj);
		return view;
	}
	
	@RequestMapping(value = "/getMatchs")
	public ModelAndView getMatchs(int pageNum) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		List<MatchModel> matchs = baseService.getMatchs(pageNum);
		if(matchs.size() > 0){
			jObj.put(RESULT, OK);
			jObj.put(DATA, matchs);
		}else{
			jObj.put(RESULT, NO);
		}
		view.addObject("models", jObj);
		return view;
	}
	
	@RequestMapping(value = "/getSMSs")
	public ModelAndView getSMSs(int pageNum) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		List<SMSModel> matchs = baseService.getSMSs(pageNum);
		if(matchs.size() > 0){
			jObj.put(RESULT, OK);
			jObj.put(DATA, matchs);
		}else{
			jObj.put(RESULT, NO);
		}
		view.addObject("models", jObj);
		return view;
	}
	
	@RequestMapping(value = "/adminLogin")
	public ModelAndView adminLogin(String username,String password,HttpServletRequest request) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		if("ipinche".equals(username) && "admin.".equals(password)){
			request.getSession().setAttribute("userSession", 1);
			jObj.put(RESULT, OK);
			jObj.put(DATA, "back/index.jsp");
		}else{
			jObj.put(RESULT, NO);
			jObj.put(DATA, "账号/密码错误.");
		}
		view.addObject("models", jObj);
		return view;
	}
	
	@RequestMapping(value = "/getSession")
	public ModelAndView adminLogin(HttpServletRequest request) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		jObj.put(DATA, request.getSession().getAttribute("userSession"));
		view.addObject("models", jObj);
		return view;
	}

	@RequestMapping(value = "/search")
	public ModelAndView search(String mobile) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		List<MatchModel> models = matchService.getMatchByMobile(mobile);
		if(models.size() > 0){
			jObj.put(RESULT, OK);
			jObj.put(DATA, models);
		}else{
			jObj.put(RESULT, NO);
		}
		view.addObject("models", jObj);
		return view;
	}
	
}
