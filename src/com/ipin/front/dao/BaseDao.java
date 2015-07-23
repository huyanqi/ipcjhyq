package com.ipin.front.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ipin.front.model.CityModel;
import com.ipin.front.model.DistrictModel;
import com.ipin.front.model.ProvinceModel;

@Component
public interface BaseDao {

	List<ProvinceModel> getProvinces();

	List<CityModel> getCityByProvince(Integer provinceId);

	List<DistrictModel> getDistrictByCity(Integer cityId);

}
