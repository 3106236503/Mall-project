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
    @Select("SELECT COUNT(*) FROM ums_member_sign_in_record WHERE member_id = #{memberId} AND sign_in_date >= (SELECT IFNULL(MAX(sign_in_date), '1970-01-01') FROM ums_member_sign_in_record WHERE member_id = #{memberId} AND sign_in_date < CURDATE() AND NOT EXISTS (SELECT 1 FROM ums_member_sign_in_record s2 WHERE s2.member_id = #{memberId} AND s2.sign_in_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY))) AND sign_in_date <= CURDATE()")
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