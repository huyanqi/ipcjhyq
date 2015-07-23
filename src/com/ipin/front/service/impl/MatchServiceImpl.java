package com.ipin.front.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.WxMpCustomMessage;
import me.chanjar.weixin.mp.bean.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.WxMpTemplateMessage;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipin.front.dao.MatchDao;
import com.ipin.front.model.MatchModel;
import com.ipin.front.model.TempUser;
import com.ipin.front.model.UserModel;
import com.ipin.front.service.MatchService;
import com.ipin.front.util.BaseData;
import com.ipin.front.util.Tools;

@Service("matchService")
@Transactional(rollbackFor = Exception.class)
public class MatchServiceImpl extends BaseData implements MatchService {

	@Autowired
	private MatchDao matchDao;
	
	@Override
	public boolean p4c(MatchModel match) {
		boolean result = matchDao.saveMatchModel(match);
		return result;
	}

	@Override
	public boolean c4p(MatchModel match) {
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
		templateMessage.getDatas().add(new WxMpTemplateData("remark", "��л��ʹ�� iƴ��"));
		try {
			wxMpService.templateSend(templateMessage);
		} catch (WxErrorException e) {
			e.printStackTrace();
		}
    }

	@Override
	public void sendSuccessMSG(HttpServletRequest request,MatchModel myModel, MatchModel otherModel) {
		String mymobile = "";
		String othermobile = "";
		String name = "";
		if(myModel.user != null){
			mymobile = myModel.user.mobile;
		}else{
			mymobile = myModel.temp_user.mobile;
		}
		if(otherModel.user != null){
			othermobile = otherModel.user.mobile;
			name = otherModel.user.name;
		}else{
			othermobile = otherModel.temp_user.mobile;
			name = otherModel.temp_user.name;
		}
		if(myModel.user != null){
			//����΢����ʾ
			sendMatchSuccessMsgToOpenId(request, myModel.user.openId, otherModel);
		}
		//�ȼ������Ƿ��ѷ��͹�,û�з����ٷ�
		//���Ͷ�����ʾ
		if(myModel.user != null){
			sendSuccessSMSToMobile(myModel.user.mobile, otherModel);
		}else{
			sendSuccessSMSToMobile(myModel.temp_user.mobile, otherModel);
		}
		matchDao.saveMatchModel(myModel);
	}

	@Override
	public void removeTempUserMatches(TempUser tempUser) {
		List<MatchModel> list = matchDao.getTempUserMatches(tempUser);
		for(MatchModel model : list){
			System.out.println("ɾ����"+model.id);
			matchDao.removeMatchById(model.id);
		}
	}

	@Override
	public void sendSuccessSMSToMobile(String mobile, MatchModel targetModel) {
		int date = targetModel.date;
		int hour = targetModel.hour;
		String from = targetModel.fromDistrict.name;
		String to = targetModel.toDistrict.name;
		String name = "";
		String tomobile = "";
		if(targetModel.user != null){
			name = targetModel.user.name;
			tomobile = targetModel.user.mobile;
		}else{
			name = targetModel.temp_user.name;
			tomobile = targetModel.temp_user.mobile;
		}
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://dx.chinactcm.com/osgi/sendSMS.do");
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("userName", "iƴ��"));
		nvps.add(new BasicNameValuePair("passWord", "a123123"));
		nvps.add(new BasicNameValuePair("phoneList", mobile));
		if(targetModel.type == 0){
			//Ŀ�������ҳ���mobile�����ǳ����绰
			String msg = "��iƴ�����װ���ƴ��Ⱥ�ѣ����ҵ�"+date+"��"+hour+"��"+from+"��"+to+"��ƴ��"+name+tomobile+"�뼰ʱ��ϵ����ע���ں�xxxxxx���ƴ���ȴ���".replace("��", "").replace("��", "");
			nvps.add(new BasicNameValuePair("content", msg));
		}else{
			//Ŀ���ǳ�����,mobile������ƴ�͵绰
			String msg = "��iƴ�����װ���ƴ��Ⱥ�ѣ����ҵ�"+date+"��"+hour+"��"+from+"��"+to+"�ĳ���"+name+tomobile+"�뼰ʱ��ϵ����ע���ں�xxxxxx���ƴ���ȴ���".replace("��", "").replace("��", "");
			nvps.add(new BasicNameValuePair("content", msg));
		}
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
			HttpResponse response = httpclient.execute(httpPost);
			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();
			String responseStr = EntityUtils.toString(entity, "utf-8");
			// TODO: �������������������Ӧ�ı�
			System.out.println(response.getStatusLine() + ":" + responseStr);
			EntityUtils.consume(entity);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpPost.releaseConnection();
		}
	}

}
