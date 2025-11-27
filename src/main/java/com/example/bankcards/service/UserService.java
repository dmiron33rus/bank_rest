package com.example.bankcards.service;

import com.example.bankcards.dto.CreateUserDto;
import com.example.bankcards.dto.UserDto;

import java.util.List;

/**
 * Сервис для управления пользователями системы.
 * Предоставляет функционал для административного управления пользователями
 */
public interface UserService {

    /**
     * Получает список всех пользователей системы
     *
     * @return список всех пользователей
     */
    List<UserDto> getAllUsers();

    /**
     * Получает пользователя по идентификатору
     *
     * @param id идентификатор пользователя
     * @return данные пользователя
     * @throws com.example.bankcards.exception.UserNotFoundException если пользователь не найден
     */
    UserDto getUserById(long id);

    /**
     * Создает нового пользователя в системе
     *
     * @param createUserDto данные для создания пользователя
     * @return созданный пользователь
     * @throws com.example.bankcards.exception.UsernameAlreadyExistsException если пользователь с таким username уже существует
     * @throws com.example.bankcards.exception.InvalidUserDataException если данные пользователя невалидны
     */
    UserDto createUser(CreateUserDto createUserDto);

    /**
     * Обновляет данные существующего пользователя
     *
     * @param userId идентификатор пользователя для обновления
     * @param dto новые данные пользователя
     * @return обновленный пользователь
     * @throws com.example.bankcards.exception.UserNotFoundException если пользователь не найден
     * @throws com.example.bankcards.exception.UsernameAlreadyExistsException если данные пользователя невалидны
     */
    UserDto updateUser(long userId, CreateUserDto dto);

    /**
     * Удаляет пользователя из системы
     *
     * @param userId идентификатор пользователя для удаления
     * @throws com.example.bankcards.exception.UserNotFoundException если пользователь не найден
     */
    void deleteUser(long userId);
}
