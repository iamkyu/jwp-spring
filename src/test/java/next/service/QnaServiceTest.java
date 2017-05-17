package next.service;

import next.CannotOperateException;
import next.dao.AnswerDao;
import next.dao.QuestionDao;
import next.model.Answer;
import next.model.Question;
import next.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;

import static org.mockito.Mockito.when;

/**
 * @author Kj Nam
 * @since 2017-05-15
 */
@RunWith(MockitoJUnitRunner.class)
public class QnaServiceTest {

    @Mock private QuestionDao questionDao;
    @Mock private AnswerDao answerDao;
    private QnaService qnaService;
    private User anUser;

    @Before
    public void setUp() {
        qnaService = new QnaService(questionDao, answerDao);
        anUser = new User("iamkyu", "pass", "kyu", "a@b.c");
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void 존재하지_않는_질문을_삭제하면_예외발생() throws CannotOperateException {
        //given
        when(qnaService.findById(1L)).thenReturn(null);

        //when
        qnaService.deleteQuestion(1L, anUser);

        //then
        //exception
    }

    @Test(expected = CannotOperateException.class)
    public void 타인의_질문을_삭제하면_예외발생() throws CannotOperateException {
        //given
        when(qnaService.findById(1L)).thenReturn(new Question("other", "title", "content"));

        //when
        qnaService.deleteQuestion(1L, anUser);

        //then
        //exception
    }

    @Test
    public void 자신이_작성한_질문을_삭제한다() throws CannotOperateException {
        //given
        when(qnaService.findById(1L)).thenReturn(new Question(anUser.getUserId(), "title", "content"));

        //when then
        qnaService.deleteQuestion(1L, anUser);
    }

    @Test
    public void 자신이_작성한_질문에_자신의_답변만_있으면_삭제한다() throws CannotOperateException {
        //given
        when(qnaService.findById(1L)).thenReturn(new Question(anUser.getUserId(), "title", "content"));
        when(qnaService.findAllByQuestionId(1L)).thenReturn(Arrays.asList(new Answer("iamkyu", "contents", 1L)));

        //when then
        qnaService.deleteQuestion(1L, anUser);
    }

    @Test(expected = CannotOperateException.class)
    public void 타인이_답변한_질문을_삭제하면_예외발생() throws CannotOperateException {
        //given
        when(qnaService.findById(1L)).thenReturn(new Question(anUser.getUserId(), "title", "content"));
        when(qnaService.findAllByQuestionId(1L)).thenReturn(Arrays.asList(new Answer("other", "contents", 1L)));

        //when then
        qnaService.deleteQuestion(1L, anUser);
    }
}