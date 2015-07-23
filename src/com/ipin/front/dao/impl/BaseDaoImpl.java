package com.ipin.front.dao.impl;

import java.util.List;

import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;

import com.ipin.front.dao.BaseDao;
import com.ipin.front.model.CityModel;
import com.ipin.front.model.DistrictModel;
import com.ipin.front.model.ProvinceModel;

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
		List<DistrictModel> list = (List<DistrictModel>) getHibernateTemplate().find("FROM DistrictModel district WHERE district.city.id = ?", cityId);
		return list;
	}

}
