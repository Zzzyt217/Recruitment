package com.test.Mapper;

import com.test.Pojo.Application;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface ApplicationMapper {
    @Insert("INSERT INTO application(user_id, position_id, company_id, resume_id, cover_letter, status, created_at, updated_at) " +
            "VALUES(#{userId}, #{positionId}, #{companyId}, #{resumeId}, #{coverLetter}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Application application);
    
    @Select("SELECT * FROM application WHERE id = #{id}")
    Application findById(@Param("id") Long id);
    
    @Select("SELECT a.*, p.title as positionTitle, p.location as positionLocation, c.name as companyName " +
           "FROM application a " +
           "LEFT JOIN company1_db.position p ON a.position_id = p.id " +
           "LEFT JOIN company1_db.company c ON p.company_id = c.id " +
           "WHERE a.user_id = #{userId} " +
           "ORDER BY a.created_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "positionId", column = "position_id"),
        @Result(property = "companyId", column = "company_id"),
        @Result(property = "resumeId", column = "resume_id"),
        @Result(property = "coverLetter", column = "cover_letter"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "positionTitle", column = "positionTitle"),
        @Result(property = "companyName", column = "companyName")
    })
    List<Application> findByUserId(@Param("userId") Integer userId);
    
    @Select("SELECT a.*, u.username as userName, p.title as positionTitle " +
           "FROM application a " +
           "LEFT JOIN recruitment.users u ON a.user_id = u.id " +
           "LEFT JOIN company1_db.position p ON a.position_id = p.id " +
           "WHERE a.company_id = #{companyId} " +
           "ORDER BY a.created_at DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "positionId", column = "position_id"),
        @Result(property = "companyId", column = "company_id"),
        @Result(property = "resumeId", column = "resume_id"),
        @Result(property = "coverLetter", column = "cover_letter"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "userName", column = "userName"),
        @Result(property = "positionTitle", column = "positionTitle")
    })
    List<Application> findByCompanyId(@Param("companyId") Long companyId);
    
    @Select("SELECT * FROM application WHERE position_id = #{positionId} ORDER BY created_at DESC")
    List<Application> findByPositionId(@Param("positionId") Long positionId);
    
    @Update("UPDATE application SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status, @Param("updatedAt") Date updatedAt);
    
    @Delete("DELETE FROM application WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    @Select("SELECT COUNT(*) FROM application WHERE user_id = #{userId} AND position_id = #{positionId}")
    int checkUserApplied(@Param("userId") Integer userId, @Param("positionId") Long positionId);
} 