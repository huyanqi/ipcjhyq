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
		String matchModelResult = Tools.matchModelIsWhole(match);// ����֤
		if ("".equals(matchModelResult)) {
			if (session.getAttribute("user") == null) {
				// û�б����session�������˹�����
				match.setTemp_user(matchService.createTempUser(match.user.name,
						match.user.mobile));
				match.setUser(null);
				// ɾ�������ʱ�û���ǰ��ƥ������
				matchService.removeTempUserMatches(match.temp_user);
			} else {
				String openId = ((WxMpUser) session.getAttribute("user"))
						.getOpenId();
				match.user.setOpenId(openId);
				match.setUser(getUserByOpenId(match.user));// ��ȡ�û���Ϣ��û���򴴽��µ�
			}
			boolean result = matchService.p4c(match);
			if (result) {
				jObj.put(RESULT, OK);
				if (match.user != null)
					matchService.sendSuccessMsgToOpenId(request, match.user.openId,"Biu~��Ϣ�ѳɹ�¼�룬���ǽ�����Ϊ���ҵ����ʵ�С�������С����ע���ǵ�΢��������Ϣ�ͱ��ֵ绰��ͨ��");
			} else {
				jObj.put(RESULT, NO);
				jObj.put(DATA, "��Ǹ,��̨�����쳣");
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
				// û�б����session�������˹�����
				match.setTemp_user(matchService.createTempUser(match.user.name,
						match.user.mobile));
				match.setUser(null);
			} else {
				String openId = ((WxMpUser) session.getAttribute("user"))
						.getOpenId();
				match.user.setOpenId(openId);
				match.setUser(getUserByOpenId(match.user));// ��ȡ�û���Ϣ��û���򴴽��µ�
			}
			boolean result = matchService.c4p(match);
			if (result) {
				jObj.put(RESULT, OK);
				if (match.user != null) {
					matchService
							.sendSuccessMsgToOpenId(request, match.user.openId,
									"Biu~��Ϣ�ѳɹ�¼�룬���ǽ�����Ϊ���ҵ����ʵ�С�����С����ע���ǵ�΢��������Ϣ�ͱ��ֵ绰��ͨ��");
				}
			} else {
				jObj.put(RESULT, NO);
				jObj.put(DATA, "��Ǹ,��̨�����쳣");
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
	 * ����OAuth��Ȩҳ
	 * 
	 * @param redirectJSP
	 *            ��Ȩ�ɹ�����ת��ҳ��
	 * @return
	 */
	@RequestMapping(value = "/WXOAuth")
	public ModelAndView WXOAuth(HttpSession session, String redirectJSP) {

		WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
		config.setAppId(WX_APPID); // ����΢�Ź��ںŵ�appid
		config.setSecret(WX_APPSECRET); // ����΢�Ź��ںŵ�app corpSecret
		config.setToken(WX_TOKEN); // ����΢�Ź��ںŵ�token
		config.setAesKey(WX_AESKEY); // ����΢�Ź��ںŵ�EncodingAESKey

		WxMpServiceImpl wxMpService = new WxMpServiceImpl();
		wxMpService.setWxMpConfigStorage(config);

		String url = wxMpService.oauth2buildAuthorizationUrl(DOMAIN + "WXOauth.jsp", WxConsts.OAUTH2_SCOPE_USER_INFO, redirectJSP);
		ModelAndView view = new ModelAndView("redirect:" + url);

		return view;
	}

	/**
	 * ��Ȩ��ɺ���õĽӿ�
	 * 
	 * @param code
	 *            ���ڻ�ȡ�û�������Ϣ
	 * @param state
	 *            �û���תҳ��
	 * @return
	 */
	@RequestMapping(value = "/WXOAuthCompleted")
	public ModelAndView WXOAuthCompleted(HttpSession session, String code,
			String state) {
		WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
		config.setAppId(WX_APPID); // ����΢�Ź��ںŵ�appid
		config.setSecret(WX_APPSECRET); // ����΢�Ź��ںŵ�app corpSecret
		config.setToken(WX_TOKEN); // ����΢�Ź��ںŵ�token
		config.setAesKey(WX_AESKEY); // ����΢�Ź��ںŵ�EncodingAESKey

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
			//������ƥ�����Ϣ����ת��mylist.jsp
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
		System.out.println("�ֲ�ʽ���ӳ��첽���ú�ʱ: " + ((end - start) / 1000.0) + " ��");
		try {
			Thread.sleep(5000);// ˯5�룬Ȼ���ӡjedis���صĽ��
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("���ؽ����" + results);
		m.addObject("returnMsg", "ôô�գ�");
		return m;
	}

	/**
	 * ͨ��openid��ȡ���û������û�д��û������Զ�����
	 * 
	 * @param user
	 * @return
	 */
	public UserModel getUserByOpenId(UserModel user) {
		return matchService.getUserByOpenId(user.openId, user);
	}

	/**
	 * ˢ��ƥ�����ݣ�֪ͨ���ƥ����û�
	 */
	public void refreshMatch(HttpServletRequest request, MatchModel model) {
		List<MatchModel> matchs = matchService.refreshMatch(model);
		if (matchs.size() > 0) {
			// ���͸���
			matchService.sendSuccessMSG(request, model, matchs.get(0));
		}
		for (MatchModel match : matchs) {
			// ���͸�������
			matchService.sendSuccessMSG(request, match, model);
		}
	}

	/**
	 * ��ȡ���ύ��ƥ����Ϣ
	 */
	@RequestMapping(value = "/getMyMatch")
	public ModelAndView getMyMatch(HttpSession session) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		String openId = ((WxMpUser) session.getAttribute("user")).getOpenId();// "oezO4uDJBNuyRd8VflHxjpguQmd0"
		MatchModel model = matchService.getMyMatchByOpenId(openId);
		if (model == null) {
			// ���ҵ�ƥ����Ϣ
			jObj.put(RESULT, NO);
		} else {
			jObj.put(RESULT, OK);
			jObj.put(DATA, model);
		}
		view.addObject(MODELS, jObj);
		return view;
	}

	/**
	 * ��ȡ����ƥ�����Ϣ
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
			// ���ҵ�ƥ����Ϣ
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
			// �򿪺�URL֮�������
			URLConnection conn = url.openConnection();
			// ����ͨ�õ���������
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Content-Type",
					"application/json;charset=utf-8");
			conn.setRequestProperty("Authorization",
					new BASE64Encoder().encode(timestamp.getBytes()));
			// ����POST�������������������
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// ��ȡURLConnection�����Ӧ�������
			out = new PrintWriter(conn.getOutputStream());
			// �����������

			JSONArray jAry = new JSONArray();
			jAry.add(code);
			jAry.add(minutes);
			JSONObject params = new JSONObject();
			params.put("to", mobile);
			params.put("appId", "aaf98f894e999d73014eaae67202120b");
			params.put("templateId", "1");

			params.put("datas", jAry);
			out.print(params.toString());
			// flush������Ļ���
			out.flush();
			// ����BufferedReader����������ȡURL����Ӧ
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
