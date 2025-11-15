package cz.uhk.zlesak.threejslearningapp.services;

import cz.uhk.zlesak.threejslearningapp.api.clients.QuizApiClient;
import cz.uhk.zlesak.threejslearningapp.domain.quiz.QuizEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Scope("prototype")
public class QuizService {
    private final QuizApiClient quizApiClient;
    private QuizEntity quizEntity = null;

    @Autowired
    public QuizService(QuizApiClient quizApiClient) {
        this.quizApiClient = quizApiClient;
    }
}
