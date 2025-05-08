package com.example.backend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.entity.RestBean;
import com.example.backend.entity.dto.Account;
import com.example.backend.entity.vo.request.AccountEditVO;
import com.example.backend.entity.vo.response.AccountVO;
import com.example.backend.service.AccountService;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Resource
    AccountService accountService;
    @GetMapping("/all")
    public RestBean<List<AccountVO>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        List<AccountVO> accountVOs = new ArrayList<>();
        for (Account account : accounts) {
            AccountVO vo = new AccountVO();
            vo=account.asViewObject(vo.getClass());
            accountVOs.add(vo);
        }
        return RestBean.success(accountVOs);
    }

    @PostMapping("/delete/{id}")
    public RestBean<Void> deleteAccount(@PathVariable("id") int id) {
        accountService.deleteAccountById(id);
        return RestBean.success();
    }

    @PostMapping("/edit")
    public RestBean<Void> editAccount(@RequestBody AccountEditVO vo) {
        String result = accountService.editAccount(vo);
        if (result == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(400, result);
        }
    }
}
