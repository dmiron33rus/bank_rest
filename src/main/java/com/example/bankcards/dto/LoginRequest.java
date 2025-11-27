package com.example.bankcards.dto;

/**
 * DTO для запроса аутентификации пользователя.
 * Содержит учетные данные для входа в систему
 *
 * @param username имя пользователя (логин)
 * @param password пароль пользователя в открытом виде
 */
public record LoginRequest(
        String username,
        String password
) {
}
