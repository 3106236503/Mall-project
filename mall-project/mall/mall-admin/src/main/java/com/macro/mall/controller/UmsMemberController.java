package com.macro.mall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.mapper.UmsIntegrationChangeHistoryMapper;
import com.macro.mall.model.UmsIntegrationChangeHistory;
import com.macro.mall.model.UmsIntegrationChangeHistoryExample;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @Autowired
    private UmsIntegrationChangeHistoryMapper integrationChangeHistoryMapper;


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

    //充值会员积分
    @ApiOperation("充值会员积分")
    @RequestMapping(value = "/recharge", method = RequestMethod.POST)
    public CommonResult<String> recharge(@RequestParam Long id, @RequestParam Integer integration, @RequestParam(required = false) String operateNote) {
        // 检查会员是否存在
        UmsMember existingMember = memberService.getById(id);
        if (existingMember == null) {
            return CommonResult.failed("会员不存在");
        }

        // 充值会员积分
        existingMember.setIntegration(existingMember.getIntegration() + integration);
        int count = memberService.update(existingMember);
        if (count > 0) {

            //记录充值记录
            UmsIntegrationChangeHistory umsMemberRecharge = new UmsIntegrationChangeHistory();
            umsMemberRecharge.setMemberId(id);
            umsMemberRecharge.setChangeCount(integration);
            umsMemberRecharge.setCreateTime(new Date());
            umsMemberRecharge.setChangeType(0);
            umsMemberRecharge.setSourceType(2);
            umsMemberRecharge.setOperateMan("系统管理员");
            umsMemberRecharge.setOperateNote(operateNote);
            integrationChangeHistoryMapper.insert(umsMemberRecharge);


            return CommonResult.success("充值成功");
        } else {
            return CommonResult.failed("充值失败");
        }
    }

    /**
     * 查询会员积分改变记录（支持分页）
     *
     * @param memberId 会员ID（必传）
     * @param pageNum  页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @return 分页结果（总条数+当前页数据）
     */
    @ApiOperation("查询会员积分改变记录（分页）")
    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public CommonResult<PageInfo<UmsIntegrationChangeHistory>> getIntegrationChangeHistory(
            @ApiParam(value = "会员ID", required = true) @RequestParam Long memberId,
            @ApiParam(value = "页码（默认1）") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam(value = "每页条数（默认10）") @RequestParam(defaultValue = "10") Integer pageSize) {

        // 1. 校验会员是否存在
        UmsMember existingMember = memberService.getById(memberId);
        if (existingMember == null) {
            return CommonResult.failed("会员不存在");
        }

        // 2. 开启分页（PageHelper 自动拦截查询，实现分页）
        PageHelper.startPage(pageNum, pageSize);

        // 3. 执行查询（无需手动处理分页逻辑，PageHelper 自动拼接 limit）
        UmsIntegrationChangeHistoryExample example = new UmsIntegrationChangeHistoryExample();
        // 按创建时间倒序（最新记录在前，符合用户查看习惯）
        example.setOrderByClause("create_time DESC");
        example.createCriteria().andMemberIdEqualTo(memberId);
        List<UmsIntegrationChangeHistory> historyList = integrationChangeHistoryMapper.selectByExample(example);

        // 4. 封装分页结果（PageInfo 包含总条数、总页数、当前页数据等）
        PageInfo<UmsIntegrationChangeHistory> pageInfo = new PageInfo<>(historyList);

        // 5. 返回结果（CommonResult 包装分页对象）
        return CommonResult.success(pageInfo);
    }

}