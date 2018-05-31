package com.stylefeng.guns.modular.air.service.impl;

import java.net.DatagramSocket;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.stylefeng.guns.core.common.constant.Constant;
import com.stylefeng.guns.core.common.constant.SensorTypeEnum;
import com.stylefeng.guns.core.common.constant.WindDirection;
import com.stylefeng.guns.core.other.CronUtil;
import com.stylefeng.guns.core.other.StringUtil;
import com.stylefeng.guns.core.shiro.ShiroKit;
import com.stylefeng.guns.core.util.Contrast;
import com.stylefeng.guns.core.util.Convert;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.modular.air.dao.AirSensorAlarmInfoMapper;
import com.stylefeng.guns.modular.air.dao.AirSensorMapper;
import com.stylefeng.guns.modular.air.dao.AirStationDataMapper;
import com.stylefeng.guns.modular.air.dao.AirStationMapper;
import com.stylefeng.guns.modular.air.dao.AirTaskMapper;
import com.stylefeng.guns.modular.air.dao.SensorTypeMapper;
import com.stylefeng.guns.modular.air.dto.AirSensorDataDto;
import com.stylefeng.guns.modular.air.model.AirSensor;
import com.stylefeng.guns.modular.air.model.AirSensorAlarmInfo;
import com.stylefeng.guns.modular.air.model.AirStation;
import com.stylefeng.guns.modular.air.model.AirStationData;
import com.stylefeng.guns.modular.air.model.AirTask;
import com.stylefeng.guns.modular.air.model.SensorType;
import com.stylefeng.guns.modular.air.service.IAirStationService;
import com.stylefeng.guns.modular.air.task.GetUdpDataTask;
import com.stylefeng.guns.modular.air.task.JobTask;
import com.stylefeng.guns.modular.air.warpper.AirSensorAlarmInfoWarpper;

import cn.hutool.core.util.NumberUtil;

/**
 * <p>
 * 气象站表 服务实现类
 * </p>
 *
 * @author YangZhenfu123
 * @since 2018-04-17
 */
@Service
public class AirStationServiceImpl extends ServiceImpl<AirStationMapper, AirStation> implements IAirStationService {

	@Autowired
	private AirTaskMapper airTaskMapper;
	@Autowired
	private JobTask jobTask;
	
	@Autowired
	private AirSensorMapper airSensorMapper;
	@Autowired
	private SensorTypeMapper sensorTypeMapper;
	@Autowired
	private AirStationDataMapper airStationDataMapper;
	@Autowired
	private AirSensorAlarmInfoMapper sensorAlarmInfoMapper;
	
	private static Logger logger = LoggerFactory.getLogger(AirStationServiceImpl.class);
	
	@Override
	public List<Map<String, Object>> findListByParams(Page<AirStation> page, String condition, String orderByField,
			boolean asc) {
		return baseMapper.selectByParams(page,condition,orderByField,asc);
	}

	@Override
	public Integer saveAirStation(AirStation station,String installTimes) {
		int count=0;
		if(station!=null){
			
			if(StringUtils.isNotBlank(installTimes)){
				station.setInstallTime(DateUtil.parse(installTimes, "yyyy-MM-DD"));
			}
			//新增
			if(station.getId()==null){
				station.setCode(StringUtil.generatorShort());
				station.setCreateBy(ShiroKit.getUser().getName());
				station.setCreateTime(new Date());
				count=baseMapper.insert(station);
				
				//新增气象站数据上传任务
				AirTask task = new AirTask();
				task.setId(StringUtil.generator());
				task.setJobName("气象站 -->"+station.gettName()+" 数据上传定时");
				task.setJobDesc("气象站 -->"+station.gettName()+" 数据上传定时任务, 每隔"+station.getDataUploadInterval()+"分钟执行一次");
				task.setDeviceCode(station.getCode());
				task.setCron(CronUtil.generateMinuteCron(station.getDataUploadInterval()));
				task.setClazzPath("com.stylefeng.guns.modular.air.task.jobs.AirDataUploadJob");
				task.setStatus(station.getDataUploadStatus());
				task.setCreateBy(ShiroKit.getUser().getName());
				task.setCreateTime(new Date());
				airTaskMapper.insert(task);
				//如果为启动状态
				if(task.getStatus()==0){
					//启动任务
					jobTask.startJob(task);
				}
				
			}
			//修改
			else{
				station.setUpdateBy(ShiroKit.getUser().getName());
				station.setUpdateTime(new Date());
				count=baseMapper.updateById(station);
				
				//查询气象站
				AirStation airStation = baseMapper.selectById(station.getId());
				
				//查询气象站数据上传任务
				AirTask taskQuery=new AirTask();
				taskQuery.setDeviceCode(airStation.getCode());
				AirTask task = airTaskMapper.selectOne(taskQuery);
				if(task!=null){
					task.setJobName("气象站 -->"+airStation.gettName()+" 数据上传定时");
					task.setJobDesc("气象站 -->"+airStation.gettName()+" 数据上传定时任务, 每隔"+airStation.getDataUploadInterval()+"分钟执行一次");
					task.setCron(CronUtil.generateMinuteCron(airStation.getDataUploadInterval()));
					task.setUpdateBy(ShiroKit.getUser().getName());
					task.setUpdateTime(new Date());
					task.setStatus(airStation.getDataUploadStatus());
					airTaskMapper.updateById(task);
					
					//先将任务移除
					jobTask.remove(task);
					//启动
					if(airStation.getDataUploadStatus()==0){
						jobTask.startJob(task);
					}
				}
				
			}
		}
		return count;
	}

	
	@Override
	public int deleteAirStationById(Integer stationId) {
		int count=0;
		if(stationId!=null){
			AirStation station = baseMapper.selectById(stationId);
			if(station!=null){
				station.setValid("1");
				station.setUpdateBy(ShiroKit.getUser().getName());
				station.setUpdateTime(new Date());
				count=baseMapper.updateById(station);
				
				//查询气象站数据上传任务
				AirTask taskQuery=new AirTask();
				taskQuery.setDeviceCode(station.getCode());
				AirTask task = airTaskMapper.selectOne(taskQuery);
				if(task!=null){
					airTaskMapper.deleteById(task.getId());
					//将任务移除
					jobTask.remove(task);
					
				}
			}
		}
		return count;
	}

	/* (non-Javadoc)  
	 * <p>Title: queryRealTimeData</p>  
	 * <p>Description: </p>  
	 * @param stationCode
	 * @return  
	 * @see com.stylefeng.guns.modular.air.service.IAirStationService#queryRealTimeData(java.lang.String)  
	 */
	@Override
	public Map<String, Object> queryRealTimeData(String stationCode,boolean refresh) {
		Map<String,Object> result=Maps.newHashMap();
    	if(StringUtils.isNotBlank(stationCode)){
    		//查询气象站
    		 List<AirStation> list = baseMapper.selectList(new EntityWrapper<AirStation>().eq("code", stationCode).eq("valid", "0"));
    		if(CollectionUtils.isNotEmpty(list)){
    			AirStation station = list.get(0);
    			
    			//是否查询当前数据
    			if(refresh){
    				queryCurrentData(station);
    			}
    			
    			//查询所有的传感器
    			List<AirSensor> sensors = airSensorMapper.selectList(new EntityWrapper<AirSensor>().eq("station_id", station.getId()).eq("valid", "0"));
    			if(CollectionUtils.isNotEmpty(sensors)){
    				List<AirSensorDataDto> dtos=Lists.newArrayList();
    				
    				
    				//查询全部数据
    				List<AirStationData> allData = airStationDataMapper.selectList(new EntityWrapper<AirStationData>().eq("station_id", station.getId()).orderBy("heartbeat_time desc"));
    				
    				//查询24小时数据
    				List<AirStationData> data = airStationDataMapper.findOneDayData(station.getId());
        				for(AirSensor sensor : sensors){
        					//查询传感器类型
        					SensorType type = sensorTypeMapper.selectById(sensor.getTypeId());
        					
        					//传感器数据类型
        					SensorTypeEnum typeEnum = SensorTypeEnum.findSensorTypeByName(type.gettName());
        					String code = typeEnum.getCode();
        					
        					AirStationData nowData = null;//当前数值
        					AirStationData minData = null;//最大数值
    						AirStationData maxData = null;//最小数值
        					
    						if(CollectionUtils.isNotEmpty(allData)){
    							//当前数值
            					nowData = allData.get(0);
    						}
    						
        					if(CollectionUtils.isNotEmpty(data) ){
        						
        						if(StringUtils.isNotBlank(code) && !type.gettName().contains("风向")){
        							//按传感器数据类型排序
        							Collections.sort(data, new Comparator<AirStationData>() {
            							@Override
            							public int compare(AirStationData o1, AirStationData o2) {
            								Double num1=Convert.toDouble(SensorTypeEnum.findDataBySensorType(code, o1));
            								Double num2 = Convert.toDouble(SensorTypeEnum.findDataBySensorType(code, o2));
            								if(num1==null || num2==null){
            									return 0;
            								}
            								return NumberUtil.compare(num1, num2);
            							}
            						});
        						}
        						
        					    minData = data.get(0);//最大数值
        						maxData = data.get(data.size()-1);//最小数值
        					}
        					
        						AirSensorDataDto dto =new AirSensorDataDto();
            					dto.setId(StringUtil.generatorShort());
            					dto.setCode(sensor.getCode());
            					dto.settName(sensor.gettName());
            					dto.setTypeName(type.gettName());
            					dto.setUnit(sensor.getUnit());
            					dto.setIcon(typeEnum.getIcon());
            					if(maxData!=null){
            						
            						if(dto.getTypeName().contains("风向")){
            							WindDirection direction = WindDirection.findWindDirectionByMark(String.valueOf(SensorTypeEnum.findDataBySensorType(code, maxData)));
            							if(direction!=null){
            								dto.setMaxNumerical(direction.getMsg());
            							}
            						}else{
            							dto.setMaxNumerical(String.valueOf(SensorTypeEnum.findDataBySensorType(code, maxData)));
            						}
            						dto.setMaxTime(maxData.getHeartbeatTime());
            					}
            					if(minData!=null){
            						if(dto.getTypeName().contains("风向")){
            							WindDirection direction = WindDirection.findWindDirectionByMark(String.valueOf(SensorTypeEnum.findDataBySensorType(code, minData)));
            							if(direction!=null){
            								dto.setMinNumerical(direction.getMsg());
            							}
            						}else{
            							dto.setMinNumerical(String.valueOf(SensorTypeEnum.findDataBySensorType(code, minData)));
            						}
            						dto.setMinTime(minData.getHeartbeatTime());
            					}
            					if(nowData!=null){
            						if(dto.getTypeName().contains("风向")){
            							WindDirection direction = WindDirection.findWindDirectionByMark(String.valueOf(SensorTypeEnum.findDataBySensorType(code, nowData)));
            							if(direction!=null){
            								dto.setNowNumerical(direction.getMsg());
            							}
            						}else{
            							dto.setNowNumerical(String.valueOf(SensorTypeEnum.findDataBySensorType(code, nowData)));
            						}
            					}
            					dtos.add(dto);
    				}
        			result.put("data", dtos);
    				if(CollectionUtils.isNotEmpty(allData)){
    					result.put("refreshTime", allData.get(0).getHeartbeatTime());
    				}
    				return result;
    				
    				
    			}
    			
    		}
    	}
    	
    	
    	return result;
	}

	
	/**
	 * <p>Title: queryCurrentData</p>  
	 * <p>Description: 查询气象站当前数据</p>  
	 * @param station
	 */
	public void queryCurrentData(AirStation station){
		//查询所有的传感器
		List<AirSensor> sensors = airSensorMapper.selectList(new EntityWrapper<AirSensor>().eq("station_id", station.getId()));
		if(CollectionUtils.isNotEmpty(sensors)){
			GetUdpDataTask task = new GetUdpDataTask(station.getIpAddr(), Integer.parseInt(station.getPort()));
			DatagramSocket socket=null;
			ExecutorService service = Executors.newCachedThreadPool();
			try {
				socket = new DatagramSocket();
				
				AirStationData data=new AirStationData();
				for(AirSensor sensor : sensors){
					//获取传感器查询指令
					String command = sensor.getCommand();
					if(StringUtils.isNotBlank(command)){
						//发送查询指令
						Future<String> future = service.submit(task.new GetUdpDataThread(socket, command));
						
						Object[] status = task.getEntity(future.get(), data,sensor);
						if(!Convert.toBool(status[0])){
							
							//TODO 传感器离线   新增传感器报警信息
							//查询报警信息是否存在
							List<AirSensorAlarmInfo> alarms = sensorAlarmInfoMapper.selectList(new EntityWrapper<AirSensorAlarmInfo>().eq("sensor_id", sensor.getId()).eq("valid", "0").eq("alarm_type", "0").eq("handle_state", "0"));
							if(CollectionUtils.isEmpty(alarms)){
								//新增传感器报警信息
								AirSensorAlarmInfo alarm=new AirSensorAlarmInfo();
								alarm.settName(sensor.gettName()+"-"+Constant.sensor_exception_type.get("0"));
								alarm.setSensorId(sensor.getId());
								alarm.setAlarmType("0");//设备离线
								alarm.setAlarmInfo(String.format("站点[%s]，传感器[%s]设备离线，请检查", station.gettName(),sensor.gettName()));
								alarm.setAlarmTime(new Date());
								alarm.setCode(StringUtil.generatorShort());
								alarm.setCreateTime(new Date());
								sensorAlarmInfoMapper.insert(alarm);
								
							}
							
							
							//更新传感器状态
							sensor.setStatus("2");//通讯故障
							sensor.setUpdateTime(new Date());
							airSensorMapper.updateById(sensor);
							
							continue;
						}else{
							//更新传感器状态
							sensor.setStatus("0");//正常
							sensor.setUpdateTime(new Date());
							airSensorMapper.updateById(sensor);
							
							//更改传感器报警信息状态
							//查询报警信息是否存在
							List<AirSensorAlarmInfo> alarms = sensorAlarmInfoMapper.selectList(new EntityWrapper<AirSensorAlarmInfo>().eq("sensor_id", sensor.getId()).eq("valid", "0").eq("alarm_type", "0").eq("handle_state", "0"));
							if(CollectionUtils.isNotEmpty(alarms)){
								for(AirSensorAlarmInfo info : alarms){
									info.setHandleState("1");//已恢复
									info.setHandleContent("自动恢复");
									info.setHandleTime(new Date());
									sensorAlarmInfoMapper.updateById(info);
								}
							}
						}
					}
					
				}
				//如果数据不为空  则存入数据库
				if(!Contrast.isAllFieldNull(data)){
					//将数据存入数据库
					data.setStationId(station.getId());
					data.setHeartbeatTime(new Date());
					data.settName(station.gettName()+"-"+com.stylefeng.guns.core.other.DateUtil.formatFullDateTime(new Date()));
					airStationDataMapper.insert(data);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage(), e);
			} finally {
				service.shutdown();
				if(socket!=null){
					socket.close();
				}
			}
			
			
		}
	}
	
	
}
