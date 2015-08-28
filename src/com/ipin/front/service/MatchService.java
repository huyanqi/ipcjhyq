package com.ipin.front.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ipin.front.model.MatchModel;
import com.ipin.front.model.TempUser;
import com.ipin.front.model.UserModel;

@Component
public interface MatchService {

	boolean p4c(MatchModel match);

	boolean c4p(MatchModel match);
	
	List<MatchModel> refreshMatch(MatchModel model);
	
	List<MatchModel> refreshP4CMatch(MatchModel model);
	
	List<MatchModel> refreshC4PMatch(MatchModel model);

	UserModel getUserByOpenId(String openId,UserModel user);

	List<MatchModel> testrefreshMatch(MatchModel match);

	MatchModel getMatchById(int i);

	MatchModel getMyMatchByOpenId(String openId);

	boolean removeMatchById(int id);

	UserModel getMyUserInfo(String openId);

	TempUser createTempUser(String name, String mobile);

	void sendSuccessMsgToOpenId(HttpServletRequest request, String openId,
			String string);
	
	void sendMatchSuccessMsgToOpenId(HttpServletRequest request,String openId,MatchModel targetModel);
	
	List<MatchModel> getMatchByMobile(String mobile);
	
	void clearByMobile(String mobile);

	List<MatchModel> dimMatch(String openId);
	
	void sendMsg2Mobile(String toMobiles,String[] datas,int type);

	void refreshMatch(HttpServletRequest request, MatchModel match);

}
