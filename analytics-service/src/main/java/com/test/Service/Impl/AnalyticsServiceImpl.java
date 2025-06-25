package com.test.Service.Impl;

import com.test.Client.CompanyClient;
import com.test.Client.JobseekerClient;
import com.test.Client.RecruitmentClient;
import com.test.Pojo.DashboardStatsDTO;
import com.test.Service.AnalyticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据分析服务实现类
 */
@Slf4j
@Service
public class AnalyticsServiceImpl implements AnalyticsService {

    @Autowired
    private JobseekerClient jobseekerClient;

    @Autowired
    private CompanyClient companyClient;

    @Autowired
    private RecruitmentClient recruitmentClient;

    /**
     * 获取仪表盘统计数据
     * @param token 认证令牌
     * @param userId 用户ID
     * @return 仪表盘统计数据
     */
    @Override
    public DashboardStatsDTO getDashboardStats(String token, String userId) {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        try {
            // 获取求职者数量
            Integer jobseekerCount = jobseekerClient.getJobseekerCount(token, userId);
            stats.setTotalJobseekers(jobseekerCount);
        } catch (Exception e) {
            // 保持默认值0
        }

        try {
            // 获取企业数量
            Integer companyCount = companyClient.getCompanyCount(token, userId);
            stats.setTotalCompanies(companyCount);
        } catch (Exception e) {
            // 保持默认值0
        }

        try {
            // 获取职位数量
            Integer positionCount = companyClient.getPositionCount(token, userId);
            stats.setTotalPositions(positionCount);
        } catch (Exception e) {
            // 保持默认值0
        }

        try {
            // 获取成功招聘数量
            Integer successfulHireCount = recruitmentClient.getSuccessfulHireCount(token, userId);
            stats.setTotalSuccessfulHires(successfulHireCount);
        } catch (Exception e) {
            // 保持默认值0
        }

        return stats;
    }
}