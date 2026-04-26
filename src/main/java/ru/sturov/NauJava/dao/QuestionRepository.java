package ru.sturov.naujava.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.sturov.naujava.entity.Question;
import ru.sturov.naujava.dao.CrudRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class QuestionRepository implements CrudRepository<Question, Long> {

    private final List<Question> questionContainer;

    @Autowired
    public QuestionRepository(List<Question> questionContainer) {
        this.questionContainer = questionContainer;
    }

    @Override
    public void create(Question entity) {
        questionContainer.add(entity);
    }

    @Override
    public Question read(Long id) {
        return questionContainer.stream()
                .filter(question -> question.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void update(Question entity) {
        for (int i = 0; i < questionContainer.size(); i++) {
            Question current = questionContainer.get(i);
            if (current.getId().equals(entity.getId())) {
                questionContainer.set(i, entity);
                return;
            }
        }
    }

    @Override
    public void delete(Long id) {
        questionContainer.removeIf(question -> question.getId().equals(id));
    }

    public List<Question> findAll() {
        return List.copyOf(questionContainer);
    }

    public boolean existsById(Long id) {
        return questionContainer.stream().anyMatch(question -> question.getId().equals(id));
    }

    public Optional<Question> findOptionalById(Long id) {
        return questionContainer.stream()
                .filter(question -> question.getId().equals(id))
                .findFirst();
    }

    public List<Question> findByTopic(String topic) {
        return questionContainer.stream()
                .filter(question -> question.getTopic().equalsIgnoreCase(topic))
                .toList();
    }
}