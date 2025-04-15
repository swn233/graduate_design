package com.example.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.backend.entity.dto.Account;
import com.example.backend.entity.vo.ConfirmResetVO;
import com.example.backend.entity.vo.EmailRegisterVO;
import com.example.backend.entity.vo.EmailResetVO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);

    String RegisterEmailVerifyCode(String type,String email,String ip);

    //根据ip地址进行过滤
    boolean verifyLimit(String ip);

    String registerEmailAccount(EmailRegisterVO vo);

    String resetConfirm(ConfirmResetVO vo);

    String resetEmailAccountPassword(EmailResetVO vo);
}
