package com.ipin.front.dao.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.ipin.front.dao.BaseDao;
import com.ipin.front.model.CityModel;
import com.ipin.front.model.DistrictModel;
import com.ipin.front.model.MatchModel;
import com.ipin.front.model.ProvinceModel;
import com.ipin.front.model.SMSModel;

@Repository("baseDao")
public class BaseDaoImpl extends HibernateDaoSupport implements BaseDao {

	@Override
	public List<ProvinceModel> getProvinces() {
		List<ProvinceModel> list = (List<ProvinceModel>) getHibernateTemplate().find("FROM ProvinceModel model WHERE model.state = 1", null);
		return list;
	}

	@Override
	public List<CityModel> getCityByProvince(Integer provinceId) {
		List<CityModel> list = (List<CityModel>) getHibernateTemplate().find("FROM CityModel city WHERE city.province.id = ?", provinceId);
		return list;
	}

	@Override
	public List<DistrictModel> getDistrictByCity(Integer cityId) {
		List<DistrictModel> list = (List<DistrictModel>) getHibernateTemplate().find("FROM DistrictModel district WHERE district.city.id = ? ORDER BY district.weight DESC", cityId);
		return list;
	}

	@Override
	public boolean removeDistrictById(DistrictModel model) {
		try{
			getHibernateTemplate().delete(model);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public DistrictModel getDistrictById(int id) {
		List<DistrictModel> list = (List<DistrictModel>) getHibernateTemplate().find("FROM DistrictModel district WHERE district.id = ?", id);
		if(list.size() > 0)
			return list.get(0);
		return null;
	}

	@Override
	public boolean insertRedirect(DistrictModel model) {
		try{
			getHibernateTemplate().save(model);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean updateDistrictWeight(DistrictModel model) {
		try{
			getHibernateTemplate().update(model);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<MatchModel> getMatchs(int pageNum) {
			final int starts = (pageNum-1)*20;
			final int sizes = 20;
			final String queryString = "FROM MatchModel model ORDER BY model.update_time DESC";
			return getHibernateTemplate().execute(new HibernateCallback<List<MatchModel>>() {
				@Override
				public List<MatchModel> doInHibernate(Session session) throws HibernateException {
					return session.createQuery(queryString).setFirstResult(starts).setMaxResults(sizes).list();
				}
			});
	}

	@Override
	public List<SMSModel> getSMSs(int pageNum) {
		final int starts = (pageNum-1)*20;
		final int sizes = 20;
		final String queryString = "FROM SMSModel model ORDER BY model.timestamp DESC";
		return getHibernateTemplate().execute(new HibernateCallback<List<SMSModel>>() {
			@Override
			public List<SMSModel> doInHibernate(Session session) throws HibernateException {
				return session.createQuery(queryString).setFirstResult(starts).setMaxResults(sizes).list();
			}
		});
	}
	
}
