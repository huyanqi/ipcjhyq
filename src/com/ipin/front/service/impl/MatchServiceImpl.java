package com.ipin.front.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.WxMpCustomMessage;
import me.chanjar.weixin.mp.bean.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.WxMpTemplateMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloopen.rest.sdk.CCPRestSDK;
import com.ipin.front.dao.MatchDao;
import com.ipin.front.model.MatchModel;
import com.ipin.front.model.SMSModel;
import com.ipin.front.model.TempUser;
import com.ipin.front.model.UserModel;
import com.ipin.front.service.MatchService;
import com.ipin.front.util.BaseData;
import com.ipin.front.util.Tools;

/**
 * @author Frankie
 *
 */
@Service("matchService")
@Transactional(rollbackFor = Exception.class)
public class MatchServiceImpl extends BaseData implements MatchService {

	@Autowired
	private MatchDao matchDao;
	
	@Override
	public boolean p4c(MatchModel match) {
		if(match.user != null)
			clearByMobile(match.user.mobile);
		else
			clearByMobile(match.temp_user.mobile);
		boolean result = matchDao.saveMatchModel(match);
		return result;
	}

	@Override
	public boolean c4p(MatchModel match) {
		if(match.user != null)
			clearByMobile(match.user.mobile);
		else
			clearByMobile(match.temp_user.mobile);
		boolean result = matchDao.saveMatchModel(match);
		return result;
	}

	/**
	 * ˢ��һ��ƥ��
	 */
	@Override
	public List<MatchModel> refreshMatch(MatchModel model) {
		if(model.type == 0)
			//�������һ����Ϣ�����ҳ�
			return refreshP4CMatchFromDB(model);
		else
			//�������һ����Ϣ�ǳ�����
			return refreshC4PMatchFromDB(model);
	}

	private List<MatchModel> refreshC4PMatchFromDB(MatchModel model) {
		return matchDao.refreshC4PMatchFromDB(model);
	}

	private List<MatchModel> refreshP4CMatchFromDB(MatchModel model) {
		return matchDao.refreshP4CMatchFromDB(model);
	}

	@Override
	public List<MatchModel> refreshP4CMatch(MatchModel model) {
		List<MatchModel> resultArray = new ArrayList<MatchModel>();
		//���ҳ�˼·��
		//1����ѯ����������>0�������������ͬ��(ʡ��-����-��)��������Ϣ
		List<MatchModel> baseModels = matchDao.searchBaseP4C(model);
		System.out.print("���ҳ���������"+baseModels.size()+"��:");
		for(MatchModel mod : baseModels){
			System.out.print(mod.id+",");
		}
		//2������ɸѡ���� Ŀ�ĵ� ��ͬ��(ʡ��-����-��)��������Ϣ
		Iterator<MatchModel> baseIt = baseModels.iterator();
		while(baseIt.hasNext()){
			MatchModel baseModel = baseIt.next();
			System.out.println(baseModel.toDistrict.id != model.toDistrict.id);
			System.out.println(baseModel.toDistrict.city.id != model.toDistrict.city.id);
			System.out.println(baseModel.toDistrict.city.province.id != model.toDistrict.city.province.id);
			if(baseModel.toDistrict.id != model.toDistrict.id || baseModel.toDistrict.city.id != model.toDistrict.city.id || baseModel.toDistrict.city.province.id != model.toDistrict.city.province.id){
				baseIt.remove();
			}
		}
		//3��ƥ�����ڣ�ɸѡ��yyyy/MM/ddһ������Ϣ
		String[] modelDateArray = model.time.split(" ")[0].split("/");
		baseIt = baseModels.iterator();
		while(baseIt.hasNext()){
			MatchModel baseModel = baseIt.next();
			String[] baseDateArray = baseModel.time.split(" ")[0].split("/");
			System.out.println(!baseDateArray[0].equals(modelDateArray[0]));
			System.out.println(!baseDateArray[1].equals(modelDateArray[1]));
			System.out.println(!baseDateArray[2].equals(modelDateArray[2]));
			if(!baseDateArray[0].equals(modelDateArray[0]) || !baseDateArray[1].equals(modelDateArray[1]) || !baseDateArray[2].equals(modelDateArray[2])){
				baseIt.remove();
			}
		}
		//4��ƥ��ʱ��:1��ɸѡ���ӳ���ǰ2Сʱ������ʱ�����Ϣ 2��ɸѡ���˳���ǰ��2Сʱʱ��
		int peopleTime = Integer.parseInt(model.time.split(" ")[1].split(":")[0]);
		baseIt = baseModels.iterator();
		while(baseIt.hasNext()){
			MatchModel baseModel = baseIt.next();
			int carTime = Integer.parseInt(baseModel.time.split(" ")[1].split(":")[0]);
			if((carTime - 2 <= peopleTime && peopleTime <= carTime) || (peopleTime-2 <= carTime && carTime <= peopleTime) || (peopleTime <= carTime && carTime <= peopleTime+2)){
				resultArray.add(baseModel);
			}
		}
		//5���õ����
		return resultArray;
	}

	@Override
	public List<MatchModel> refreshC4PMatch(MatchModel model) {
		//������˼·
		//1����ѯ�����������ͬ��(ʡ��-����-��)���ҳ���Ϣ
		List<MatchModel> baseModels = matchDao.searchBaseC4P(model);
		//2������ɸѡ����Ŀ�ĵ���ͬ��(ʡ��-����-��)���ҳ���Ϣ
		Iterator<MatchModel> baseIt = baseModels.iterator();
		while(baseIt.hasNext()){
			MatchModel baseModel = baseIt.next();
			if(baseModel.toDistrict.id != model.toDistrict.id || baseModel.toDistrict.city.id != model.toDistrict.city.id || baseModel.toDistrict.city.province.id != model.toDistrict.city.province.id){
				baseIt.remove();
			}
		}
		//3��ƥ�����ڣ�ɸѡ��yyyy/MM/ddһ������Ϣ
		String[] modelDateArray = model.time.split(" ")[0].split("/");
		baseIt = baseModels.iterator();
		while(baseIt.hasNext()){
			MatchModel baseModel = baseIt.next();
			String[] baseDateArray = baseModel.time.split(" ")[0].split("/");
			if(!baseDateArray[0].equals(modelDateArray[0]) || !baseDateArray[1].equals(modelDateArray[1]) || !baseDateArray[2].equals(modelDateArray[2])){
				baseIt.remove();
			}
		}
		//4��ƥ��ʱ�䣬ɸѡ������ǰ3Сʱ������ʱ������ҳ���Ϣ
		int carTime = Integer.parseInt(model.time.split(" ")[1].split(":")[0]);//����17�����
		baseIt = baseModels.iterator();
		while(baseIt.hasNext()){
			MatchModel baseModel = baseIt.next();
			int peopleTime = Integer.parseInt(baseModel.time.split(" ")[1].split(":")[0]);//��15�����
			if(!(carTime - 3 <= peopleTime && peopleTime <= carTime)){
				baseIt.remove();
			}
		}
		//5���õ����
		return baseModels;
	}

	@Override
	public UserModel getUserByOpenId(String openId,UserModel user) {
		UserModel userModel = null;
		userModel = matchDao.getUserByOpenId(openId);
		if(userModel == null){
			//û��ע�����openId,����ע��
			user.setOpenId(openId);
			matchDao.regUser(user);
			userModel = user;
		}else{
			//�޸����ֺ͵绰,��֤ÿ���Զ����Ķ������һ����¼����Ϣ
			userModel.setName(user.getName());
			userModel.setMobile(user.getMobile());
			matchDao.regUser(userModel);
		}
		return userModel;
	}

	@Override
	public List<MatchModel> testrefreshMatch(MatchModel match) {
		return refreshMatch(match);
	}

	@Override
	public MatchModel getMatchById(int i) {
		return matchDao.getMatchById(i);
	}

	@Override
	public MatchModel getMyMatchByOpenId(String openId) {
		return matchDao.getMyMatchByOpenId(openId);
	}

	@Override
	public boolean removeMatchById(int id) {
		return matchDao.removeMatchById(id);
	}

	@Override
 	public UserModel getMyUserInfo(String openId) {
		return matchDao.getMyUserInfo(openId);
	}

	@Override
	public TempUser createTempUser(String name, String mobile) {
		return matchDao.createTempUser(name,mobile);
	}
	
	/**
     * �����ı���Ϣ����openId
     * @param openId
     * @param msg
     */
	public void sendSuccessMsgToOpenId(HttpServletRequest request,String openId,String msg){
    	WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
		config.setAppId(WX_APPID); // ����΢�Ź��ںŵ�appid
		config.setSecret(WX_APPSECRET); // ����΢�Ź��ںŵ�app corpSecret
		config.setToken(WX_TOKEN); // ����΢�Ź��ںŵ�token
		config.setAesKey(WX_AESKEY); // ����΢�Ź��ںŵ�EncodingAESKey
		
		WxMpServiceImpl wxMpService = new WxMpServiceImpl();
		wxMpService.setWxMpConfigStorage(config);
		
		WxMpCustomMessage message = null;
		//�ύ�ɹ�
		message = WxMpCustomMessage
		  .TEXT()
		  .toUser(openId)
		  .content(msg)
		  .build();
		try {
			wxMpService.customMessageSend(message);
		} catch (WxErrorException e) {
			e.printStackTrace();
		}
    }
	
	@Override
	public void sendMatchSuccessMsgToOpenId(HttpServletRequest request,String openId,MatchModel targetModel){
    	WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
		config.setAppId(WX_APPID); // ����΢�Ź��ںŵ�appid
		config.setSecret(WX_APPSECRET); // ����΢�Ź��ںŵ�app corpSecret
		config.setToken(WX_TOKEN); // ����΢�Ź��ںŵ�token
		config.setAesKey(WX_AESKEY); // ����΢�Ź��ںŵ�EncodingAESKey
		
		WxMpServiceImpl wxMpService = new WxMpServiceImpl();
		wxMpService.setWxMpConfigStorage(config);
		
		WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
		templateMessage.setToUser(openId);
		templateMessage.setUrl(DOMAIN+"mylist.jsp");
		templateMessage.setTopColor("#04aeda");
		String role;
		if(targetModel.type == 0){
			//Ŀ������
			role = "ƴ��";
			templateMessage.setTemplateId("S_wVsjczeOa5Lm3ttUj_y2aqBMs2yf4hzm1AieUDOyQ");
		}else{
			//Ŀ���ǳ�
			role = "����";
			templateMessage.setTemplateId("8VZvWCwtN4_PyW-LNgrDnKy8sZSPKTvzG69vqqt5iBw");
		}
		templateMessage.getDatas().add(new WxMpTemplateData("first", "��ƥ�䵽"+role+",�뼰ʱ��ϵ"));
		templateMessage.getDatas().add(new WxMpTemplateData("keyword1", Tools.getAddress(targetModel.fromDistrict, targetModel.fromAddress)));
		templateMessage.getDatas().add(new WxMpTemplateData("keyword2", Tools.getAddress(targetModel.toDistrict, targetModel.toAddress)));
		templateMessage.getDatas().add(new WxMpTemplateData("keyword3", targetModel.time));
		if(targetModel.user != null){
			templateMessage.getDatas().add(new WxMpTemplateData("keyword4", targetModel.user.name));
			templateMessage.getDatas().add(new WxMpTemplateData("keyword5", targetModel.user.mobile));
		}else{
			templateMessage.getDatas().add(new WxMpTemplateData("keyword4", targetModel.temp_user.name));
			templateMessage.getDatas().add(new WxMpTemplateData("keyword5", targetModel.temp_user.mobile));
		}
		if(targetModel.type == 0){
			//������Ϣ������
			templateMessage.getDatas().add(new WxMpTemplateData("remark", "��л��ʹ�� iƴ��"));
		}else{
			//������Ϣ������
			templateMessage.getDatas().add(new WxMpTemplateData("remark", "����̯�ͷ�"+targetModel.price+"Ԫ,��л��ʹ�� iƴ��","#FF4500"));
		}
		try {
			wxMpService.templateSend(templateMessage);
		} catch (WxErrorException e) {
			e.printStackTrace();
		}
    }
	
	private void sendMatchSuccessMsgToOpenId(HttpServletRequest request,String openId, List<MatchModel> otherModels) {
		WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
		config.setAppId(WX_APPID); // ����΢�Ź��ںŵ�appid
		config.setSecret(WX_APPSECRET); // ����΢�Ź��ںŵ�app corpSecret
		config.setToken(WX_TOKEN); // ����΢�Ź��ںŵ�token
		config.setAesKey(WX_AESKEY); // ����΢�Ź��ںŵ�EncodingAESKey
		
		WxMpServiceImpl wxMpService = new WxMpServiceImpl();
		wxMpService.setWxMpConfigStorage(config);
		
		WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
		templateMessage.setToUser(openId);
		templateMessage.setUrl(DOMAIN+"mylist.jsp");
		templateMessage.setTopColor("#04aeda");
		for(MatchModel targetModel:otherModels){
			String role;
			if(targetModel.type == 0){
				//Ŀ������
				role = "ƴ��";
				templateMessage.setTemplateId("S_wVsjczeOa5Lm3ttUj_y2aqBMs2yf4hzm1AieUDOyQ");
			}else{
				//Ŀ���ǳ�
				role = "����";
				templateMessage.setTemplateId("8VZvWCwtN4_PyW-LNgrDnKy8sZSPKTvzG69vqqt5iBw");
			}
			templateMessage.getDatas().add(new WxMpTemplateData("first", "��ƥ�䵽"+role+",�뼰ʱ��ϵ"));
			templateMessage.getDatas().add(new WxMpTemplateData("keyword1", Tools.getAddress(targetModel.fromDistrict, targetModel.fromAddress)));
			templateMessage.getDatas().add(new WxMpTemplateData("keyword2", Tools.getAddress(targetModel.toDistrict, targetModel.toAddress)));
			templateMessage.getDatas().add(new WxMpTemplateData("keyword3", targetModel.time));
			if(targetModel.user != null){
				templateMessage.getDatas().add(new WxMpTemplateData("keyword4", targetModel.user.name));
				templateMessage.getDatas().add(new WxMpTemplateData("keyword5", targetModel.user.mobile));
			}else{
				templateMessage.getDatas().add(new WxMpTemplateData("keyword4", targetModel.temp_user.name));
				templateMessage.getDatas().add(new WxMpTemplateData("keyword5", targetModel.temp_user.mobile));
			}
			if(targetModel.type == 0){
				//������Ϣ������
				templateMessage.getDatas().add(new WxMpTemplateData("remark", "��л��ʹ�� iƴ��"));
			}else{
				//������Ϣ������
				templateMessage.getDatas().add(new WxMpTemplateData("remark", "����̯�ͷ�"+targetModel.price+"Ԫ,��л��ʹ�� iƴ��","#FF4500"));
			}
			try {
				wxMpService.templateSend(templateMessage);
			} catch (WxErrorException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param mobile �ֻ���
	 * @param type 0��ʱ�û� 1��ʽ�û�
	 * @return
	 */
	@Override
	public List<MatchModel> getMatchByMobile(String mobile) {
		List<MatchModel> models = new ArrayList<MatchModel>();
		List<TempUser> tempUser = matchDao.getTempUserByMobile(mobile);
		for(TempUser user : tempUser){
			List<MatchModel> model = matchDao.getMatchByTempUser(user.id);
			models.addAll(model);
		}
		
		List<UserModel> user = matchDao.getUserByMobile(mobile);
		for(UserModel um : user){
			List<MatchModel> model = matchDao.getMatchByUser(um.id);
			models.addAll(model);
		}
		return models;
	}
	
	@Override
	public void clearByMobile(String mobile) {
		List<MatchModel> matchs = getMatchByMobile(mobile);
		for(MatchModel match : matchs){
			matchDao.removeMatch(match);
		}
	}

	/**
	 * ��ȡ�����г�ͬһ�졢ͬһ������/Ŀ�ĵص�ƥ����Ϣ
	 */
	@Override
	public List<MatchModel> dimMatch(String openId) {
		MatchModel myMatch = matchDao.getMyMatchByOpenId(openId);
		if(myMatch == null) return null;
		return matchDao.dimMatch(myMatch);
	}

	/**
	 * toMobiles:Ŀ���ֻ��ţ������,����
	 * datas ���������ģ������
	 * type:0(�Է��ǳ�����):ʹ��ģ��29183 1(�Է������ҳ�):ʹ��ģ��27475
	 */
	@Override
	public void sendMsg2Mobile(String toMobiles, String[] datas, int type) {
		MatchModel model = getMatchByMobile(toMobiles).get(0);
		if(model.temp_user != null && model.msgcount > 0){
			//����ʱ�û������ҽ��չ�ƥ����Ϣ�����ٷ���
			return;
		}
		HashMap<String, Object> result = null;
		CCPRestSDK restAPI = new CCPRestSDK();
		restAPI.init("app.cloopen.com", "8883");// ��ʼ����������ַ�Ͷ˿ڣ���ʽ���£���������ַ����Ҫдhttps://
		restAPI.setAccount("8a48b5514e8a7522014eaa80786f23d5", "f2e464252d5a4806b9ceb6b838ebd7ea");// ��ʼ�����ʺ����ƺ����ʺ�����
		restAPI.setAppId("8a48b5514eaf512c014eb3c631bb05d8");// ��ʼ��Ӧ��ID
		List<String> temp = new ArrayList<String>();
		for(String str:datas){
			if(str != null)
				temp.add(str);
		}
		datas = new String[temp.size()];
		for(int i=0;i<temp.size();i++){
			datas[i] = temp.get(i);
		}
		
		result = restAPI.sendTemplateSMS(toMobiles,type==0?"29183":"27475",datas);
		SMSModel smsModel = new SMSModel();
		smsModel.content = Tools.ary2Str(datas);
		smsModel.statusCode = (String) result.get("statusCode");
		smsModel.mobile = toMobiles;
		if("000000".equals(result.get("statusCode"))){
			//�����������data������Ϣ��map��
			HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
			}
		}else{
			//�쳣�������������ʹ�����Ϣ
			smsModel.statusMsg = (String) result.get("statusMsg");
		}
		matchDao.saveSMSModel(smsModel);
		
		for(MatchModel my : getMatchByMobile(toMobiles)){
			my.msgcount ++;
			matchDao.saveMatchModel(my); 
		}
	}

	/**
	 * ˢ��ƥ�����ݣ�֪ͨ���ƥ����û�
	 */
	@Override
	public void refreshMatch(HttpServletRequest request, MatchModel model) {
		List<MatchModel> matchs = refreshMatch(model);
		if(matchs.size() == 0)return;
		//��ƥ�䵽�����ݣ���Ҫ���ͳɹ�����
		String tartgetMobile = "";
		for(MatchModel targetModel : matchs){
			if(targetModel.user != null){
				tartgetMobile += targetModel.user.mobile+",";
				//��װ�����ҵ�����
				if(model.user != null){
					sendMatchSuccessMsgToOpenId(request, model.user.openId, targetModel);
				}
			}else{
				tartgetMobile += targetModel.temp_user.mobile+",";
			}
			
			//�ٷ����Է�
			if(targetModel.user != null){
				sendMatchSuccessMsgToOpenId(request, targetModel.user.openId, model);
			}
		}
		if(tartgetMobile.length() > 0) tartgetMobile = tartgetMobile.substring(0,tartgetMobile.length()-1);
		String[] data = new String[7];
		data[0] = model.date+"";
		data[1] = model.hour+"";
		data[2] = model.fromDistrict.name;
		data[3] = model.toDistrict.name;
		data[4] = (model.type==0?"����:":"ƴ��:")+tartgetMobile;
		if(model.type==0){
			//��Ҫ��ʾ�ͷ���Ϣ
			data[5] = matchs.get(0).price+"";
		}
		data[6] = "ippppc";
		if(model.user != null){
			sendMsg2Mobile(model.user.mobile, data,model.type);
		}else{
			sendMsg2Mobile(model.temp_user.mobile, data,model.type);
		}
		
		//����װ�����Է�������
		data = new String[7];
		for(MatchModel targetModel : matchs){
			data[0] = targetModel.date+"";
			data[1] = targetModel.hour+"";
			data[2] = targetModel.fromDistrict.name;
			data[3] = targetModel.toDistrict.name;
			if(model.user != null){
				data[4] = (targetModel.type==0?"����:":"ƴ��:"+model.user.mobile);
			}else{
				data[4] = (targetModel.type==0?"����:":"ƴ��:"+model.temp_user.mobile);
			}
			if(targetModel.type==0){
				//��Ҫ��ʾ�ͷ���Ϣ
				data[5] = model.price+"";
			}
			data[6] = "ippppc";
			if(targetModel.user != null){
				sendMsg2Mobile(targetModel.user.mobile, data,targetModel.type);
			}else{
				sendMsg2Mobile(targetModel.temp_user.mobile, data,targetModel.type);
			}
		}
	}
	
}
