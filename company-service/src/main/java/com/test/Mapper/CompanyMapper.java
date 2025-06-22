package com.test.Mapper;

import com.test.Pojo.Company;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CompanyMapper {
    // 插入新的企业信息
    @Insert("INSERT INTO company(user_id, name, industry, scale, address, description, verified) VALUES(#{userId}, #{name}, #{industry}, #{scale}, #{address}, #{description}, #{verified})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertCompany(Company company);

    // 根据userId查找企业信息
    @Select("SELECT id, user_id as userId, name, industry, scale, address, description, verified FROM company WHERE user_id = #{userId}")
    Company findByUserId(@Param("userId") Integer userId);

    // 根据id查找企业信息
    @Select("SELECT id, user_id as userId, name, industry, scale, address, description, verified FROM company WHERE id = #{id}")
    Company findById(@Param("id") Long id);

    // 更新企业信息
    @Update("UPDATE company SET name = #{name}, industry = #{industry}, scale = #{scale}, address = #{address}, description = #{description} WHERE id = #{id}")
    int updateCompany(Company company);
} 