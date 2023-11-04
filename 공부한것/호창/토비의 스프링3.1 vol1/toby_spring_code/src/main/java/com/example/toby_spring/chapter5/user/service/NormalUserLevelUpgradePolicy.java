package com.example.toby_spring.chapter5.user.service;

import com.example.toby_spring.chapter5.user.dao.UserDao;
import com.example.toby_spring.chapter5.user.domain.Level;
import com.example.toby_spring.chapter5.user.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NormalUserLevelUpgradePolicy implements UserLevelUpgradePolicy{

    public static final int MIN_LOGOUT_FOR_SILVER = 50;
    public static final int MIN_RECCOMEND_FOR_GOLD = 30;

    private UserDao userDao;

    @Override
    public boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC:
                return (user.getLogin() >= MIN_LOGOUT_FOR_SILVER);
            case SILVER:
                return (user.getRecommend() >= MIN_RECCOMEND_FOR_GOLD);
            case GOLD:
                return false;
            default:
                throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }

    @Override
    public void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }
}
