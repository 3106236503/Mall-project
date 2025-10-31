package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.model.UmsMemberSignInRecord;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.portal.service.UmsMemberSignInService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Api(tags = "UmsMemberSignInController", description = "会员签到管理")
@RequestMapping("/sso/signIn")
@CrossOrigin(origins = "*", maxAge = 3600)  // 添加跨域注解
public class UmsMemberSignInController {

    @Autowired
    private UmsMemberSignInService signInService;

    @Autowired
    private UmsMemberService memberService;


    @ApiOperation("会员签到")
    @RequestMapping(value = "/signIn", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult signIn() {
        Map<String, Object> result = signInService.signIn();

        if (Boolean.TRUE.equals(result.get("success"))) {
            return CommonResult.success(result);
        } else {
            return CommonResult.failed((String) result.get("message"));
        }
    }

    @ApiOperation("会员补签")
    @RequestMapping(value = "/makeUp", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult makeUpSignIn(@RequestBody Map<String, String> requestBody) {

        // 从请求体中获取signInDate参数
        String signInDate = requestBody.get("signInDate");
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(signInDate);

            Map<String, Object> result = signInService.makeUpSignIn(date);
            if (Boolean.TRUE.equals(result.get("success"))) {
                return CommonResult.success(result);
            } else {
                return CommonResult.failed((String) result.get("message"));
            }
        } catch (ParseException e) {
            return CommonResult.failed("日期格式错误，请使用yyyy-MM-dd格式");
        }
    }

    @ApiOperation("查询签到记录")
    @RequestMapping(value = "/records", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getSignInRecords(@RequestParam Integer year,
                                         @RequestParam Integer month) {
        List<UmsMemberSignInRecord> records = signInService.getSignInRecords(year, month);
        return CommonResult.success(records);
    }

    @ApiOperation("查询连续签到天数")
    @RequestMapping(value = "/continuousDays", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getContinuousSignInDays() {
        Integer days = signInService.getContinuousSignInDays();
        return CommonResult.success(days);
    }

    @ApiOperation("查询今日是否已签到")
    @RequestMapping(value = "/todayStatus", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult isSignedInToday() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.unauthorized("请先登录");
        }

        boolean signedIn = signInService.isSignedInToday();
        Map<String, Object> result = new HashMap<>();
        if (signedIn) {
            result.put("success", false);
            result.put("message", "今日已签到");
        } else {
            result.put("success", true);
            result.put("message", "今日未签到");
        }
        return CommonResult.success(result);
    }


    //查询会员积分和成长值
    @ApiOperation("查询会员积分和成长值")
    @RequestMapping(value = "/points", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getMemberPoints() {

        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.unauthorized("请先登录");
        }
       /* // 查询会员积分和成长值
        Map<String, Object> points = signInService.getMemberPoints(currentMember.getId());
        if (points == null) {
            return CommonResult.failed("查询会员积分和成长值失败");
        }*/

        Map<String, Object> points = new HashMap<>();
        points.put("integration", currentMember.getIntegration());
        points.put("growth", currentMember.getGrowth());
        return CommonResult.success(points);
    }

    //查询本会员的签到记录
    @ApiOperation("查询本会员的签到记录")
    @RequestMapping(value = "/reecords", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getMemberSignInRecords() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.unauthorized("请先登录");
        }
        List<UmsMemberSignInRecord> records = signInService.getAll(currentMember.getId());
        return CommonResult.success(records);
    }


    @ApiOperation("查询累计签到总天数")
    @RequestMapping(value = "/totalDays", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getTotalSignDays() {
        UmsMember currentMember = memberService.getCurrentMember();
        if (currentMember == null) {
            return CommonResult.unauthorized("请先登录");
        }

        Integer totalDays = signInService.getTotalSignDays(currentMember.getId());
        return CommonResult.success(totalDays);
    }

}
