package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.entity.dto.Account;
import com.example.backend.entity.vo.request.ConfirmResetVO;
import com.example.backend.entity.vo.request.EmailRegisterVO;
import com.example.backend.entity.vo.response.EmailResetVO;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);

    String RegisterEmailVerifyCode(String type,String email,String ip);

    //根据ip地址进行过滤
    boolean verifyLimit(String ip);

    String registerEmailAccount(EmailRegisterVO vo);


    String resetConfirm(ConfirmResetVO vo);

    String resetEmailAccountPassword(EmailResetVO vo);

    List<Account> getAllAccounts();

    void deleteAccountById(int id);

    String editAccount(com.example.backend.entity.vo.request.AccountEditVO vo);
    
}
