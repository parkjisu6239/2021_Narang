package com.exp.narang;

import com.exp.narang.api.controller.UserController;
import com.exp.narang.api.model.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserTest {
    @InjectMocks
    private UserController userController;
    
    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    public void init(){
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    @DisplayName("이메일 중복됨")
    public void emailExistTest() throws Exception {
        // given : 어떤 데이터가 있을때
        String email = "abc@abc.com";
//        doReturn(false).when(userService).idExists(email); : 특정한 값을 반환해야 하는 경우
        // when : 어떤 동작을 실행하면
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/user/chkemail/" + email)
        );
        // then : 이런 결과가 나와야 한다
        final MvcResult mvcResult = resultActions.andExpect(status().isNotFound()).andReturn();
        final int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(404);
    }

    @Test
    @DisplayName("이메일 중복되지 않음")
    public void emailNotExistTest() throws Exception {
        // given : 어떤 데이터가 있을때
        String email = "나는너랑@나랑.com";
        // when : 어떤 동작을 실행하면
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/user/chkemail/" + email)
        );
        // then : 이런 결과가 나와야 한다
        final MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
        final int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);
    }

    @Test
    @DisplayName("이름 중복됨")
    public void usernameExistTest() throws Exception{
        // given
        String username = "abc";
        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/user/chkusername/" + username)
        );
        //then
        final MvcResult mvcResult = resultActions.andExpect(status().isNotFound()).andReturn();
        final int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(404);
    }

    @Test
    @DisplayName("이름 중복되지 않음")
    public void usernameNotExistTest() throws Exception{
        // given
        String username = "나는너랑너는누구";
        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/user/chkusername/" + username)
        );
        //then
        final MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
        final int status = mvcResult.getResponse().getStatus();
        assertThat(status).isEqualTo(200);
    }
}
