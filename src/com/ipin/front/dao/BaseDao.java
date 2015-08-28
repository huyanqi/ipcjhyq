package com.ipin.front.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ipin.front.model.CityModel;
import com.ipin.front.model.DistrictModel;
import com.ipin.front.model.MatchModel;
import com.ipin.front.model.ProvinceModel;
import com.ipin.front.model.SMSModel;

@Component
public interface BaseDao {

	List<ProvinceModel> getProvinces();

	List<CityModel> getCityByProvince(Integer provinceId);

	List<DistrictModel> getDistrictByCity(Integer cityId);

	boolean removeDistrictById(DistrictModel model);

	DistrictModel getDistrictById(int id);

	boolean insertRedirect(DistrictModel model);

	boolean updateDistrictWeight(DistrictModel model);

	List<MatchModel> getMatchs(int pageNum);

	List<SMSModel> getSMSs(int pageNum);

}
