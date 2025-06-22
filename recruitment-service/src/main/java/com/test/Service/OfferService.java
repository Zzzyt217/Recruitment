package com.test.Service;

import com.test.Pojo.Offer;
import java.util.Map;

public interface OfferService {
    // 创建录用通知
    Map<String, Object> createOffer(Offer offer);
    
    // 根据ID获取录用通知
    Map<String, Object> getOfferById(Long id);
    
    // 根据申请ID获取录用通知
    Map<String, Object> getOfferByApplicationId(Long applicationId);
    
    // 更新录用通知
    Map<String, Object> updateOffer(Offer offer);
    
    // 更新录用通知状态
    Map<String, Object> updateOfferStatus(Long id, String status);
    
    // 删除录用通知
    boolean deleteOffer(Long id);
} 