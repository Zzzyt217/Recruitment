package com.test.Pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仪表盘统计数据传输对象
 * 用于封装首页展示的统计信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    /**
     * 求职者总数
     */
    private Integer totalJobseekers = 0;

    public Integer getTotalJobseekers() {
        return totalJobseekers;
    }

    public void setTotalJobseekers(Integer totalJobseekers) {
        this.totalJobseekers = totalJobseekers;
    }

    public Integer getTotalCompanies() {
        return totalCompanies;
    }

    public void setTotalCompanies(Integer totalCompanies) {
        this.totalCompanies = totalCompanies;
    }

    public Integer getTotalPositions() {
        return totalPositions;
    }

    public void setTotalPositions(Integer totalPositions) {
        this.totalPositions = totalPositions;
    }

    public Integer getTotalSuccessfulHires() {
        return totalSuccessfulHires;
    }

    public void setTotalSuccessfulHires(Integer totalSuccessfulHires) {
        this.totalSuccessfulHires = totalSuccessfulHires;
    }

    /**
     * 企业总数
     */
    private Integer totalCompanies = 0;
    
    /**
     * 职位总数
     */
    private Integer totalPositions = 0;

    /**
     * 成功招聘数（已录用的申请）
     */
    private Integer totalSuccessfulHires = 0;
    
    @Override
    public String toString() {
        return "DashboardStatsDTO{" +
                "totalJobseekers=" + totalJobseekers +
                ", totalCompanies=" + totalCompanies +
                ", totalPositions=" + totalPositions +
                ", totalSuccessfulHires=" + totalSuccessfulHires +
                '}';
    }
} 