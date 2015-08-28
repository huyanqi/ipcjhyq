package com.ipin.front.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ipin.front.model.MatchModel;
import com.ipin.front.model.SMSModel;
import com.ipin.front.model.TempUser;
import com.ipin.front.model.UserModel;

@Component
public interface MatchDao {

	boolean saveMatchModel(MatchModel matchModel);

	List<MatchModel> searchBaseP4C(MatchModel model);
	
	List<MatchModel> searchBaseC4P(MatchModel model);

	UserModel getUserByOpenId(String openId);

	void regUser(UserModel user);

	List<MatchModel> testrefreshMatch();

	MatchModel getMatchById(int i);

	List<MatchModel> refreshC4PMatchFromDB(MatchModel model);

	List<MatchModel> refreshP4CMatchFromDB(MatchModel model);

	MatchModel getMyMatchByOpenId(String openId);

	boolean removeMatch(MatchModel model);

	UserModel getMyUserInfo(String openId);

	TempUser createTempUser(String name, String mobile);

	List<MatchModel> getTempUserMatches(TempUser tempUser);

	List<MatchModel> getMyMatchByTempId(int tempId);

	void saveSMSModel(SMSModel smsModel);

	List<UserModel> getUserByMobile(String mobile);

	List<MatchModel> getMatchByUser(int id);

	List<TempUser> getTempUserByMobile(String mobile);

	List<MatchModel> getMatchByTempUser(int id);

	List<MatchModel> dimMatch(MatchModel myMatch);

	boolean removeMatchById(int id);

}
