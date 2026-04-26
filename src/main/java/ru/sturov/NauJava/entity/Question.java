package ru.sturov.naujava.entity;

import java.util.Objects;

public class Question {

    private Long id;
    private String topic;
    private String text;
    private String difficulty;
    private String correctAnswer;

    public Question() {
    }

    public Question(Long id, String topic, String text, String difficulty, String correctAnswer) {
        this.id = id;
        this.topic = topic;
        this.text = text;
        this.difficulty = difficulty;
        this.correctAnswer = correctAnswer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question question)) return false;
        return Objects.equals(id, question.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", topic='" + topic + '\'' +
                ", text='" + text + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", correctAnswer='" + correctAnswer + '\'' +
                '}';
    }
}