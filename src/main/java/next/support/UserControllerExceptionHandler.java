package next.support;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Kj Nam
 * @since 2017-05-11
 */
@ControllerAdvice
public class UserControllerExceptionHandler {
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnAuthorizedException.class)
    public ModelAndView redirect() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("user/login");

        return mav;
    }
}
