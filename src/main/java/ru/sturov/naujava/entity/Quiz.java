package ru.sturov.naujava.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Тест, объединяющий набор вопросов одной предметной области.
 *
 * <p>Связан с {@link Category}, {@link Question} и {@link QuizAttempt}.
 */
@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer timeLimitMinutes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "quiz")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz")
    private List<QuizAttempt> attempts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public List<QuizAttempt> getAttempts() {
        return attempts;
    }

    public void setAttempts(List<QuizAttempt> attempts) {
        this.attempts = attempts;
    }
}
