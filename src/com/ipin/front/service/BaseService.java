package com.ipin.front.service;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ipin.front.model.CityModel;
import com.ipin.front.model.DistrictModel;
import com.ipin.front.model.MatchModel;
import com.ipin.front.model.ProvinceModel;
import com.ipin.front.model.SMSModel;

@Component
public interface BaseService {

	List<ProvinceModel> getProvinces();

	List<CityModel> getCityByProvince(int provinceId);

	List<DistrictModel> getDistrictByCity(int cityId);

	boolean removeDistrictById(int id);

	boolean insertDistrict(DistrictModel model);

	boolean updateDistrictWeight(int id, int weight);

	List<MatchModel> getMatchs(int pageNum);

	List<SMSModel> getSMSs(int pageNum);

}
