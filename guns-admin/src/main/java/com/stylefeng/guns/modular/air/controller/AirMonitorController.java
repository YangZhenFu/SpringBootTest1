package com.stylefeng.guns.modular.air.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.core.base.controller.BaseController;
import com.stylefeng.guns.core.common.constant.factory.PageFactory;
import com.stylefeng.guns.modular.air.model.AirStation;
import com.stylefeng.guns.modular.air.service.IAirStationService;

/**  
 * <p>Title: AirMonitorController</p>  
 * <p>Description: </p> 
 * @author YangZhenfu  
 * @date 2018年4月20日  
 */
@RequestMapping("air/monitor")
@Controller
public class AirMonitorController extends BaseController{

	private String PREFIX = "/air/monitor/";
	
	@Autowired
	private IAirStationService airStationService;
	
	 /**
     * 跳转到监控首页
     */
    @RequestMapping("")
    public String index(Model model) {
        return PREFIX + "monitor.html";
    }
    
    @RequestMapping(value="list",method=RequestMethod.POST)
    @ResponseBody
    public Object list(@RequestParam(required=false)String condition){
    	
    	Page<AirStation> page=new PageFactory<AirStation>().defaultPage();
    	page=airStationService.selectPage(page, new EntityWrapper<AirStation>().like("t_name", condition).eq("valid", "0"));
    	return packForBT(page);
    }
    
    
    
    /**
     * 
     * <p>Title: queryRealTimeData</p>  
     * <p>Description: 查询气象站实时数据</p>  
     * @param stationCode
     * @return
     */
    @RequestMapping(value="queryData",method=RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> queryRealTimeData(@RequestParam(required=true) String stationCode,@RequestParam(required=false)Boolean refresh){
    	return airStationService.queryRealTimeData(stationCode,refresh);
    }
    
    

}
