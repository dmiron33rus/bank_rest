package com.example.bankcards.dto;


import com.example.bankcards.entity.enums.Role;

/**
 * DTO для представления данных пользователя в системе.
 * Содержит основную информацию о пользователе для отображения клиенту
 *
 * @param id уникальный идентификатор пользователя
 * @param username имя пользователя (логин)
 * @param role роль пользователя в системе, определяющая уровень доступа
 */
public record UserDto(
        long id,
        String username,
        Role role
) {
}
