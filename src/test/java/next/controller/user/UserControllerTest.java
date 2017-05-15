package next.controller.user;

import next.dao.UserDao;
import next.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Kj Nam
 * @since 2017-05-15
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @Mock private UserDao userDao;
    private UserController controller;

    @Before
    public void setUp() {
        controller = new UserController(userDao);
    }

    @Test
    public void profileTest() throws Exception {
        //given
        ExtendedModelMap model = new ExtendedModelMap();

        User mockUser = new User("iamkyu", "pass", "kyu", "a@b.c");
        when(userDao.findByUserId("iamkyu")).thenReturn(mockUser);

        //when
        controller.profile("iamkyu", model);
        User user = (User) model.get("user");

        //then
        assertThat(user.getUserId(), is("iamkyu"));
        assertThat(user.getPassword(), is("pass"));
    }

    @Test
    public void createTest() throws Exception {
        //given
        User mockUser = new User("iamkyu", "pass", "kyu", "a@b.c");

        //when
        controller.create(mockUser);

        //then
        verify(userDao).insert(mockUser);
    }
}