package com.example.demo;

import com.example.demo.controller.AccountController;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountTest {

    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(new AccountController()).build();
    }

    @Test
    public void getAccount() throws Exception {
        mvc.perform(
                MockMvcRequestBuilders
                        .get("/account/register")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("")));
    }

//    @Test
//    public void testPageQuery() throws Exception {
//        int page=1,size=10;
//        Sort sort = new Sort(Direction.DESC, Collections.emptyList().add("id"));
//        Pageable pageable = new PageRequest(page, size, sort);
//        userRepository.findALL(pageable);
//        userRepository.findByUserName("testName", pageable);
//    }

}
