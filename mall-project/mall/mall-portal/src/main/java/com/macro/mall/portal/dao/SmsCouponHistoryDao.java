package com.macro.mall.portal.dao;

import com.macro.mall.model.SmsCoupon;
import com.macro.mall.portal.domain.SmsCouponHistoryDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 会员优惠券领取记录管理自定义Dao
 * Created by macro on 2018/8/29.
 */
@Mapper
public interface SmsCouponHistoryDao {
    /**
     * 获取优惠券历史详情
     */
    @Select("SELECT ch.*, c.name, c.amount, c.min_point, c.platform, c.start_time, c.end_time, c.note, c.use_type, c.type " +
            "FROM sms_coupon_history ch " +
            "LEFT JOIN sms_coupon c ON ch.coupon_id = c.id " +
            "WHERE ch.member_id = #{memberId}")
    List<SmsCouponHistoryDetail> getDetailList(@Param("memberId") Long memberId);

    /**
     * 获取指定会员优惠券列表
     */
    @Select("SELECT c.* " +
            "FROM sms_coupon_history ch " +
            "LEFT JOIN sms_coupon c ON ch.coupon_id = c.id " +
            "WHERE ch.member_id = #{memberId} " +
            "AND ch.use_status = #{useStatus}")
    List<SmsCoupon> getCouponList(@Param("memberId") Long memberId, @Param("useStatus") Integer useStatus);
}
