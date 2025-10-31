package com.macro.mall.portal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

@TableName("ums_member_sign_in_record")
public class UmsMemberSignInRecord implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "签到日期")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date signInDate;

    @ApiModelProperty(value = "签到类型：0-正常签到，1-补签")
    private Integer signInType;

    @ApiModelProperty(value = "获得积分")
    private Integer integration;

    @ApiModelProperty(value = "获得成长值")
    private Integer growth;

    @ApiModelProperty(value = "总积分（签到后累计）")
    private Integer totalIntegration;

    @ApiModelProperty(value = "总成长值（签到后累计）")
    private Integer totalGrowth;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    // getter和setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Date getSignInDate() {
        return signInDate;
    }

    public void setSignInDate(Date signInDate) {
        this.signInDate = signInDate;
    }

    public Integer getSignInType() {
        return signInType;
    }

    public void setSignInType(Integer signInType) {
        this.signInType = signInType;
    }

    public Integer getIntegration() {
        return integration;
    }

    public void setIntegration(Integer integration) {
        this.integration = integration;
    }

    public Integer getGrowth() {
        return growth;
    }

    public void setGrowth(Integer growth) {
        this.growth = growth;
    }

    public Integer getTotalIntegration() {
        return totalIntegration;
    }

    public void setTotalIntegration(Integer totalIntegration) {
        this.totalIntegration = totalIntegration;
    }

    public Integer getTotalGrowth() {
        return totalGrowth;
    }

    public void setTotalGrowth(Integer totalGrowth) {
        this.totalGrowth = totalGrowth;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}