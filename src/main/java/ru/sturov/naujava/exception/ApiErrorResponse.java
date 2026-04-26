package ru.sturov.naujava.exception;

import java.time.LocalDateTime;

/**
 * Унифицированный формат ошибки REST API.
 *
 * @param timestamp время формирования ошибки
 * @param status HTTP-код
 * @param error краткое имя HTTP-ошибки
 * @param message сообщение об ошибке
 * @param path путь запроса
 */
public record ApiErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {}
