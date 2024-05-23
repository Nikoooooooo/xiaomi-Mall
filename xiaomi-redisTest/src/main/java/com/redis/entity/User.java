package com.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
//    private String name;
//    private Integer age;
    private Integer id;
    private String username;
    private String tel;
    private String pwd;
    private String regDate;
    private String email;
}
