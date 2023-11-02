package com.example.toby_spring.chapter6.user.service;

import com.example.toby_spring.chapter6.user.domain.User;

public interface UserLevelUpgradePolicy {
    boolean canUpgradeLevel(User user);
    void upgradeLevel(User user);
}
