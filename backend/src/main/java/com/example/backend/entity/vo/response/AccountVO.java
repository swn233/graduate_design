package com.example.backend.entity.vo.response;

import java.util.Date;

import lombok.Data;

@Data
public class AccountVO {
    Integer id;
    String username;
    String email;
    String role;
    Date register_time;
}
