package ru.eshmakar.sweater.service;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import ru.eshmakar.sweater.domain.Role;
import ru.eshmakar.sweater.domain.User;
import ru.eshmakar.sweater.repos.UserRepo;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean //используется для создания фейкового окружения
    private UserRepo userRepo;

    @MockBean
    private MailSender mailSender;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void addUser() {
        User user = new User();
        user.setEmail("some@mail.ru");
        boolean isUserCreated = userService.addUser(user);//если было добавлено

        Assert.assertTrue(isUserCreated);//проверка на true
        Assert.assertNotNull(user.getActivationCode());//проверка на не null

        //Создает сопоставление, которое соответствует, если исследуемый объект соответствует ВСЕМ из указанных сопоставителей.
        //Например: assertThat ("myValue", allOf (startWith ("my"), containsString ("Val")))
        Assert.assertTrue(CoreMatchers.is(user.getRoles()).matches(Collections.singleton(Role.USER)));//если юзер содержит роль юзера


        Mockito.verify(userRepo, Mockito.times(1))//Позволяет проверить точное количество вызовов userRepo
                .save(user);


        Mockito.verify(mailSender, Mockito.times(1))
                .send(ArgumentMatchers.eq(user.getEmail()),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString());
    }

    @Test
    public void addUserFailTest(){ //проверка на не существующего пользователя
        User user = new User();
        user.setUsername("John");

        Mockito.doReturn(new User()) //возвращать нового юзера
                .when(userRepo) //когда userRepo нашел
                .findByUsername("John"); //юзера по имени Джон

        boolean isUserCreated = userService.addUser(user);//если было добавлено
        Assert.assertFalse(isUserCreated);


        Mockito.verify(userRepo, Mockito.times(0)).save(ArgumentMatchers.any(User.class));//если не был вызван userRepo

        Mockito.verify(mailSender, Mockito.times(0))
                .send(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );

    }

    @Test
    public void activateUser() {//проверка на активационного кода
        User user = new User();
        user.setActivationCode("bingo");//пусть будет код такой

        Mockito.doReturn(user)
                .when(userRepo)
                .findByActivationCode("activate");

        boolean isUserActivated = userService.activateUser("activate");
        Assert.assertTrue(isUserActivated);//если вернул true
        Assert.assertNull(user.getActivationCode());//если вернул null

        Mockito.verify(userRepo, Mockito.times(1)).save(user);
    }

    @Test
    public void activateUserFailTest(){
        boolean isUserActivated = userService.activateUser("activate me");
        Assert.assertFalse(isUserActivated);
        Mockito.verify(userRepo, Mockito.times(0)).save(ArgumentMatchers.any(User.class));
    }
}










