package com.ipin.front.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipin.front.dao.BaseDao;
import com.ipin.front.model.CityModel;
import com.ipin.front.model.DistrictModel;
import com.ipin.front.model.MatchModel;
import com.ipin.front.model.ProvinceModel;
import com.ipin.front.model.SMSModel;
import com.ipin.front.service.BaseService;

@Service("baseService")
@Transactional(rollbackFor = Exception.class)
public class BaseServiceImpl implements BaseService {

	@Autowired
	private BaseDao baseDao;
	
	@Override
	public List<ProvinceModel> getProvinces() {
		return baseDao.getProvinces();
	}

	@Override
	public List<CityModel> getCityByProvince(int provinceId) {
		return baseDao.getCityByProvince(provinceId);
	}

	@Override
	public List<DistrictModel> getDistrictByCity(int cityId) {
		return baseDao.getDistrictByCity(cityId);
	}

	@Override
	public boolean removeDistrictById(int id) {
		DistrictModel model = baseDao.getDistrictById(id);
		if(model == null)return false;
		return baseDao.removeDistrictById(model);
	}

	@Override
	public boolean insertDistrict(DistrictModel model) {
		return baseDao.insertRedirect(model);
	}

	@Override
	public boolean updateDistrictWeight(int id, int weight) {
		DistrictModel model = baseDao.getDistrictById(id);
		model.weight = weight;
		return baseDao.updateDistrictWeight(model);
	}

	@Override
	public List<MatchModel> getMatchs(int pageNum) {
		return baseDao.getMatchs(pageNum);
	}

	@Override
	public List<SMSModel> getSMSs(int pageNum) {
		return baseDao.getSMSs(pageNum);
	}

}
