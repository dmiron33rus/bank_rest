package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.Role;

/**
 * DTO для создания нового пользователя в системе.
 * Содержит данные, необходимые для регистрации пользователя
 *
 * @param username уникальное имя пользователя (логин)
 * @param password пароль пользователя (должен быть зашифрован)
 * @param role роль пользователя в системе определяющая уровень доступа
 */
public record CreateUserDto(
        String username,
        String password,
        Role role
) {
}
