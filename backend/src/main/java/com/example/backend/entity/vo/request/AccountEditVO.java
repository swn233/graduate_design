package com.example.backend.entity.vo.request;

import lombok.Data;

@Data
public class AccountEditVO {
    private Integer id;
    private String username;
    private String email;
    private String password; // 用户可能只想修改密码，或者其他信息时也可能需要提供旧密码或新密码
}