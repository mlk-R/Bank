package ru.malik.bank.StartBank.controller;


import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ru.malik.bank.StartBank.controller.web.WebLoginController;

@WebMvcTest(WebLoginController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WebRegistrationControllerTest {

}
