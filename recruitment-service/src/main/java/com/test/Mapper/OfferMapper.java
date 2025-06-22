package com.test.Mapper;

import com.test.Pojo.Offer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OfferMapper {
    @Insert("INSERT INTO offer(application_id, salary, benefits, start_date, status, created_at) " +
            "VALUES(#{applicationId}, #{salary}, #{benefits}, #{startDate}, #{status}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Offer offer);
    
    @Select("SELECT * FROM offer WHERE id = #{id}")
    Offer findById(@Param("id") Long id);
    
    @Select("SELECT * FROM offer WHERE application_id = #{applicationId}")
    Offer findByApplicationId(@Param("applicationId") Long applicationId);
    
    @Update("UPDATE offer SET salary = #{salary}, benefits = #{benefits}, " +
            "start_date = #{startDate}, status = #{status} WHERE id = #{id}")
    int update(Offer offer);
    
    @Update("UPDATE offer SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    @Delete("DELETE FROM offer WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
} 