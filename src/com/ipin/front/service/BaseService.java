package com.ipin.front.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ipin.front.model.CityModel;
import com.ipin.front.model.DistrictModel;
import com.ipin.front.model.ProvinceModel;

@Component
public interface BaseService {

	List<ProvinceModel> getProvinces();

	List<CityModel> getCityByProvince(int provinceId);

	List<DistrictModel> getDistrictByCity(int cityId);

}
