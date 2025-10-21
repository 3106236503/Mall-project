/*
package com.macro.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.macro.mall.dto.UmsMember;
import org.apache.ibatis.annotations.Param;

import java.util.List;

*/
/**
 * Created with IntelliJ IDEA
 * User: hwj
 * Date: 2025/10/11
 * Time: 11:14
 * Description:
 *//*

public interface UmsMemberMapper extends BaseMapper<UmsMember> {

    */
/**
 * 根据用户名查询会员（用户名唯一）
 *
 * @param username 用户名
 * @return 会员信息
 *//*

    UmsMember selectByUsername(@Param("username") String username);

    */
/**
 * 根据手机号查询会员（手机号唯一）
 *
 * @param phone 手机号
 * @return 会员信息
 *//*

    UmsMember selectByPhone(@Param("phone") String phone);

    */
/**
 * 根据会员等级ID查询会员列表
 *
 * @param memberLevelId 会员等级ID
 * @return 会员列表
 *//*

    List<UmsMember> selectByLevelId(@Param("memberLevelId") Long memberLevelId);

}*/
