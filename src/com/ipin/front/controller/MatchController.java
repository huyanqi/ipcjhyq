package com.ipin.front.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import sun.misc.BASE64Encoder;

import com.ipin.front.model.MatchModel;
import com.ipin.front.model.UserModel;
import com.ipin.front.service.MatchService;
import com.ipin.front.util.BaseData;
import com.ipin.front.util.MD5Util;
import com.ipin.front.util.Tools;

@Controller
@RequestMapping
public class MatchController extends BaseData {

	@Autowired
	private MatchService matchService;

	@Autowired
	private RedisInitBean rib;

	@RequestMapping(value = "/p4c")
	public ModelAndView p4c(HttpServletRequest request, HttpSession session,
			@RequestBody MatchModel match) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		String matchModelResult = Tools.matchModelIsWhole(match);// 表单验证
		if ("".equals(matchModelResult)) {
			if (session.getAttribute("user") == null) {
				// 没有保存过session，属于人工创建
				match.setTemp_user(matchService.createTempUser(match.user.name,
						match.user.mobile));
				match.setUser(null);
				// 删除这个临时用户以前的匹配数据
				matchService.removeTempUserMatches(match.temp_user);
			} else {
				String openId = ((WxMpUser) session.getAttribute("user"))
						.getOpenId();
				match.user.setOpenId(openId);
				match.setUser(getUserByOpenId(match.user));// 获取用户信息，没有则创建新的
			}
			boolean result = matchService.p4c(match);
			if (result) {
				jObj.put(RESULT, OK);
				if (match.user != null)
					matchService.sendSuccessMsgToOpenId(request, match.user.openId,"Biu~信息已成功录入，我们将尽快为你找到合适的小车车出行。请关注我们的微信推送消息和保持电话畅通。");
			} else {
				jObj.put(RESULT, NO);
				jObj.put(DATA, "抱歉,后台处理异常");
			}
		} else {
			jObj.put(RESULT, NO);
			jObj.put(DATA, matchModelResult);
		}
		view.addObject(MODELS, jObj);
		refreshMatch(request, match);
		return view;
	}

	@RequestMapping(value = "/c4p")
	public ModelAndView c4p(HttpServletRequest request, HttpSession session,
			@RequestBody MatchModel match) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		String matchModelResult = Tools.matchModelIsWhole(match);
		if ("".equals(matchModelResult)) {
			if (session.getAttribute("user") == null) {
				// 没有保存过session，属于人工创建
				match.setTemp_user(matchService.createTempUser(match.user.name,
						match.user.mobile));
				match.setUser(null);
			} else {
				String openId = ((WxMpUser) session.getAttribute("user"))
						.getOpenId();
				match.user.setOpenId(openId);
				match.setUser(getUserByOpenId(match.user));// 获取用户信息，没有则创建新的
			}
			boolean result = matchService.c4p(match);
			if (result) {
				jObj.put(RESULT, OK);
				if (match.user != null) {
					matchService
							.sendSuccessMsgToOpenId(request, match.user.openId,
									"Biu~信息已成功录入，我们将尽快为你找到合适的小伙伴出行。请关注我们的微信推送消息和保持电话畅通。");
				}
			} else {
				jObj.put(RESULT, NO);
				jObj.put(DATA, "抱歉,后台处理异常");
			}
		} else {
			jObj.put(RESULT, NO);
			jObj.put(DATA, matchModelResult);
		}
		view.addObject(MODELS, jObj);
		refreshMatch(request, match);
		return view;
	}

	/**
	 * 进入OAuth授权页
	 * 
	 * @param redirectJSP
	 *            授权成功后跳转的页面
	 * @return
	 */
	@RequestMapping(value = "/WXOAuth")
	public ModelAndView WXOAuth(HttpSession session, String redirectJSP) {

		WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
		config.setAppId(WX_APPID); // 设置微信公众号的appid
		config.setSecret(WX_APPSECRET); // 设置微信公众号的app corpSecret
		config.setToken(WX_TOKEN); // 设置微信公众号的token
		config.setAesKey(WX_AESKEY); // 设置微信公众号的EncodingAESKey

		WxMpServiceImpl wxMpService = new WxMpServiceImpl();
		wxMpService.setWxMpConfigStorage(config);

		String url = wxMpService.oauth2buildAuthorizationUrl(DOMAIN + "WXOauth.jsp", WxConsts.OAUTH2_SCOPE_USER_INFO, redirectJSP);
		ModelAndView view = new ModelAndView("redirect:" + url);

		return view;
	}

	/**
	 * 授权完成后调用的接口
	 * 
	 * @param code
	 *            用于获取用户基本信息
	 * @param state
	 *            用户跳转页面
	 * @return
	 */
	@RequestMapping(value = "/WXOAuthCompleted")
	public ModelAndView WXOAuthCompleted(HttpSession session, String code,
			String state) {
		WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
		config.setAppId(WX_APPID); // 设置微信公众号的appid
		config.setSecret(WX_APPSECRET); // 设置微信公众号的app corpSecret
		config.setToken(WX_TOKEN); // 设置微信公众号的token
		config.setAesKey(WX_AESKEY); // 设置微信公众号的EncodingAESKey

		WxMpServiceImpl wxMpService = new WxMpServiceImpl();
		wxMpService.setWxMpConfigStorage(config);

		WxMpOAuth2AccessToken wxMpOAuth2AccessToken;
		WxMpUser wxMpUser = null;
		try {
			wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
			wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken,null);
		} catch (WxErrorException e) {
			e.printStackTrace();
			ModelAndView view = new ModelAndView("/json");
			JSONObject jObj = new JSONObject();
			jObj.put("error", e.getError().getErrorCode() + ":" + e.getError().getErrorMsg());
			view.addObject(MODELS, jObj);
			return view;
		}

		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		jObj.put(RESULT, OK);
		jObj.put("user", wxMpUser);
		MatchModel myMatch = matchService.getMyMatchByOpenId(wxMpUser.getOpenId());
		if(myMatch != null){
			//存在我匹配的信息，跳转至mylist.jsp
			jObj.put("state", "mylist");
		}else{
			jObj.put("state", state);
		}
		view.addObject(MODELS, jObj);
		session.setAttribute("user", wxMpUser);
		return view;
	}

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	public ModelAndView test() {
		ModelAndView m = new ModelAndView("/json");
		ShardedJedis jedis = rib.getSingletonInstance();
		ShardedJedisPipeline pipeline = jedis.pipelined();
		long start = System.currentTimeMillis();
		for (int i = 0; i < 99999; i++) {
			pipeline.set("zhenbn" + i, "n" + i);
		}
		List<Object> results = pipeline.syncAndReturnAll();
		long end = System.currentTimeMillis();
		rib.returnResource();
		rib.destroy();
		System.out.println("分布式连接池异步调用耗时: " + ((end - start) / 1000.0) + " 秒");
		try {
			Thread.sleep(5000);// 睡5秒，然后打印jedis返回的结果
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("返回结果：" + results);
		m.addObject("returnMsg", "么么哒！");
		return m;
	}

	/**
	 * 通过openid获取到用户，如果没有此用户，会自动创建
	 * 
	 * @param user
	 * @return
	 */
	public UserModel getUserByOpenId(UserModel user) {
		return matchService.getUserByOpenId(user.openId, user);
	}

	/**
	 * 刷新匹配数据，通知完成匹配的用户
	 */
	public void refreshMatch(HttpServletRequest request, MatchModel model) {
		List<MatchModel> matchs = matchService.refreshMatch(model);
		if (matchs.size() > 0) {
			// 发送给我
			matchService.sendSuccessMSG(request, model, matchs.get(0));
		}
		for (MatchModel match : matchs) {
			// 发送给其他人
			matchService.sendSuccessMSG(request, match, model);
		}
	}

	/**
	 * 获取我提交的匹配信息
	 */
	@RequestMapping(value = "/getMyMatch")
	public ModelAndView getMyMatch(HttpSession session) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		String openId = ((WxMpUser) session.getAttribute("user")).getOpenId();// "oezO4uDJBNuyRd8VflHxjpguQmd0"
		MatchModel model = matchService.getMyMatchByOpenId(openId);
		if (model == null) {
			// 无我的匹配信息
			jObj.put(RESULT, NO);
		} else {
			jObj.put(RESULT, OK);
			jObj.put(DATA, model);
		}
		view.addObject(MODELS, jObj);
		return view;
	}

	/**
	 * 获取与我匹配的信息
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping(value = "/getMyMatchs")
	public ModelAndView getMyMatchs(HttpSession session) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		String openId = ((WxMpUser) session.getAttribute("user")).getOpenId();//"oezO4uDJBNuyRd8VflHxjpguQmd0";
		MatchModel model = matchService.getMyMatchByOpenId(openId);
		if (model == null) {
			// 无我的匹配信息
			jObj.put(RESULT, NO);
		} else {
			jObj.put(RESULT, OK);
			List<MatchModel> matchs = matchService.refreshMatch(model);
			jObj.put(DATA, matchs);
			jObj.put("my", model);
		}
		view.addObject(MODELS, jObj);
		return view;
	}

	@RequestMapping(value = "/removeMatchById")
	public ModelAndView removeMatchById(int id) {
		ModelAndView view = new ModelAndView("/json");
		boolean result = matchService.removeMatchById(id);
		JSONObject jObj = new JSONObject();
		if (result) {
			jObj.put(RESULT, OK);
		} else {
			jObj.put(RESULT, NO);
		}
		view.addObject(MODELS, jObj);
		return view;
	}

	@RequestMapping(value = "/getMyUserInfo")
	public ModelAndView getMyUserInfo(HttpSession session) {
		ModelAndView view = new ModelAndView("/json");
		String openId = ((WxMpUser) session.getAttribute("user")).getOpenId();// "oezO4uDJBNuyRd8VflHxjpguQmd0";
		UserModel user = matchService.getMyUserInfo(openId);
		JSONObject jObj = new JSONObject();
		if (user != null) {
			jObj.put(RESULT, OK);
			jObj.put(DATA, user);
		} else {
			jObj.put(RESULT, NO);
		}
		view.addObject(MODELS, jObj);
		return view;
	}

	@RequestMapping(value = "/testrefreshMatch")
	public void testrefreshMatch(String mobile, int code, int minutes) {
		try {
			PrintWriter out = null;
			BufferedReader in = null;
			String result = "";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			String timestamp = "8a48b5514e8a7522014eaa80786f23d5:"
					+ sdf.format(new Date());
			URL url = new URL(
					"https://sandboxapp.cloopen.com:8883/2013-12-26/Accounts/8a48b5514e8a7522014eaa80786f23d5/SMS/TemplateSMS?sig="
							+ MD5Util.MD5("aaf98f894e999d73014eaae67228120c"
									+ "ab9d7bbe3395479eb7350edb9f8fdfc8"
									+ timestamp));
			System.out.println(url);
			// 打开和URL之间的连接
			URLConnection conn = url.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type",
					"application/json;charset=utf-8");
			conn.setRequestProperty("Authorization",
					new BASE64Encoder().encode(timestamp.getBytes()));
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(conn.getOutputStream());
			// 发送请求参数

			JSONArray jAry = new JSONArray();
			jAry.add(code);
			jAry.add(minutes);
			JSONObject params = new JSONObject();
			params.put("to", mobile);
			params.put("appId", "aaf98f894e999d73014eaae67202120b");
			params.put("templateId", "1");

			params.put("datas", jAry);
			out.print(params.toString());
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), Charset.forName("UTF-8")));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			in.close();
			out.close();
			System.out.println(result);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/testSMS")
	public void testSMS(String mobile) {
		
	}

}
