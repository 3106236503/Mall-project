package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.service.UmsMemberLevelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员等级管理Controller
 * Created by macro on 2018/4/26.
 */
@Controller
@Api(tags = "UmsMemberLevelController")
@Tag(name = "UmsMemberLevelController", description = "会员等级管理")
@RequestMapping("/memberLevel")
public class UmsMemberLevelController {
    @Autowired
    private UmsMemberLevelService memberLevelService;

    @ApiOperation("查询所有会员等级")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsMemberLevel>> list(@RequestParam(value = "defaultStatus", required = false) Integer defaultStatus) {
        List<UmsMemberLevel> memberLevelList = memberLevelService.list(defaultStatus);
        return CommonResult.success(memberLevelList);
    }

    @ApiOperation("查询所有可用会员等级")
    @RequestMapping(value = "/listAvailable", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<UmsMemberLevel>> listAvailable() {
        List<UmsMemberLevel> memberLevelList = memberLevelService.list(1);
        return CommonResult.success(memberLevelList);
    }


    @ApiOperation("添加会员等级")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<UmsMemberLevel> create(@RequestBody UmsMemberLevel umsMemberLevel) {
        int count = memberLevelService.insertLevel(umsMemberLevel);
        if (count > 0) {
            return CommonResult.success(umsMemberLevel);
        } else {
            return CommonResult.failed("新增失败");
        }
    }

    @ApiOperation("修改会员等级信息")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<UmsMemberLevel> update(@RequestBody UmsMemberLevel umsMemberLevel) {

        int count = memberLevelService.updateLevel(umsMemberLevel);
        if (count > 0) {
            return CommonResult.success(umsMemberLevel);
        } else {
            return CommonResult.failed("修改失败");
        }
    }

    @ApiOperation("删除会员等级信息")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> delete(@PathVariable("id") Long id) {
        int count = memberLevelService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("删除失败");
        }
    }


    @ApiOperation("修改会员等级状态")
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Integer> updateStatus(@PathVariable("id") Long id,
                                              @RequestParam(value = "defaultStatus") Integer defaultStatus) {
        int count = memberLevelService.updateStatus(id, defaultStatus);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed("修改状态失败");
        }
    }
}
