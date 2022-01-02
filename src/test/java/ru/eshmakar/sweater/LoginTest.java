package ru.eshmakar.sweater;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.eshmakar.sweater.controller.MessageController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc//создает фейковое окружение для тестирования
@TestPropertySource("/application-test.properties") //для тестов будем использовать этот конфиг
public class LoginTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MessageController controller;

    @Test
    public void test() throws Exception{
        assertThat(controller).isNotNull(); //грубо говоря, проверяем наличие контекста, то есть бин controller
    }

    @Test
    public void test0() throws Exception{
        assertThat("text").contains("ex");
    }

    @Test
    public void contextLoads() throws Exception{
        this.mockMvc.perform(get("/")) //сообщаем, что мы хотим выполнить get запрос на главную страницу сайта
                .andDo(print()) //если тест провалится, то можем узнать это в консоли
                .andExpect(status().isOk()) //ожидаем, что код ответа от сервера будет 200
                .andExpect(content().string(containsString("Hello, guest")))//проверяем, что вернется какой-то контент,и как строку проверяем на содержание текста
                .andExpect(content().string(containsString("Please, login")));
    }

    @Test
    public void accessDeniedTest() throws Exception{
        this.mockMvc.perform(get("/main")) //проверяем, при переходе на эту страницу
                .andDo(print())
                .andExpect(status().is3xxRedirection()) //чтобы получили переадресацию
                .andExpect(redirectedUrl("http://localhost/login"));//на эту страницу
    }

    @Test
    @Sql(value = {"/create-user-before.sql", "/messages-list-before.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) //сперва выполняем этот скрипт
    @Sql(value = {"/messages-list-after.sql", "/create-user-after.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)//потом этот
    public void correctLoginTest() throws Exception{
        this.mockMvc.perform(formLogin().user("admin").password("123")) //обращаемся к форму логина с такими данными
                .andDo(print())
                .andExpect(status().is3xxRedirection())//если пришел переадресация
                .andExpect(redirectedUrl("/")); //на главную страницу
    }
    @Test
    public void badCredentials()throws Exception{
        this.mockMvc.perform(post("/login").param("user", "Alfred")) //проверка на не существующего юзера
                .andDo(print())
                .andExpect(status().isForbidden());
    }
}
