package com.ipin.front.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;

import com.ipin.front.model.MatchModel;
import com.ipin.front.model.UserModel;
import com.ipin.front.service.MatchService;
import com.ipin.front.util.BaseData;
import com.ipin.front.util.Tools;

@Controller
@RequestMapping
public class MatchController extends BaseData {

	@Autowired
	private MatchService matchService;

	@Autowired
	private RedisInitBean rib;

	@RequestMapping(value = "/p4c")
	public ModelAndView p4c(HttpServletRequest request, HttpSession session,@RequestBody MatchModel match) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		String matchModelResult = Tools.matchModelIsWhole(match);// ����֤
		if ("".equals(matchModelResult)) {
			if (session.getAttribute("user") == null) {
				// û�б����session�������˹��������ж��Ƿ���userSession
				if(session.getAttribute("userSession") == null){
					jObj.put(RESULT, NO);
					jObj.put(DATA, "û�е�¼");
					view.addObject(MODELS, jObj);
					return view;
				}
				match.setTemp_user(matchService.createTempUser(match.user.name,match.user.mobile));
				match.setUser(null);
				// ɾ�������ʱ�û���ǰ��ƥ������
			} else {
				//��ʽ�û�¼�����Ϣ��ɾ���Ѵ��ڵ���ʱ��Ϣ
				/*MatchModel matchByMobile = matchService.getMatchByMobile(match.user.mobile,0);
				if(matchByMobile != null)
					matchService.removeMatchById(matchByMobile.id);*/
				String openId = ((WxMpUser) session.getAttribute("user")).getOpenId();
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
		matchService.refreshMatch(request, match);
		return view;
	}

	@RequestMapping(value = "/c4p")
	public ModelAndView c4p(HttpServletRequest request, HttpSession session,@RequestBody MatchModel match) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		String matchModelResult = Tools.matchModelIsWhole(match);
		if ("".equals(matchModelResult)) {
			if (session.getAttribute("user") == null) {
				// û�б����session�������˹��������ж��Ƿ���userSession
				if(session.getAttribute("userSession") == null){
					jObj.put(RESULT, NO);
					jObj.put(DATA, "û�е�¼");
					view.addObject(MODELS, jObj);
					return view;
				}
				match.setTemp_user(matchService.createTempUser(match.user.name,match.user.mobile));
				match.setUser(null);
			} else {
				//��ʽ�û�¼�����Ϣ��ɾ���Ѵ��ڵ���ʱ��Ϣ
				/*MatchModel matchByMobile = matchService.getMatchByMobile(match.user.mobile,0);
				if(matchByMobile != null)
					matchService.removeMatchById(matchByMobile.id);*/
				String openId = ((WxMpUser) session.getAttribute("user")).getOpenId();
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
		matchService.refreshMatch(request, match); 
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

		WxMpUser wxMpUser = ((WxMpUser) session.getAttribute("user"));
		if(wxMpUser != null){
			//�������SESSION����ֱ����ת��Ŀ�����
			MatchModel myMatch = matchService.getMyMatchByOpenId(wxMpUser.getOpenId());
			ModelAndView view;
			if(myMatch == null){
				view = new ModelAndView("redirect:" + redirectJSP +".jsp");
			}else{
				view = new ModelAndView("redirect:mylist.jsp");
			}
			return view;
		}
		
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
	 * ��ȡ���ύ��ƥ����Ϣ
	 */
	@RequestMapping(value = "/getMyMatch")
	public ModelAndView getMyMatch(HttpSession session) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		String openId = ((WxMpUser) session.getAttribute("user")).getOpenId();//"olU-4uPi_KTjcOMjc2Nnlkt1_Rpk";
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
		String openId = ((WxMpUser) session.getAttribute("user")).getOpenId();//"olU-4uPi_KTjcOMjc2Nnlkt1_Rpk";
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

	@RequestMapping(value = "/testp4c")
	public ModelAndView testp4c(HttpServletRequest request,@RequestBody MatchModel match) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		String matchModelResult = Tools.matchModelIsWhole(match);// ����֤
		if ("".equals(matchModelResult)) {
			//��ʽ�û�¼�����Ϣ��ɾ���Ѵ��ڵ���ʱ��Ϣ
			String openId = "olU-4uPi_KTjcOMjc2Nnlkt1_Rpk";//((WxMpUser) session.getAttribute("user")).getOpenId();
			match.user.setOpenId(openId);
			match.setUser(getUserByOpenId(match.user));// ��ȡ�û���Ϣ��û���򴴽��µ�
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
		matchService.refreshMatch(request, match);
		return view;
	}

	@RequestMapping(value = "/testc4p")
	public ModelAndView testc4p(HttpServletRequest request, HttpSession session,@RequestBody MatchModel match) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		String matchModelResult = Tools.matchModelIsWhole(match);
		if ("".equals(matchModelResult)) {
			if (session.getAttribute("user") == null) {
				// û�б����session�������˹��������ж��Ƿ���userSession
				if(session.getAttribute("userSession") == null){
					jObj.put(RESULT, NO);
					jObj.put(DATA, "û�е�¼");
					view.addObject(MODELS, jObj);
					return view;
				}
				match.setTemp_user(matchService.createTempUser(match.user.name,match.user.mobile));
				match.setUser(null);
			} else {
				//��ʽ�û�¼�����Ϣ��ɾ���Ѵ��ڵ���ʱ��Ϣ
				/*MatchModel matchByMobile = matchService.getMatchByMobile(match.user.mobile,0);
				if(matchByMobile != null)
					matchService.removeMatchById(matchByMobile.id);*/
				String openId = ((WxMpUser) session.getAttribute("user")).getOpenId();
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
		matchService.refreshMatch(request, match); 
		return view;
	}
	
	@RequestMapping(value = "/dimMatch")
	public ModelAndView dimMatch(HttpSession session) {
		ModelAndView view = new ModelAndView("/json");
		JSONObject jObj = new JSONObject();
		String openId = ((WxMpUser) session.getAttribute("user")).getOpenId();//"olU-4uPi_KTjcOMjc2Nnlkt1_Rpk";
		List<MatchModel> models = matchService.dimMatch(openId);
		if(models == null || models.size() == 0){
			jObj.put(RESULT, NO);
			jObj.put(DATA, "������");
		}else{
			jObj.put(RESULT, OK);
			jObj.put(DATA, models);
		}
		view.addObject(MODELS, jObj);
		return view;
	}
	

}
