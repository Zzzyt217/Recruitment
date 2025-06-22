package com.test.Mapper;

import com.test.Pojo.Position;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface PositionMapper {
    
    @Select("SELECT * FROM position WHERE company_id = #{companyId}")
    List<Position> findByCompanyId(@Param("companyId") Long companyId);
    
    @Select("SELECT * FROM position WHERE id = #{id} AND company_id = #{companyId}")
    Position findByIdAndCompanyId(@Param("id") Long id, @Param("companyId") Long companyId);
    
    @Insert("INSERT INTO position (title, category, salary_range, location, description, requirements, status, company_id, created_at, updated_at) " +
            "VALUES (#{title}, #{category}, #{salaryRange}, #{location}, #{description}, #{requirements}, #{status}, #{companyId}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Position position);
    
    @Update("UPDATE position SET title = #{title}, category = #{category}, salary_range = #{salaryRange}, " +
            "location = #{location}, description = #{description}, requirements = #{requirements}, " +
            "status = #{status}, updated_at = #{updatedAt} " +
            "WHERE id = #{id} AND company_id = #{companyId}")
    int update(Position position);
    
    @Update("UPDATE position SET status = #{status}, updated_at = NOW() WHERE id = #{id} AND company_id = #{companyId}")
    int updateStatus(@Param("id") Long id, @Param("companyId") Long companyId, @Param("status") String status);
    
    @Delete("DELETE FROM position WHERE id = #{id} AND company_id = #{companyId}")
    int deleteById(@Param("id") Long id, @Param("companyId") Long companyId);
    
    // 查询所有已发布的职位
    @Select("SELECT * FROM position WHERE status = 'PUBLISHED' ORDER BY updated_at DESC")
    List<Position> findAllPublished();
    
    // 根据ID查询职位
    @Select("SELECT * FROM position WHERE id = #{id}")
    Position findById(@Param("id") Long id);

} 