package com.example.bankcards.controller;

import com.example.bankcards.dto.CreateUserDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "Администрирование пользователями",
        description = "API для административного управления пользователями")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all")
    @Operation(summary = "Получить всех пользователей",
            description = "Запрос на получение всех пользователей в системе")
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID",
            description = "Запрос на получение пользователя")
    public UserDto getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/create")
    @Operation(summary = "Создать нового пользователя",
            description = "Запрос на создание нового пользователя")
    public UserDto createUser(@RequestBody CreateUserDto dto) {
        return userService.createUser(dto);
    }

    @PatchMapping("/update/{id}")
    @Operation(summary = "Изменить данные пользователя",
            description = "Запрос на изменение данных пользователя")
    public UserDto updateUser(@PathVariable long id, @RequestBody CreateUserDto dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удалить пользователя",
            description = "Запрос на удаление пользователя из системы")
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }
}
