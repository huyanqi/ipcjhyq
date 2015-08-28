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
	 * 刷新一次匹配
	 */
	@Override
	public List<MatchModel> refreshMatch(MatchModel model) {
		if(model.type == 0)
			//如果最新一条信息是人找车
			return refreshP4CMatchFromDB(model);
		else
			//如果最新一条信息是车找人
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
		//人找车思路：
		//1、查询出可载人数>0并且与出发地相同的(省份-城市-区)车找人信息
		List<MatchModel> baseModels = matchDao.searchBaseP4C(model);
		System.out.print("人找车基础数据"+baseModels.size()+"个:");
		for(MatchModel mod : baseModels){
			System.out.print(mod.id+",");
		}
		//2、从中筛选出与 目的地 相同的(省份-城市-区)车找人信息
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
		//3、匹配日期，筛选出yyyy/MM/dd一样的信息
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
		//4、匹配时间:1、筛选车子出发前2小时到出发时间的信息 2、筛选出人出发前后2小时时间
		int peopleTime = Integer.parseInt(model.time.split(" ")[1].split(":")[0]);
		baseIt = baseModels.iterator();
		while(baseIt.hasNext()){
			MatchModel baseModel = baseIt.next();
			int carTime = Integer.parseInt(baseModel.time.split(" ")[1].split(":")[0]);
			if((carTime - 2 <= peopleTime && peopleTime <= carTime) || (peopleTime-2 <= carTime && carTime <= peopleTime) || (peopleTime <= carTime && carTime <= peopleTime+2)){
				resultArray.add(baseModel);
			}
		}
		//5、得到结果
		return resultArray;
	}

	@Override
	public List<MatchModel> refreshC4PMatch(MatchModel model) {
		//车找人思路
		//1、查询出与出发地相同的(省份-城市-区)人找车信息
		List<MatchModel> baseModels = matchDao.searchBaseC4P(model);
		//2、从中筛选出与目的地相同的(省份-城市-区)人找车信息
		Iterator<MatchModel> baseIt = baseModels.iterator();
		while(baseIt.hasNext()){
			MatchModel baseModel = baseIt.next();
			if(baseModel.toDistrict.id != model.toDistrict.id || baseModel.toDistrict.city.id != model.toDistrict.city.id || baseModel.toDistrict.city.province.id != model.toDistrict.city.province.id){
				baseIt.remove();
			}
		}
		//3、匹配日期，筛选出yyyy/MM/dd一样的信息
		String[] modelDateArray = model.time.split(" ")[0].split("/");
		baseIt = baseModels.iterator();
		while(baseIt.hasNext()){
			MatchModel baseModel = baseIt.next();
			String[] baseDateArray = baseModel.time.split(" ")[0].split("/");
			if(!baseDateArray[0].equals(modelDateArray[0]) || !baseDateArray[1].equals(modelDateArray[1]) || !baseDateArray[2].equals(modelDateArray[2])){
				baseIt.remove();
			}
		}
		//4、匹配时间，筛选车出发前3小时到出发时间的人找车信息
		int carTime = Integer.parseInt(model.time.split(" ")[1].split(":")[0]);//汽车17点出发
		baseIt = baseModels.iterator();
		while(baseIt.hasNext()){
			MatchModel baseModel = baseIt.next();
			int peopleTime = Integer.parseInt(baseModel.time.split(" ")[1].split(":")[0]);//人15点出发
			if(!(carTime - 3 <= peopleTime && peopleTime <= carTime)){
				baseIt.remove();
			}
		}
		//5、得到结果
		return baseModels;
	}

	@Override
	public UserModel getUserByOpenId(String openId,UserModel user) {
		UserModel userModel = null;
		userModel = matchDao.getUserByOpenId(openId);
		if(userModel == null){
			//没有注册过此openId,重新注册
			user.setOpenId(openId);
			matchDao.regUser(user);
			userModel = user;
		}else{
			//修改名字和电话,保证每次自动填充的都是最后一条记录的信息
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
     * 发送文本消息到此openId
     * @param openId
     * @param msg
     */
	public void sendSuccessMsgToOpenId(HttpServletRequest request,String openId,String msg){
    	WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
		config.setAppId(WX_APPID); // 设置微信公众号的appid
		config.setSecret(WX_APPSECRET); // 设置微信公众号的app corpSecret
		config.setToken(WX_TOKEN); // 设置微信公众号的token
		config.setAesKey(WX_AESKEY); // 设置微信公众号的EncodingAESKey
		
		WxMpServiceImpl wxMpService = new WxMpServiceImpl();
		wxMpService.setWxMpConfigStorage(config);
		
		WxMpCustomMessage message = null;
		//提交成功
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
		config.setAppId(WX_APPID); // 设置微信公众号的appid
		config.setSecret(WX_APPSECRET); // 设置微信公众号的app corpSecret
		config.setToken(WX_TOKEN); // 设置微信公众号的token
		config.setAesKey(WX_AESKEY); // 设置微信公众号的EncodingAESKey
		
		WxMpServiceImpl wxMpService = new WxMpServiceImpl();
		wxMpService.setWxMpConfigStorage(config);
		
		WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
		templateMessage.setToUser(openId);
		templateMessage.setUrl(DOMAIN+"mylist.jsp");
		templateMessage.setTopColor("#04aeda");
		String role;
		if(targetModel.type == 0){
			//目标是人
			role = "拼客";
			templateMessage.setTemplateId("S_wVsjczeOa5Lm3ttUj_y2aqBMs2yf4hzm1AieUDOyQ");
		}else{
			//目标是车
			role = "车主";
			templateMessage.setTemplateId("8VZvWCwtN4_PyW-LNgrDnKy8sZSPKTvzG69vqqt5iBw");
		}
		templateMessage.getDatas().add(new WxMpTemplateData("first", "已匹配到"+role+",请及时联系"));
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
			//本条信息发给车
			templateMessage.getDatas().add(new WxMpTemplateData("remark", "感谢您使用 i拼车"));
		}else{
			//本条信息发给人
			templateMessage.getDatas().add(new WxMpTemplateData("remark", "将分摊油费"+targetModel.price+"元,感谢您使用 i拼车","#FF4500"));
		}
		try {
			wxMpService.templateSend(templateMessage);
		} catch (WxErrorException e) {
			e.printStackTrace();
		}
    }
	
	private void sendMatchSuccessMsgToOpenId(HttpServletRequest request,String openId, List<MatchModel> otherModels) {
		WxMpInMemoryConfigStorage config = new WxMpInMemoryConfigStorage();
		config.setAppId(WX_APPID); // 设置微信公众号的appid
		config.setSecret(WX_APPSECRET); // 设置微信公众号的app corpSecret
		config.setToken(WX_TOKEN); // 设置微信公众号的token
		config.setAesKey(WX_AESKEY); // 设置微信公众号的EncodingAESKey
		
		WxMpServiceImpl wxMpService = new WxMpServiceImpl();
		wxMpService.setWxMpConfigStorage(config);
		
		WxMpTemplateMessage templateMessage = new WxMpTemplateMessage();
		templateMessage.setToUser(openId);
		templateMessage.setUrl(DOMAIN+"mylist.jsp");
		templateMessage.setTopColor("#04aeda");
		for(MatchModel targetModel:otherModels){
			String role;
			if(targetModel.type == 0){
				//目标是人
				role = "拼客";
				templateMessage.setTemplateId("S_wVsjczeOa5Lm3ttUj_y2aqBMs2yf4hzm1AieUDOyQ");
			}else{
				//目标是车
				role = "车主";
				templateMessage.setTemplateId("8VZvWCwtN4_PyW-LNgrDnKy8sZSPKTvzG69vqqt5iBw");
			}
			templateMessage.getDatas().add(new WxMpTemplateData("first", "已匹配到"+role+",请及时联系"));
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
				//本条信息发给车
				templateMessage.getDatas().add(new WxMpTemplateData("remark", "感谢您使用 i拼车"));
			}else{
				//本条信息发给人
				templateMessage.getDatas().add(new WxMpTemplateData("remark", "将分摊油费"+targetModel.price+"元,感谢您使用 i拼车","#FF4500"));
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
	 * @param mobile 手机号
	 * @param type 0临时用户 1正式用户
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
	 * 获取与我行程同一天、同一出发地/目的地的匹配信息
	 */
	@Override
	public List<MatchModel> dimMatch(String openId) {
		MatchModel myMatch = matchDao.getMyMatchByOpenId(openId);
		if(myMatch == null) return null;
		return matchDao.dimMatch(myMatch);
	}

	/**
	 * toMobiles:目标手机号，多个用,隔开
	 * datas 短信里面的模板内容
	 * type:0(对方是车找人):使用模板29183 1(对方是人找车):使用模板27475
	 */
	@Override
	public void sendMsg2Mobile(String toMobiles, String[] datas, int type) {
		MatchModel model = getMatchByMobile(toMobiles).get(0);
		if(model.temp_user != null && model.msgcount > 0){
			//是临时用户，并且接收过匹配信息，不再发送
			return;
		}
		HashMap<String, Object> result = null;
		CCPRestSDK restAPI = new CCPRestSDK();
		restAPI.init("app.cloopen.com", "8883");// 初始化服务器地址和端口，格式如下，服务器地址不需要写https://
		restAPI.setAccount("8a48b5514e8a7522014eaa80786f23d5", "f2e464252d5a4806b9ceb6b838ebd7ea");// 初始化主帐号名称和主帐号令牌
		restAPI.setAppId("8a48b5514eaf512c014eb3c631bb05d8");// 初始化应用ID
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
			//正常返回输出data包体信息（map）
			HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
			Set<String> keySet = data.keySet();
			for(String key:keySet){
				Object object = data.get(key);
			}
		}else{
			//异常返回输出错误码和错误信息
			smsModel.statusMsg = (String) result.get("statusMsg");
		}
		matchDao.saveSMSModel(smsModel);
		
		for(MatchModel my : getMatchByMobile(toMobiles)){
			my.msgcount ++;
			matchDao.saveMatchModel(my); 
		}
	}

	/**
	 * 刷新匹配数据，通知完成匹配的用户
	 */
	@Override
	public void refreshMatch(HttpServletRequest request, MatchModel model) {
		List<MatchModel> matchs = refreshMatch(model);
		if(matchs.size() == 0)return;
		//有匹配到的数据，需要发送成功短信
		String tartgetMobile = "";
		for(MatchModel targetModel : matchs){
			if(targetModel.user != null){
				tartgetMobile += targetModel.user.mobile+",";
				//组装发给我的数据
				if(model.user != null){
					sendMatchSuccessMsgToOpenId(request, model.user.openId, targetModel);
				}
			}else{
				tartgetMobile += targetModel.temp_user.mobile+",";
			}
			
			//再发给对方
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
		data[4] = (model.type==0?"车主:":"拼客:")+tartgetMobile;
		if(model.type==0){
			//需要提示油费信息
			data[5] = matchs.get(0).price+"";
		}
		data[6] = "ippppc";
		if(model.user != null){
			sendMsg2Mobile(model.user.mobile, data,model.type);
		}else{
			sendMsg2Mobile(model.temp_user.mobile, data,model.type);
		}
		
		//再组装发给对方的数据
		data = new String[7];
		for(MatchModel targetModel : matchs){
			data[0] = targetModel.date+"";
			data[1] = targetModel.hour+"";
			data[2] = targetModel.fromDistrict.name;
			data[3] = targetModel.toDistrict.name;
			if(model.user != null){
				data[4] = (targetModel.type==0?"车主:":"拼客:"+model.user.mobile);
			}else{
				data[4] = (targetModel.type==0?"车主:":"拼客:"+model.temp_user.mobile);
			}
			if(targetModel.type==0){
				//需要提示油费信息
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
