package com.test.Mapper;

import com.test.Pojo.Resumes;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ResumeMapper {

    @Select("SELECT * FROM resumes WHERE user_id = #{userId}")
    Resumes findByUserId(@Param("userId") Integer userId);

    @Select("SELECT * FROM resumes WHERE user_id = #{userId} AND base_info_id = #{baseInfoId}")
    Resumes findByUserIdAndBaseInfoId(@Param("userId") Integer userId,
                                      @Param("baseInfoId") Integer baseInfoId);

    @Insert("INSERT INTO resumes(user_id, title, base_info_id, education, experience, skills, status, is_public) " +
            "VALUES(#{userId}, #{title}, #{baseInfoId}, #{education}, #{experience}, #{skills}, #{status}, #{isPublic})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Resumes resume);

    @Update("UPDATE resumes SET title = #{title}, education = #{education}, " +
            "experience = #{experience}, skills = #{skills}, " +
            "status = #{status}, is_public = #{isPublic}, " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE user_id = #{userId} AND base_info_id = #{baseInfoId}")
    int update(Resumes resume);
} 