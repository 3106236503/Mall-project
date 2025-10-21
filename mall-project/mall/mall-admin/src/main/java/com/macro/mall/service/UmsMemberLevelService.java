package com.macro.mall.service;

import com.macro.mall.model.UmsMemberLevel;

import java.util.List;

/**
 * 会员等级管理Service
 * Created by macro on 2018/4/26.
 */
public interface UmsMemberLevelService {
    /**
     * 获取所有会员等级
     *
     * @param defaultStatus 是否为默认会员
     */
    List<UmsMemberLevel> list(Integer defaultStatus);

    /**
     * 添加会员等级
     */
    int insertLevel(UmsMemberLevel umsMemberLevel);


    /**
     * 修改会员等级
     */
    int updateLevel(UmsMemberLevel umsMemberLevel);


    /**
     * 删除会员等级
     */
    int delete(Long id);


    /**
     * 修改会员等级状态
     */
    int updateStatus(Long id, Integer status);
}
