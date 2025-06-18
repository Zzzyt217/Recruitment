package com.test.Service;

/**
 * 简历导出服务接口
 */
public interface ResumeExportService {
    /**
     * 将简历导出为PDF
     * @param userId 用户ID
     * @return PDF文件的字节数组
     */
    byte[] exportToPdf(Long userId);
} 