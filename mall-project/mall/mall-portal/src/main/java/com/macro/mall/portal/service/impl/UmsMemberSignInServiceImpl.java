package com.macro.mall.portal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.mapper.UmsMemberMapperPlus;
import com.macro.mall.portal.mapper.UmsMemberSignInRecordMapper;
import com.macro.mall.portal.model.UmsMemberSignInRecord;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.service.UmsMemberSignInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class UmsMemberSignInServiceImpl implements UmsMemberSignInService {

    @Autowired
    private UmsMemberSignInRecordMapper signInRecordMapper;

    @Autowired
    private UmsMemberMapperPlus memberMapper;

    @Autowired
    private UmsMemberService memberService;

    // 基础签到积分
    private static final int BASE_INTEGRATION = 10;
    // 补签消耗积分
    private static final int MAKE_UP_COST = 50;
    // 连续签到奖励积分（每7天）
    private static final int CONTINUOUS_REWARD = 20;

    @Override
    @Transactional
    public Map<String, Object> signIn() {


        Map<String, Object> result = new HashMap<>();

        // 获取当前登录会员
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        Long memberId = currentMember.getId();

        // 检查今天是否已经签到
        if (isSignedInToday()) {
            result.put("success", false);
            result.put("message", "今天已经签到过了");
            return result;
        }

        // 检查今日是否已签到
        if (isSignedInToday()) {
            result.put("success", false);
            result.put("message", "今日已签到");
            return result;
        }

        // 计算连续签到天数
        Integer continuousDays = getContinuousSignInDays();
        int currentContinuousDays = continuousDays != null ? continuousDays : 0;

        // 计算本次签到获得的积分
        int integration = BASE_INTEGRATION;
        if ((currentContinuousDays + 1) % 7 == 0) {
            integration += CONTINUOUS_REWARD;
        }


        UmsMemberSignInRecord lastRecord = signInRecordMapper.selectLatestSignInRecord(memberId);

        // 添加空值检查
        int lastIntegration = (lastRecord != null) ? lastRecord.getIntegration() : 0;
        int lastGrowth = (lastRecord != null) ? lastRecord.getGrowth() : 0;

        // 计算总积分和总成长值
        int totalIntegration = lastIntegration + integration;
        int totalGrowth = lastGrowth + 1;

        try {
            // 创建签到记录
            UmsMemberSignInRecord record = new UmsMemberSignInRecord();
            record.setMemberId(memberId);
            record.setSignInDate(new Date());
            record.setSignInType(0); // 正常签到
            record.setIntegration(integration);
            record.setGrowth(1); // 每次签到获得1成长值
            record.setTotalIntegration(totalIntegration); // 总积分
            record.setTotalGrowth(totalGrowth); // 总成长值
            record.setCreateTime(new Date());

            signInRecordMapper.insert(record);

            // 更新会员积分和成长值
            UmsMember updateMember = new UmsMember();
            updateMember.setId(memberId);
            updateMember.setIntegration(totalIntegration);
            updateMember.setGrowth(totalGrowth);

            memberMapper.updateById(updateMember);
            // 清除缓存，保持数据一致性
            memberService.updateIntegration(memberId, totalIntegration);

            result.put("success", true);
            result.put("integration", integration);
            result.put("continuousDays", currentContinuousDays + 1);
            result.put("totalIntegration", totalIntegration);
            result.put("totalGrowth", totalGrowth);
            result.put("message", "签到成功");

        } catch (DataAccessException e) {
            // 处理数据库访问异常
            if (e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
                result.put("success", false);
                result.put("message", "今天已经签到过了，请勿重复签到");
            } else {
                result.put("success", false);
                result.put("message", "签到失败，请稍后重试");
            }
        } catch (Exception e) {
            // 处理其他异常
            result.put("success", false);
            result.put("message", "签到失败，请稍后重试");
        }
        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> makeUpSignIn(Date signInDate) {
        Map<String, Object> result = new HashMap<>();

        // 获取当前登录会员
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            result.put("success", false);
            result.put("message", "请先登录");
            return result;
        }

        Long memberId = currentMember.getId();
        UmsMemberSignInRecord lastRecord = signInRecordMapper.selectLatestSignInRecord(memberId);


        // 检查积分是否足够
        if (lastRecord.getTotalIntegration() < MAKE_UP_COST) {
            result.put("success", false);
            result.put("message", "积分不足，补签需要消耗" + MAKE_UP_COST + "积分");
            return result;
        }

        // 检查该日期是否已签到
        QueryWrapper<UmsMemberSignInRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", memberId)
                .eq("sign_in_date", signInDate);
        long count = signInRecordMapper.selectCount(queryWrapper);
        if (count > 0) {
            result.put("success", false);
            result.put("message", "该日期已签到");
            return result;
        }

        // 检查补签日期是否在近3天内
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        Date threeDaysAgo = calendar.getTime();

        if (signInDate.before(threeDaysAgo) || signInDate.after(new Date())) {
            result.put("success", false);
            result.put("message", "只能补签近3天未签到的日期");
            return result;
        }

        // 计算总积分（扣除补签消耗）
        int totalIntegration = currentMember.getIntegration() - MAKE_UP_COST;

        // 创建补签记录
        UmsMemberSignInRecord record = new UmsMemberSignInRecord();
        record.setMemberId(memberId);
        record.setSignInDate(signInDate);
        record.setSignInType(1); // 补签
        record.setIntegration(0); // 补签不获得积分
        record.setGrowth(0); // 补签不获得成长值
        record.setTotalIntegration(totalIntegration); // 总积分
        record.setTotalGrowth(lastRecord.getGrowth()); // 总成长值不变
        record.setCreateTime(new Date());

        signInRecordMapper.insert(record);

        // 扣除积分，更新总积分
        UmsMember updateMember = new UmsMember();
        updateMember.setId(memberId);
        updateMember.setIntegration(totalIntegration);

        memberMapper.updateById(updateMember);
        // 清除缓存，保持数据一致性
        memberService.updateIntegration(memberId, totalIntegration);

        result.put("success", true);
        result.put("cost", MAKE_UP_COST);
        result.put("totalIntegration", totalIntegration);
        result.put("message", "补签成功");

        return result;
    }

    @Override
    public List<UmsMemberSignInRecord> getSignInRecords(Integer year, Integer month) {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) return new ArrayList<>();

        return signInRecordMapper.selectByMemberIdAndMonth(currentMember.getId(), year, month);
    }

    @Override
    public Integer getContinuousSignInDays() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) return 0;

        return signInRecordMapper.selectContinuousSignInDays(currentMember.getId());
    }

    @Override
    public boolean isSignedInToday() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) return false;

        QueryWrapper<UmsMemberSignInRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", currentMember.getId())
                .apply("DATE(sign_in_date) = CURDATE()");

        return signInRecordMapper.selectCount(queryWrapper) > 0;
    }

    // ... existing code ...

    @Override
    public Map<String, Object> getMemberPoints(Long memberId) {
        // 查询会员最晚的签到记录，获取总积分和总成长值
        QueryWrapper<UmsMemberSignInRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", memberId)
                .orderByDesc("create_time")  // 按签到日期降序排列
                .last("LIMIT 1");  // 只取第一条（最晚的签到记录）

        UmsMemberSignInRecord latestRecord = signInRecordMapper.selectOne(queryWrapper);

        if (latestRecord == null) {
            // 如果该会员没有签到记录，返回空Map或默认值
            Map<String, Object> points = new HashMap<>();
            points.put("totalIntegration", 0);
            points.put("totalGrowth", 0);
            points.put("integration", 0);
            points.put("growth", 0);
            points.put("message", "该会员暂无签到记录");
            return points;
        }

        Map<String, Object> points = new HashMap<>();
        points.put("totalIntegration", latestRecord.getTotalIntegration());
        points.put("totalGrowth", latestRecord.getTotalGrowth());
        points.put("integration", latestRecord.getIntegration());
        points.put("growth", latestRecord.getGrowth());
        points.put("signInDate", latestRecord.getSignInDate());
        points.put("signInType", latestRecord.getSignInType());

        return points;
    }

    @Override
    public UmsMemberSignInRecord getLatestSignInRecord() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) return null;

        return signInRecordMapper.selectLatestSignInRecord(currentMember.getId());
    }

    @Override
    public List<UmsMemberSignInRecord> getAll(Long id) {
        return signInRecordMapper.selectByMemberIdAndDateRange(id, null, null);
    }


    @Override
    public Integer getTotalSignDays(Long memberId) {
        QueryWrapper<UmsMemberSignInRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", memberId);

        Long count = signInRecordMapper.selectCount(queryWrapper);
        return count != null ? count.intValue() : 0;
    }
}