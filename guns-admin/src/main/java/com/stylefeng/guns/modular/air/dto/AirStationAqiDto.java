package com.stylefeng.guns.modular.air.dto;

import java.io.Serializable;


/**  
 * <p>Title: AirSensorDataDto</p>  
 * <p>Description: 数据显示封装类</p>  
 * @author YangZhenfu  
 * @date 2018年4月26日  
 */
public class AirStationAqiDto implements Serializable{

	/** serialVersionUID*/
	private static final long serialVersionUID = -8723588759164777396L;

    /**
     * 记录总数
     */
    private Integer countNumber;
    
    /**
     * 检测日期
     */
    private String dateTime;
    
    /**
     * 平均空气质量指数
     */
    private Integer AQI;
    
    /**
     * 空气质量等级
     */
    private Integer airGrade;
    
    /**
     * 首要污染物
     */
    private String primaryPollutant;
    
    /**
     * PM_10（单位：μm/m3）
     */
    private String pm10;
    /**
     * PM_2.5（单位：μm/m3）
     */
    private String pm25;
    
    /**
     * CO浓度（单位：ppm）
     */
    private String co;
    /**
     * O3浓度（单位：ppm）
     */
    private String o3;
    /**
     * SO2浓度（单位：ppm）
     */
    private String so2;
    /**
     * NO2浓度（单位：ppm）
     */
    private String no2;
   

	/**
	 * @return the aQI  
	 */
	public Integer getAQI() {
		return AQI;
	}

	/**
	 * @param aQI the aQI to set
	 */
	public void setAQI(Integer aQI) {
		AQI = aQI;
	}

	/**
	 * @return the airGrade  
	 */
	public Integer getAirGrade() {
		return airGrade;
	}

	/**
	 * @param airGrade the airGrade to set
	 */
	public void setAirGrade(Integer airGrade) {
		this.airGrade = airGrade;
	}

	/**
	 * @return the pm10  
	 */
	public String getPm10() {
		return pm10;
	}

	/**
	 * @param pm10 the pm10 to set
	 */
	public void setPm10(String pm10) {
		this.pm10 = pm10;
	}

	/**
	 * @return the pm25  
	 */
	public String getPm25() {
		return pm25;
	}

	/**
	 * @param pm25 the pm25 to set
	 */
	public void setPm25(String pm25) {
		this.pm25 = pm25;
	}

	/**
	 * @return the co  
	 */
	public String getCo() {
		return co;
	}

	/**
	 * @param co the co to set
	 */
	public void setCo(String co) {
		this.co = co;
	}

	/**
	 * @return the o3  
	 */
	public String getO3() {
		return o3;
	}

	/**
	 * @param o3 the o3 to set
	 */
	public void setO3(String o3) {
		this.o3 = o3;
	}

	/**
	 * @return the so2  
	 */
	public String getSo2() {
		return so2;
	}

	/**
	 * @param so2 the so2 to set
	 */
	public void setSo2(String so2) {
		this.so2 = so2;
	}

	/**
	 * @return the no2  
	 */
	public String getNo2() {
		return no2;
	}

	/**
	 * @param no2 the no2 to set
	 */
	public void setNo2(String no2) {
		this.no2 = no2;
	}

	/**
	 * @return the countNumber  
	 */
	public Integer getCountNumber() {
		return countNumber;
	}

	/**
	 * @param countNumber the countNumber to set
	 */
	public void setCountNumber(Integer countNumber) {
		this.countNumber = countNumber;
	}

	/**
	 * @return the dateTime  
	 */
	public String getDateTime() {
		return dateTime;
	}

	/**
	 * @param dateTime the dateTime to set
	 */
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	

	/**
	 * @return the primaryPollutant  
	 */
	public String getPrimaryPollutant() {
		return primaryPollutant;
	}

	/**
	 * @param primaryPollutant the primaryPollutant to set
	 */
	public void setPrimaryPollutant(String primaryPollutant) {
		this.primaryPollutant = primaryPollutant;
	}

	/* (non-Javadoc)  
	 * <p>Title: toString</p>  
	 * <p>Description: </p>  
	 * @return  
	 * @see java.lang.Object#toString()  
	 */
	@Override
	public String toString() {
		return "AirStationAqiDto [countNumber=" + countNumber + ", dateTime=" + dateTime + ", AQI=" + AQI
				+ ", airGrade=" + airGrade + ", primaryPollutant=" + primaryPollutant + ", pm10=" + pm10 + ", pm25="
				+ pm25 + ", co=" + co + ", o3=" + o3 + ", so2=" + so2 + ", no2=" + no2 + "]";
	}

	
}
