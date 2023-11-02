package com.example.toby_spring.chapter6.user.service.test;

import com.example.toby_spring.chapter6.user.dao.UserDao;
import com.example.toby_spring.chapter6.user.domain.Level;
import com.example.toby_spring.chapter6.user.domain.User;
import com.example.toby_spring.chapter6.user.proxy.TransactionHandler;
import com.example.toby_spring.chapter6.user.proxy.TxProxyFactoryBean;
import com.example.toby_spring.chapter6.user.service.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import static com.example.toby_spring.chapter5.user.service.NormalUserLevelUpgradePolicy.MIN_LOGOUT_FOR_SILVER;
import static com.example.toby_spring.chapter5.user.service.NormalUserLevelUpgradePolicy.MIN_RECCOMEND_FOR_GOLD;
import static com.example.toby_spring.chapter6.user.domain.Level.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"file:src/main/resources/spring/ch6/test-applicationContext.xml"})
public class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserServiceImpl userServiceImpl;
    @Autowired
    UserDao userDao;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    UserLevelUpgradePolicy userLevelUpgradePolicy;
    @Autowired
    MailSender mailSender;
    @Autowired
    ApplicationContext context;

    List<User> users;

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("bumgin", "박범진", "p1", BASIC, MIN_LOGOUT_FOR_SILVER - 1, 0,"bumgin@naver.com"),
                new User("joytouch", "강명성", "p2", BASIC, MIN_LOGOUT_FOR_SILVER, 0,"joytouch@naver.com"),
                new User("erwins", "신승환", "p3", SILVER, 60, MIN_RECCOMEND_FOR_GOLD - 1,"erwins@naver.com"),
                new User("madnite1", "이상호", "p4", SILVER, 60, MIN_RECCOMEND_FOR_GOLD,"madnite1@naver.com"),
                new User("green", "오민규", "p5", GOLD, 100, Integer.MAX_VALUE,"green@naver.com")
        );
    }

    @Test
    public void bean() {
        assertThat(this.userService, is(notNullValue()));
    }

    @Test
    @DirtiesContext
    public void upgradeLevels()throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(this.users);
        NormalUserLevelUpgradePolicy policy = new NormalUserLevelUpgradePolicy();
        policy.setUserDao(mockUserDao);

        userServiceImpl.setUserDao(mockUserDao);
        userServiceImpl.setUserLevelUpgradePolicy(policy);
        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertThat(updated.size(),is(2));
        checkUserAndLevel(updated.get(0),"joytouch", SILVER);
        checkUserAndLevel(updated.get(1),"madnite1", GOLD);

        List<String> request = mockMailSender.getRequests();
        assertThat(request.size(),is(2));
        assertThat(request.get(0),is(users.get(1).getEMail()));
        assertThat(request.get(1),is(users.get(3).getEMail()));
    }

    @Test
    public void mockUpgradeLevels()throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);
        NormalUserLevelUpgradePolicy policy = new NormalUserLevelUpgradePolicy();
        policy.setUserDao(mockUserDao);
        userServiceImpl.setUserLevelUpgradePolicy(policy);

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        verify(mockUserDao,times(2)).update(any(User.class));
        verify(mockUserDao,times(2)).update(any(User.class));
        verify(mockUserDao).update(users.get(1));
        assertThat(users.get(1).getLevel(),is(SILVER));
        verify(mockUserDao).update(users.get(3));
        assertThat(users.get(3).getLevel(),is(GOLD));

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender,times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertThat(mailMessages.get(0).getTo()[0],is(users.get(1).getEMail()));
        assertThat(mailMessages.get(1).getTo()[0],is(users.get(3).getEMail()));
    }

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertThat(updated.getId(),is(expectedId));
        assertThat(updated.getLevel(),is(expectedLevel));
    }

    private void checkLevelUpgraded(User user, Boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if (upgraded) assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
        else assertThat(userUpdate.getLevel(), is(user.getLevel()));
    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
        assertThat(userWithoutLevelRead.getLevel(), is(BASIC));
    }

    @Test
    @DirtiesContext
    public void upgradeAllOrNothing() throws Exception{
        TestUserService testUserService = new TestUserService(users.get(3).getId());
        testUserService.setUserDao(userDao);
        testUserService.setMailSender(mailSender);
        testUserService.setUserLevelUpgradePolicy(userLevelUpgradePolicy);

        TxProxyFactoryBean txProxyFactoryBean = context.getBean("&userService", TxProxyFactoryBean.class);
        txProxyFactoryBean.setTarget(testUserService);
        UserService txUserService = (UserService) txProxyFactoryBean.getObject();

        userDao.deleteAll();
        for (User user : users) userDao.add(user);

        try {
            txUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {
        }

        checkLevelUpgraded(users.get(1),false);
    }

}
