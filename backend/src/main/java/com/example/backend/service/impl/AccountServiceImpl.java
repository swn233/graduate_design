package com.example.backend.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.backend.entity.dto.Account;
import com.example.backend.entity.vo.request.ConfirmResetVO;
import com.example.backend.entity.vo.request.EmailRegisterVO;
import com.example.backend.entity.vo.request.AccountEditVO;
import com.example.backend.entity.vo.response.EmailResetVO;
import com.example.backend.mapper.AccountMapper;
import com.example.backend.service.AccountService;
import com.example.backend.utils.Const;
import com.example.backend.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    @Resource
    AmqpTemplate amqpTemplate;
    @Resource
    StringRedisTemplate template;
    @Resource
    FlowUtils utils;
    @Resource
    BCryptPasswordEncoder encoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account=this.findAccountByNameOrEmail(username);
        if (account==null){
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User.withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    public Account findAccountByNameOrEmail(String text){
        return this.query()
                .eq("username",text).or()
                .eq("email",text)
                .one();
    }

    //生成一个六位数的验证码用于注册
    @Override
    public String RegisterEmailVerifyCode(String type, String email, String ip) {
        synchronized (ip.intern()) {
            if (this.verifyLimit(ip)) {
                Random random = new Random();
                int code = random.nextInt(899999) + 100000;
                Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
                amqpTemplate.convertAndSend("test_mail", data);
                System.out.println("发送邮箱为：" + data);
                template.opsForValue()
                        .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
                return null;
            } else {
                return "您的请求过于频繁，请稍后再试";
            }
        }
    }

    @Override
    public boolean verifyLimit(String ip) {
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return utils.limitOnceCheck(key, 60);
    }

    @Override
    public String registerEmailAccount(EmailRegisterVO vo) {
        String email=vo.getEmail();
        String username=vo.getUsername();
        String code=template.opsForValue().get(Const.VERIFY_EMAIL_DATA+email);
        if (code==null){
            return "请先获取验证码";
        }
        if (!code.equals(vo.getCode())){
            return "验证码错误，请重试";
        }
        if (this.existedAccountByEmail(email)){
            return "此邮箱已被其他用户注册，请更换一个新的邮箱";
        }
        if (this.existedAccountByUsername(username)){
            return "此用户名已被其他用户注册，请更换一个新的用户名";
        }
        String password=encoder.encode(vo.getPassword());
        Account account=new Account(null,username,password,email,"user",new Date());
        if (this.save(account)){
            template.delete(Const.VERIFY_EMAIL_DATA+email);
            return null;
        }
        else {
            return "内部错误，请联系管理员";
        }
    }

    private boolean existedAccountByUsername(String username) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("username",username));
    }

    private boolean existedAccountByEmail(String email) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email",email));
    }


    @Override
    public String resetConfirm(ConfirmResetVO vo) {
        String email = vo.getEmail();
        String code = template.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
        if (code == null) return "请先获取验证码";
        if (!code.equals(vo.getCode())) return "验证码错误，请重新输入";
        return null;
    }

    @Override
    public String resetEmailAccountPassword(EmailResetVO vo){
        String email=vo.getEmail();
        String verify=this.resetConfirm(new ConfirmResetVO(vo.getEmail(),vo.getCode()));
        if (verify!=null){
            return verify;
        }
        String password=encoder.encode(vo.getPassword());
        boolean update=this.update().eq("email",email).set("password",password).update();
        if (update){
            template.delete(Const.VERIFY_EMAIL_DATA+email);
        }
        return null;
    }

    @Override
    public List<Account> getAllAccounts() {
        return this.list();
    }

    @Override
    public void deleteAccountById(int id) {
        this.removeById(id);
    }

    @Override
    public String editAccount(AccountEditVO vo) {
        Account account = this.getById(vo.getId());
        if (account == null) {
            return "账户不存在";
        }
        // 检查是否有任何信息被修改
        boolean updated = false;
        if (vo.getUsername() != null && !vo.getUsername().isEmpty() && !vo.getUsername().equals(account.getUsername())) {
            // 检查新用户名是否已存在
            if (this.existedAccountByUsername(vo.getUsername())) {
                return "此用户名已被其他用户注册，请更换一个新的用户名";
            }
            account.setUsername(vo.getUsername());
            updated = true;
        }
        if (vo.getEmail() != null && !vo.getEmail().isEmpty() && !vo.getEmail().equals(account.getEmail())) {
            // 检查新邮箱是否已存在
            if (this.existedAccountByEmail(vo.getEmail())) {
                return "此邮箱已被其他用户注册，请更换一个新的邮箱";
            }
            account.setEmail(vo.getEmail());
            updated = true;
        }
        if (vo.getPassword() != null && !vo.getPassword().isEmpty()) {
            account.setPassword(encoder.encode(vo.getPassword()));
            updated = true;
        }

        if (updated) {
            if (this.updateById(account)) {
                return null; // 成功
            } else {
                return "更新账户信息失败，请联系管理员";
            }
        } else {
            return "未提供任何需要修改的信息或信息与原信息相同";
        }
    }
}

