package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA
 * User: hwj
 * Date: 2025/10/11
 * Time: 11:23
 * Description:
 */
@RestController
@Api(tags = "UmsMemberController", description = "会员管理")
@RequestMapping("/member")
public class UmsMemberController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UmsMemberService memberService;

    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    public CommonResult<Map<String, Object>> listAll(UmsMember umsMember, Integer pageNum, Integer pageSize) {
        List<UmsMember> umsMemberList = memberService.listAll(umsMember);

        // 创建返回结果，包含分页信息
        Map<String, Object> result = new HashMap<>();
        result.put("list", umsMemberList);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("total", umsMemberList.size());

        return CommonResult.success(result);
    }

    @ApiOperation("查询会员等级列表")
    @RequestMapping(value = "/level/listAll", method = RequestMethod.GET)
    public CommonResult<List<UmsMemberLevel>> listAllLevel() {
        List<UmsMemberLevel> umsMemberLevelList = memberService.listAllLevel();
        return CommonResult.success(umsMemberLevelList);
    }

    @ApiOperation("会员详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public CommonResult<UmsMember> detail(Long id) {
        UmsMember umsMember = memberService.getById(id);
        if (umsMember == null) {
            return CommonResult.failed("会员不存在");
        }
        return CommonResult.success(umsMember);
    }


    @ApiOperation("新增会员")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public CommonResult<UmsMember> create(@RequestBody UmsMember umsMember) {
        //密码加密
        umsMember.setPassword(passwordEncoder.encode(umsMember.getPassword()));

        //获取当前时间
        umsMember.setCreateTime(new Date());

        int count = memberService.insert(umsMember);

        if (count > 0) {
            return CommonResult.success(umsMember);
        } else {
            return CommonResult.failed("新增失败");
        }
    }

    @ApiOperation("修改会员")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public CommonResult<UmsMember> update(@RequestBody UmsMember umsMember) {


        // 验证会员ID是否存在
        if (umsMember.getId() == null) {
            return CommonResult.failed("会员ID不能为空");
        }

        // 检查会员是否存在
        UmsMember existingMember = memberService.getById(umsMember.getId());
        if (existingMember == null) {
            return CommonResult.failed("会员不存在");
        }

        int count = memberService.update(umsMember);
        if (count > 0) {
            return CommonResult.success(umsMember);
        } else {
            return CommonResult.failed("修改失败");
        }
    }

    @ApiOperation("删除会员")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public CommonResult<String> delete(@RequestParam Long id) {
        // 检查会员是否存在
        UmsMember existingMember = memberService.getById(id);
        if (existingMember == null) {
            return CommonResult.failed("会员不存在");
        }

        int count = memberService.delete(id);
        if (count > 0) {
            return CommonResult.success(null);
        } else {
            return CommonResult.failed("删除失败");
        }
    }

    @ApiOperation("新增会员等级")
    @RequestMapping(value = "/level/create", method = RequestMethod.POST)
    public CommonResult<UmsMemberLevel> createLevel(@RequestBody UmsMemberLevel umsMemberLevel) {
        int count = memberService.insertLevel(umsMemberLevel);
        if (count > 0) {
            return CommonResult.success(umsMemberLevel);
        } else {
            return CommonResult.failed("新增失败");
        }
    }

}