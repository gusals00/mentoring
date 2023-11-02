package com.example.toby_spring.chapter6.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    String id;
    String name;
    String password;

    Level level;
    int login;
    int recommend;
    String eMail;

    public void upgradeLevel(){
        if (level.nextLevel() == null) {
            throw new IllegalStateException(this.level + "은 업그레이드가 불가능합니다.");
        } else {
            level = level.nextLevel();
        }
    }
}


