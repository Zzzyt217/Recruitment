package com.test.Service;

import com.test.Mapper.ResumeMapper;
import com.test.Pojo.Resume;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class ResumeService {
    
    @Resource
    private ResumeMapper resumeMapper;

    public void syncResume(Resume resume) {
        System.out.println("收到简历同步请求: " + resume);
        
        Resume existing = resumeMapper.findByUserIdAndBaseInfoId(
            resume.getUserId(), resume.getBaseInfoId());
            
        if (existing == null) {
            // 如果不存在，创建新简历
            System.out.println("创建新简历记录");
            resume.setStatus("PUBLISHED");  // 从jobseeker同步的简历默认为已发布
            resume.setIsPublic(true);       // 默认公开
            // 如果没有传入标题，才设置默认标题
            if (resume.getTitle() == null || resume.getTitle().trim().isEmpty()) {
                resume.setTitle("我的简历");
            }
            int result = resumeMapper.insert(resume);
            System.out.println("插入结果: " + result);
        } else {
            // 如果存在，更新简历
            System.out.println("更新已有简历记录");
            resume.setStatus(existing.getStatus());
            resume.setIsPublic(existing.getIsPublic());
            // 如果没有传入标题，才使用原有标题
            if (resume.getTitle() == null || resume.getTitle().trim().isEmpty()) {
                resume.setTitle(existing.getTitle());
            }
            int result = resumeMapper.update(resume);
            System.out.println("更新结果: " + result);
        }
    }
} 