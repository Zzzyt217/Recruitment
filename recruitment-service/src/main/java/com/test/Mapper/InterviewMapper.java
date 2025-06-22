package com.test.Mapper;

import com.test.Pojo.Interview;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InterviewMapper {
    @Insert("INSERT INTO interview(application_id, interview_time, location, interviewer, status) " +
            "VALUES(#{applicationId}, #{interviewTime}, #{location}, #{interviewer}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Interview interview);
    
    @Select("SELECT * FROM interview WHERE id = #{id}")
    Interview findById(@Param("id") Long id);
    
    @Select("SELECT * FROM interview WHERE application_id = #{applicationId}")
    List<Interview> findByApplicationId(@Param("applicationId") Long applicationId);
    
    @Update("UPDATE interview SET interview_time = #{interviewTime}, location = #{location}, " +
            "interviewer = #{interviewer}, feedback = #{feedback}, status = #{status} WHERE id = #{id}")
    int update(Interview interview);
    
    @Update("UPDATE interview SET feedback = #{feedback}, status = 'COMPLETED' WHERE id = #{id}")
    int updateFeedback(@Param("id") Long id, @Param("feedback") String feedback);
    
    @Delete("DELETE FROM interview WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
} 