package com.test.Mapper;

import com.test.Pojo.Resume;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ResumeMapper {
    // 插入新的简历
    @Insert("INSERT INTO resume(user_id, email, name, phone, desired_position, education, experience, skills) VALUES(#{userId}, #{email}, #{name}, #{phone}, #{desiredPosition}, #{education}, #{experience}, #{skills})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertResume(Resume resume);

    // 根据userId查找简历
    @Select("SELECT id, user_id, email, name, phone, desired_position, education, experience, skills FROM resume WHERE user_id = #{userId}")
    Resume findByUserId(@Param("userId") Integer userId);

    // 更新简历
    @Update("UPDATE resume SET name = #{name}, phone = #{phone}, desired_position = #{desiredPosition}, education = #{education}, experience = #{experience}, skills = #{skills} WHERE id = #{id}")
    int updateResume(Resume resume);
} 