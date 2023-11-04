package com.example.toby_spring.chapter5.user.service.test;

import com.example.toby_spring.chapter5.user.domain.Level;
import com.example.toby_spring.chapter5.user.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/resources/spring/ch5/test-applicationContext.xml"})
public class UserTest {

    User user;

    @Before
    public void setUp() {
        user = new User();
    }

    @Test
    public void upgradeLevel(){
        Level[] levels = Level.values();
        for (Level level : levels) {
            if(level.nextLevel()==null)continue;
            user.setLevel(level);
            user.upgradeLevel();
            assertThat(user.getLevel(),is(level.nextLevel()));
        }
    }

    @Test(expected = IllegalStateException.class)
    public void cannotUpgradeLevel() {
        Level[] levels = Level.values();
        for (Level level : levels) {
            if(level.nextLevel()!=null)continue;
            user.setLevel(level);
            user.upgradeLevel();
        }
    }
}
