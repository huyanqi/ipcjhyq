package com.ipin.front.dao.impl;

import java.util.List;

import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.ipin.front.dao.MatchDao;
import com.ipin.front.model.MatchModel;
import com.ipin.front.model.SMSModel;
import com.ipin.front.model.TempUser;
import com.ipin.front.model.UserModel;
import com.ipin.front.util.Tools;

@Repository("matchDao")
public class MatchDaoImpl extends HibernateDaoSupport implements MatchDao{

	@Override
	public boolean saveMatchModel(MatchModel match) {
		try{
			Tools.parseTime2YTD(match);
			getHibernateTemplate().saveOrUpdate(match);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MatchModel> searchBaseP4C(MatchModel model) {
		//��ѯ����������>0�������������ͬ��(ʡ��-����-��)��������Ϣ
		return (List<MatchModel>) getHibernateTemplate().find("FROM MatchModel model WHERE model.type = 1 AND model.people > 0 AND model.fromDistrict.id = ?" ,model.getFromDistrict().getId());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<MatchModel> searchBaseC4P(MatchModel model) {
		//��ѯ��������ͬ��(ʡ��-����-��)���ҳ���Ϣ
		return (List<MatchModel>) getHibernateTemplate().find("FROM MatchModel model WHERE model.type = 0 AND model.fromDistrict.id = ? " , model.getFromDistrict().getId());
	}

	@Override
	public UserModel getUserByOpenId(String openId) {
		List<UserModel> list = (List<UserModel>) getHibernateTemplate().find("FROM UserModel user WHERE user.openId = ?", openId);
		if(list.size() > 0)
			return list.get(0);
		return null;
	}

	@Override
	public void regUser(UserModel user) {
		getHibernateTemplate().saveOrUpdate(user);
	}

	@Override
	public List<MatchModel> testrefreshMatch() {
		List<MatchModel> list = (List<MatchModel>) getHibernateTemplate().find("FROM MatchModel model WHERE model.type = 1 AND model.people > 0 AND model.fromDistrict.id = ? AND model.toDistrict.id = ?",new Object[]{2771,309});
		for(MatchModel model : list){
			System.out.println(model.time);
		}
		return list;
	}

	@Override
	public MatchModel getMatchById(int i) {
		List<MatchModel> matchs = (List<MatchModel>) getHibernateTemplate().find("FROM MatchModel model WHERE model.id = ?",i);
		if(matchs.size() > 0)
			return matchs.get(0);
		return null;
	}
	
	/**
	 * ƥ�����ҳ���Ϣ
	 * @param model ����Ϣ
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<MatchModel> refreshP4CMatchFromDB(MatchModel model) {
		//��ѯ��type=1�� �����ء�Ŀ�ĵء��������ڡ�����>0����ƥ����Ϣ
		//ƥ��ʱ��:1��ɸѡ���ӳ���ǰ2Сʱ������ʱ�����Ϣ 2��ɸѡ���˳���ǰ��2Сʱʱ��
		List<MatchModel> matchs = (List<MatchModel>) getHibernateTemplate().find(
				"FROM MatchModel model WHERE model.type = 1 "
				+ "AND model.fromDistrict.id = ? "
				+ "AND model.toDistrict.id = ? "
				+ "AND model.year = ? "
				+ "AND model.month = ? "
				+ "AND model.date = ? "
				+ "AND model.people > 0"
				+ "AND ((model.hour - 2 <= ? AND ? <= model.hour) OR (?-2 <= model.hour AND model.hour <= ?) OR (? <= model.hour AND model.hour <= ?+2))",new Object[]{
						model.fromDistrict.id,
						model.toDistrict.id,
						model.year,
						model.month,
						model.date,
						model.hour,
						model.hour,
						model.hour,
						model.hour,
						model.hour,
						model.hour});
		return matchs;
	}

	/**
	 * ƥ�䳵������Ϣ
	 * @param model ������Ϣ
	 */
	@Override
	public List<MatchModel> refreshC4PMatchFromDB(MatchModel model) {
		//��ѯ��type=0�� �����ء�Ŀ�ĵء�����������ͬ ��ƥ����Ϣ
		//ƥ��ʱ��:ɸѡ������ǰ3Сʱ������ʱ������ҳ���Ϣ
		//car - 3 <= people && people <= car
		List<MatchModel> matchs = (List<MatchModel>) getHibernateTemplate().find(
				"FROM MatchModel model WHERE model.type = 0 "
				+ "AND model.fromDistrict.id = ? "
				+ "AND model.toDistrict.id = ? "
				+ "AND model.year = ? "
				+ "AND model.month = ? "
				+ "AND model.date = ? "
				+ "AND (? - 3 <= model.hour AND model.hour <= ?)",new Object[]{model.fromDistrict.id,
						model.toDistrict.id,
						model.year,
						model.month,
						model.date,
						model.hour,model.hour});
		return matchs;
	}

	@Override
	public MatchModel getMyMatchByOpenId(String openId) {
		List<MatchModel> models = (List<MatchModel>) getHibernateTemplate().find("FROM MatchModel model WHERE model.user.openId = ?", openId);
		if(models.size() > 0)
			return models.get(0);
		return null;
	}
	
	@Override
	public List<MatchModel> getMyMatchByTempId(int tempId) {
		List<MatchModel> models = (List<MatchModel>) getHibernateTemplate().find("FROM MatchModel model WHERE model.temp_user.id = ?", tempId);
		return models;
	}

	@Override
	public boolean removeMatch(MatchModel model) {
		try{
			getHibernateTemplate().delete(model);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean removeMatchById(int id) {
		MatchModel model = getMatchById(id);
		if(model != null){
			getHibernateTemplate().delete(model);
			return true;
		}
		return false;
	}

	@Override
	public UserModel getMyUserInfo(String openId) {
		List<UserModel> models = (List<UserModel>) getHibernateTemplate().find("FROM UserModel model WHERE model.openId = ?", openId);
		if(models.size() > 0)
			return models.get(0);
		return null;
	}

	@Override
	public TempUser createTempUser(String name, String mobile) {
		List<TempUser> users = (List<TempUser>) getHibernateTemplate().find("FROM TempUser model WHERE model.mobile = ?", mobile);
		if(users.size() > 0){
			//���ڣ�ɾ������ƥ���¼���û�
			List<MatchModel> models = getMyMatchByTempId(users.get(0).id);
			for(MatchModel model : models){
				getHibernateTemplate().delete(model);
			}
			getHibernateTemplate().delete(users.get(0));
		}
		TempUser tempUser = new TempUser();
		tempUser.setName(name);
		tempUser.setMobile(mobile);
		getHibernateTemplate().save(tempUser);
		return tempUser;
	}

	@Override
	public List<MatchModel> getTempUserMatches(TempUser tempUser) {
		return (List<MatchModel>) getHibernateTemplate().find("FROM MatchModel model WHERE model.temp_user.id = ?", tempUser.id);
	}

	@Override
	public void saveSMSModel(SMSModel smsModel) {
		getHibernateTemplate().save(smsModel);
	}

	@Override
	public List<UserModel> getUserByMobile(String mobile) {
		return (List<UserModel>) getHibernateTemplate().find("FROM UserModel model WHERE model.mobile = ?", mobile);
	}

	@Override
	public List<MatchModel> getMatchByUser(int id) {
		return (List<MatchModel>) getHibernateTemplate().find("FROM MatchModel model WHERE model.user.id = ?", id);
	}

	@Override
	public List<TempUser> getTempUserByMobile(String mobile) {
		return (List<TempUser>) getHibernateTemplate().find("FROM TempUser model WHERE model.mobile = ?", mobile);
	}

	@Override
	public List<MatchModel> getMatchByTempUser(int id) {
		return (List<MatchModel>) getHibernateTemplate().find("FROM MatchModel model WHERE model.temp_user.id = ?", id);
	}

	@Override
	public List<MatchModel> dimMatch(MatchModel myMatch) {
		//��ȡ������/Ŀ�ĵ�/ʱ��yyyyMMdd��ͬ������
		int type = myMatch.type == 0 ? 1:0;
		return (List<MatchModel>) getHibernateTemplate().find("FROM MatchModel model WHERE "
				+ "model.fromDistrict.city.id = ? AND "
				+ "model.toDistrict.city.id = ? AND "
				+ "model.year = ? AND "
				+ "model.month = ? AND "
				+ "model.date = ? AND "
				+ "model.type = ?", new Object[]{myMatch.fromDistrict.city.id,myMatch.toDistrict.city.id,myMatch.year,myMatch.month,myMatch.date,type});
	}

}
