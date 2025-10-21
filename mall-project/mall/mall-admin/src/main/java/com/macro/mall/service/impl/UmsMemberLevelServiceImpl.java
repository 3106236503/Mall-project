package com.macro.mall.service.impl;

import com.macro.mall.mapper.UmsMemberLevelMapper;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.model.UmsMemberLevelExample;
import com.macro.mall.service.UmsMemberLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员等级管理Service实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class UmsMemberLevelServiceImpl implements UmsMemberLevelService {
    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;

    @Override
    public List<UmsMemberLevel> list(Integer defaultStatus) {
        UmsMemberLevelExample example = new UmsMemberLevelExample();
        // 只有当defaultStatus不为null时才添加该查询条件
        if (defaultStatus != null) {
            example.createCriteria().andDefaultStatusEqualTo(defaultStatus);
        }
        return memberLevelMapper.selectByExample(example);
    }


    @Override
    public int insertLevel(UmsMemberLevel umsMemberLevel) {
        return memberLevelMapper.insertSelective(umsMemberLevel);
    }

    //修改会员
    @Override
    public int updateLevel(UmsMemberLevel umsMemberLevel) {
        return memberLevelMapper.updateByPrimaryKeySelective(umsMemberLevel);
    }


    //删除会员等级
    @Override
    public int delete(Long id) {
        return memberLevelMapper.deleteByPrimaryKey(id);
    }


    @Override
    public int updateStatus(Long id, Integer defaultStatus) {
        // 先查询现有的会员等级对象
        UmsMemberLevel memberLevel = memberLevelMapper.selectByPrimaryKey(id);
        if (memberLevel == null) {
            throw new RuntimeException("会员等级不存在");
        }

        // 修改状态字段
        memberLevel.setDefaultStatus(defaultStatus);
        // 更新数据库
        int result = memberLevelMapper.updateByPrimaryKeySelective(memberLevel);
        if (result > 0) {
            return result;
        } else {
            throw new RuntimeException("更新状态失败");
        }
    }
}