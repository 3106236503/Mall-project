/*
package com.macro.mall.dto;


import java.time.LocalDate;
import java.time.LocalDateTime;

*/
/**
 * Created with IntelliJ IDEA
 * User: hwj
 * Date: 2025/10/10
 * Time: 22:30
 * Description:会员登录参数
 *//*

@lombok.Data
public class UmsMember {

    */
/**
 * 会员ID（主键，自增）
 *//*

    private Long id;

    */
/**
 * 会员等级ID（关联ums_member_level表的id）
 *//*

    private Long memberLevelId;

    */
/**
 * 用户名（唯一）
 *//*

    private String username;

    */
/**
 * 密码（加密存储）
 *//*

    private String password;

    */
/**
 * 昵称
 *//*

    private String nickname;

    */
/**
 * 手机号码（唯一）
 *//*

    private String phone;

    */
/**
 * 帐号启用状态：0->禁用；1->启用
 *//*

    private Integer status;

    */
/**
 * 注册时间
 *//*

    private LocalDateTime createTime;

    */
/**
 * 头像（URL地址）
 *//*

    private String icon;

    */
/**
 * 性别：0->未知；1->男；2->女
 *//*

    private Integer gender;

    */
/**
 * 生日
 *//*

    private LocalDate birthday;

    */
/**
 * 所在城市
 *//*

    private String city;

    */
/**
 * 职业
 *//*

    private String job;

    */
/**
 * 个性签名
 *//*

    private String personalizedSignature;

    */
/**
 * 用户来源（未明确枚举，需根据业务补充）
 *//*

    private Integer sourceType;

    */
/**
 * 当前积分
 *//*

    private Integer integration;

    */
/**
 * 当前成长值
 *//*

    private Integer growth;

    */
/**
 * 剩余抽奖次数
 *//*

    private Integer luckeyCount;

    */
/**
 * 历史积分数量
 *//*

    private Integer historyIntegration;


}*/
