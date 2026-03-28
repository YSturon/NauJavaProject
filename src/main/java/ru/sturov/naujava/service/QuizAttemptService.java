package ru.sturov.naujava.service;

import java.util.List;
import ru.sturov.naujava.dto.AttemptSubmission;
import ru.sturov.naujava.entity.QuizAttempt;

/** Сервис для выполнения транзакционной операции отправки попытки прохождения теста. */
public interface QuizAttemptService {

    /**
     * Сохраняет попытку прохождения теста вместе с выбранными ответами.
     *
     * <p>Операция должна быть атомарной: если один из ответов невалиден, вся попытка должна откатиться.
     *
     * @param quizId идентификатор теста
     * @param userName имя пользователя
     * @param submissions список ответов пользователя
     * @return сохраненная попытка с рассчитанным результатом
     */
    QuizAttempt submitAttempt(Long quizId, String userName, List<AttemptSubmission> submissions);
}
