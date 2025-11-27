package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.Role;
import jakarta.persistence.*;


import java.util.Objects;
import java.util.Set;

/**
 * Сущность пользователя системы.
 * Представляет пользователя банковской системы с учетными данными и связанными картами
 */
@Entity
@Table(name = "users")
public class User {

    /**
     * Уникальный идентификатор пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальное имя пользователя (логин)
     * Используется для аутентификации в системе
     */
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    /**
     * Пароль пользователя в зашифрованном виде.
     * Хранится в защищенном формате (BCrypt)
     */
    @Column(nullable = false)
    private String password;

    /**
     * Роль пользователя в системе.
     * Определяет уровень доступа и права пользователя
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /**
     * Набор банковских карт, принадлежащих пользователю
     * Связь один-ко-многим с сущностью Card
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Card> cards;

    /**
     * Конструктор по умолчанию для JPA
     */
    public User() {
    }

    /**
     * Конструктор для создания нового пользователя
     * @param username имя пользователя
     * @param password зашифрованный пароль
     * @param role роль пользователя
     */
    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    /**
     * Получает уникальный идентификатор пользователя
     * @return идентификатор пользователя
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает уникальный идентификатор пользователя
     * @param id идентификатор пользователя
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Получает имя пользователя (логин)
     * @return имя пользователя
     */
    public String getUsername() {
        return username;
    }

    /**
     * Устанавливает имя пользователя (логин)
     * @param userName имя пользователя
     */
    public void setUsername(String userName) {
        this.username = userName;
    }

    /**
     * Получает пароль пользователя в зашифрованном виде
     * @return зашифрованный пароль
     */
    public String getPassword() {
        return password;
    }

    /**
     * Устанавливает пароль пользователя в зашифрованном виде
     * @param password зашифрованный пароль
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Получает роль пользователя в системе
     * @return роль пользователя
     */
    public Role getRole() {
        return role;
    }

    /**
     * Устанавливает роль пользователя в системе
     * @param role роль пользователя
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Получает набор банковских карт пользователя
     * @return набор карт пользователя
     */
    public Set<Card> getCards() {
        return cards;
    }

    /**
     * Устанавливает набор банковских карт пользователя
     * @param cards набор карт пользователя
     */
    public void setCards(Set<Card> cards) {
        this.cards = cards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && role == user.role && Objects.equals(cards, user.cards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, role, cards);
    }
}
