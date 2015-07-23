package com.ipin.front.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ipin.front.dao.BaseDao;
import com.ipin.front.model.CityModel;
import com.ipin.front.model.DistrictModel;
import com.ipin.front.model.ProvinceModel;
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

}
