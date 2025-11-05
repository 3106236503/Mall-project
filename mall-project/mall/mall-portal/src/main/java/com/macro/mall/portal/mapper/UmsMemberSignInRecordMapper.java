package com.macro.mall.portal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macro.mall.portal.model.UmsMemberSignInRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface UmsMemberSignInRecordMapper extends BaseMapper<UmsMemberSignInRecord> {

    /**
     * 根据会员ID和日期范围查询签到记录
     */

    @Select("SELECT * FROM ums_member_sign_in_record WHERE member_id = #{memberId}")
    List<UmsMemberSignInRecord> selectByMemberIdAndDateRange(@Param("memberId") Long memberId,
                                                             @Param("startDate") Date startDate,
                                                             @Param("endDate") Date endDate);

    /**
     * 查询会员连续签到天数
     */
    @Select("SELECT COUNT(*) FROM (" +
            "SELECT sign_in_date, " +
            "@row_number := @row_number + 1 AS rn, " +
            "DATEDIFF(CURDATE(), sign_in_date) AS diff " +
            "FROM ums_member_sign_in_record, (SELECT @row_number := 0) AS r " +
            "WHERE member_id = #{memberId} " +
            "ORDER BY sign_in_date DESC" +
            ") AS t " +
            "WHERE rn - diff = 1")
    Integer selectContinuousSignInDays(@Param("memberId") Long memberId);


    /**
     * 查询会员某月签到记录
     */
    @Select("SELECT * FROM ums_member_sign_in_record WHERE member_id = #{memberId} AND YEAR(sign_in_date) = #{year} AND MONTH(sign_in_date) = #{month}")
    List<UmsMemberSignInRecord> selectByMemberIdAndMonth(@Param("memberId") Long memberId,
                                                         @Param("year") Integer year,
                                                         @Param("month") Integer month);


    /**
     * 查询会员最近一次签到记录
     */
    @Select("SELECT * FROM ums_member_sign_in_record WHERE member_id = #{memberId} ORDER BY sign_in_date DESC LIMIT 1")
    UmsMemberSignInRecord selectLatestSignInRecord(@Param("memberId") Long memberId);


}