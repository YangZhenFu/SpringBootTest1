package com.stylefeng.guns.modular.air.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.stylefeng.guns.modular.air.model.AirStation;

/**
 * <p>
 * 气象站表 服务类
 * </p>
 *
 * @author YangZhenfu123
 * @since 2018-04-17
 */
public interface IAirStationService extends IService<AirStation> {

	/**  
	 * <p>Title: save</p>  
	 * <p>Description: </p>  
	 * @param station
	 * @return  
	 */ 
	public Integer saveAirStation(AirStation station,String installTimes);

	/**  
	 * <p>Title: findListByParams</p>  
	 * <p>Description: </p>  
	 * @param page
	 * @param condition
	 * @param orderByField
	 * @param asc
	 * @return  
	 */ 
	public List<Map<String, Object>> findListByParams(Page<AirStation> page, String condition, String orderByField,
			boolean asc);

	/**  
	 * <p>Title: deleteAirStationById</p>  
	 * <p>Description: </p>  
	 * @param stationId
	 * @return  
	 */ 
	public int deleteAirStationById(Integer stationId);

	/**  
	 * <p>Title: queryRealTimeData</p>  
	 * <p>Description: </p>  
	 * @param stationCode
	 * @return  
	 */ 
	public Map<String, Object> queryRealTimeData(String stationCode,boolean refresh);

	/**
	 * <p>Title: queryCurrentData</p>  
	 * <p>Description: </p>  
	 * @param station
	 */
	public void queryCurrentData(AirStation station);
}
