package com.webapi.seed.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.webapi.seed.dao.AccountDao;
import com.webapi.seed.entity.Account;
import com.webapi.seed.service.IAccountService;

import org.springframework.stereotype.Service;

/**
 * @author xiaozhong
 * @since 2017-12-30
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountDao, Account> implements IAccountService {

    @Override
    public Iterable<Account> selectAll() {
        return baseMapper.selectList(new EntityWrapper<Account>());
    }

    @Override
    public Account selectByUsername(String username) {
        Account entity = new Account();
        entity.username = username;
        return baseMapper.selectOne(new EntityWrapper<Account>(entity).getEntity());
    }

}
