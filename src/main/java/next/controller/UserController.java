package next.controller;

import next.dao.UserDao;
import next.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;

/**
 * @author Kj Nam
 * @since 2017-05-08
 */
@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserDao userDao = UserDao.getInstance();

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView index(HttpSession session) throws SQLException {
        if (!UserSessionUtils.isLogined(session)) {
            return new ModelAndView("redirect:/users/loginForm");
        }

        ModelAndView mav = new ModelAndView("user/list");
        mav.addObject("users", userDao.findAll());
        return mav;
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public ModelAndView profile(HttpServletRequest req, @PathVariable String userId) throws SQLException {
        User user = userDao.findByUserId(userId);

        if (!UserSessionUtils.isSameUser(req.getSession(), user)) {
            throw new IllegalStateException("본인 프로필만 조회할 수 있습니다.");
        }

        ModelAndView mav = new ModelAndView("user/profile");
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/loginForm", method = RequestMethod.GET)
    public String loginForm() {
        return "user/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(HttpSession session, User user) {
        if (user == null) {
            throw new NullPointerException("사용자를 찾을 수 없습니다.");
        }

        User anUser = userDao.findByUserId(user.getUserId());
        if (anUser.matchPassword(user.getPassword())) {
            session.setAttribute("user", user);
            return "redirect:/";
        } else {
            throw new IllegalStateException("비밀번호가 틀립니다.");
        }
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String form() {
        return "user/form";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String create(User user) {
        logger.debug("User : {}", user);
        userDao.insert(user);
        return "redirect:/";
    }

    @RequestMapping(value = "/{userId}/form", method = RequestMethod.GET)
    public ModelAndView updateForm(HttpServletRequest req, @PathVariable String userId) {
        User user = userDao.findByUserId(userId);

        if (!UserSessionUtils.isSameUser(req.getSession(), user)) {
            throw new IllegalStateException("다른 사용자의 정보를 수정할 수 없습니다.");
        }

        ModelAndView mav = new ModelAndView("user/updateForm");
        mav.addObject("user", user);
        return mav;
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public String update(HttpServletRequest req, User user) {
        User anUser = userDao.findByUserId(user.getUserId());

        if (!UserSessionUtils.isSameUser(req.getSession(), anUser)) {
            throw new IllegalStateException("다른 사용자의 정보를 수정할 수 없습니다.");
        }

        User updateUser = new User(
                user.getUserId(), user.getPassword(),
                user.getName(), user.getEmail());
        logger.debug("Update User : {}", updateUser);
        userDao.update(updateUser);
        return "redirect:/";
    }
}
