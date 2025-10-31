package com.macro.mall.portal.service;


import com.macro.mall.portal.model.UmsMemberSignInRecord;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface UmsMemberSignInService {

    /**
     * 正常签到
     */
    Map<String, Object> signIn();

    /**
     * 补签
     */
    Map<String, Object> makeUpSignIn(Date signInDate);

    /**
     * 查询会员签到记录
     */
    List<UmsMemberSignInRecord> getSignInRecords(Integer year, Integer month);

    /**
     * 查询连续签到天数
     */
    Integer getContinuousSignInDays();

    /**
     * 查询今日是否已签到
     */
    boolean isSignedInToday();


    /**
     * 获取会员积分
     */
    Map<String, Object> getMemberPoints(Long id);


    /**
     * 查询最近一次签到记录
     */
    UmsMemberSignInRecord getLatestSignInRecord();

    /**
     * 获取会员签到记录
     */
    List<UmsMemberSignInRecord> getAll(Long id);

    /**
     * 查询累计签到总天数
     */
    Integer getTotalSignDays(Long memberId);


}