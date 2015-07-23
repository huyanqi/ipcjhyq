package com.ipin.front.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ipin.front.model.MatchModel;
import com.ipin.front.model.TempUser;
import com.ipin.front.model.UserModel;

@Component
public interface MatchDao {

	boolean saveMatchModel(MatchModel match);

	List<MatchModel> searchBaseP4C(MatchModel model);
	
	List<MatchModel> searchBaseC4P(MatchModel model);

	UserModel getUserByOpenId(String openId);

	void regUser(UserModel user);

	List<MatchModel> testrefreshMatch();

	MatchModel getMatchById(int i);

	List<MatchModel> refreshC4PMatchFromDB(MatchModel model);

	List<MatchModel> refreshP4CMatchFromDB(MatchModel model);

	MatchModel getMyMatchByOpenId(String openId);

	boolean removeMatchById(int id);

	UserModel getMyUserInfo(String openId);

	TempUser createTempUser(String name, String mobile);

	List<MatchModel> getTempUserMatches(TempUser tempUser);

	List<MatchModel> getMyMatchByTempId(int tempId);

}
