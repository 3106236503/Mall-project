package com.macro.mall.service.impl;

import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Created with IntelliJ IDEA
 * User: hwj
 * Date: 2025/10/11
 * Time: 11:19
 * Description:
 */

@Service
public class UmsMemberServiceImpl implements UmsMemberService {

    @Autowired
    private UmsMemberMapper memberMapper;

    @Override
    public List<UmsMember> listAll(UmsMember umsMember) {

       /* // 参数校验和默认值设置
        int currentPage = (pageNum == null || pageNum <= 0) ? 1 : pageNum;
        int pageSizeValue = (pageSize == null || pageSize <= 0) ? 10 : pageSize;*/

       /* // 启动分页
        PageHelper.startPage(pageNum, pageSize);*/
        return memberMapper.selectListMember(umsMember);
    }

    @Override
    public List<UmsMemberLevel> listAllLevel() {
        return memberMapper.selectAllLevel();
    }

    @Override
    public UmsMember getById(Long id) {
        return memberMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insert(UmsMember umsMember) {
        return memberMapper.insertSelective(umsMember);
    }

    @Override
    public int update(UmsMember umsMember) {
        return memberMapper.updateByPrimaryKeySelective(umsMember);
    }

    @Override
    public int delete(Long id) {
        return memberMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int insertLevel(UmsMemberLevel umsMemberLevel) {
        return memberMapper.insertLevel(umsMemberLevel);
    }
}
