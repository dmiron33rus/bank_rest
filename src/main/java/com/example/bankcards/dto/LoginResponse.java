package com.example.bankcards.dto;

/**
 * DTO для ответа на успешную аутентификацию пользователя.
 * Содержит токен доступа для авторизации последующих запросов
 *
 * @param token JWT токен доступа, который должен передаваться в заголовке Authorization
 */
public record LoginResponse(String token) {
}
