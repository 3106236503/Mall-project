package com.macro.mall.service;

import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberLevel;
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
public interface UmsMemberService {

    List<UmsMember> listAll(UmsMember umsMember);

    List<UmsMemberLevel> listAllLevel();

    UmsMember getById(Long id);

    int insert(UmsMember umsMember);

    int update(UmsMember umsMember);

    int delete(Long ids);

    int insertLevel(UmsMemberLevel umsMemberLevel);
}
