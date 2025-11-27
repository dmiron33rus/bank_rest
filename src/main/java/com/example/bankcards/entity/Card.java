package com.example.bankcards.entity;

import com.example.bankcards.entity.enums.CardStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Сущность банковской карты.
 * Представляет банковскую карту в системе с основной информацией и связью с владельцем
 */
@Entity
@Table(name = "cards")
public class Card {

    /**
     * Уникальный идентификатор карты
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Номер карты в зашифрованном виде.
     * Хранится в защищенном формате для безопасности
     */
    @Column(name = "card_number", nullable = false)
    private String cardNumber;

    /**
     * Владелец карты
     * Связь многие-к-одному с сущностью User
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    /**
     * Дата окончания действия карты
     * Формат: ГГГГ-ММ-ДД
     */
    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    /**
     * Текущий статус карты.
     * Определяет доступные операции с картой
     */
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    /**
     * Текущий баланс карты.
     * Хранится в формате decimal с двумя знаками после запятой
     */
    @Column
    private BigDecimal balance;

    /**
     * Конструктор по умолчанию для JPA
     */
    public Card() {
    }

    /**
     * Конструктор для создания новой карты
     *
     * @param cardNumber     номер карты в зашифрованном виде
     * @param owner          владелец карты
     * @param expirationDate дата окончания действия
     */
    public Card(String cardNumber, User owner, LocalDate expirationDate) {
        this.cardNumber = cardNumber;
        this.owner = owner;
        this.expirationDate = expirationDate;
        this.status = CardStatus.ACTIVE;
        this.balance = BigDecimal.ZERO;
    }

    /**
     * Получает уникальный идентификатор карты
     *
     * @return идентификатор карты
     */
    public Long getId() {
        return id;
    }

    /**
     * Устанавливает уникальный идентификатор карты
     *
     * @param id идентификатор карты
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Получает номер карты в зашифрованном виде
     *
     * @return зашифрованный номер карты
     */
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * Устанавливает номер карты в зашифрованном виде
     *
     * @param cardNumber зашифрованный номер карты
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * Получает владельца карты
     *
     * @return сущность пользователя-владельца
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Устанавливает владельца карты
     *
     * @param owner сущность пользователя-владельца
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * Получает дату окончания действия карты
     *
     * @return дата окончания действия
     */
    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    /**
     * Устанавливает дату окончания действия карты
     *
     * @param expirationDate дата окончания действия
     */
    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Получает текущий статус карты
     *
     * @return статус карты
     */
    public CardStatus getStatus() {
        return status;
    }

    /**
     * Устанавливает текущий статус карты
     *
     * @param status статус карты
     */
    public void setStatus(CardStatus status) {
        this.status = status;
    }

    /**
     * Получает текущий баланс карты
     *
     * @return баланс карты
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Устанавливает текущий баланс карты
     *
     * @param balance баланс карты
     */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id) && Objects.equals(cardNumber, card.cardNumber) && Objects.equals(owner, card.owner) && Objects.equals(expirationDate, card.expirationDate) && status == card.status && Objects.equals(balance, card.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, cardNumber, owner, expirationDate, status, balance);
    }
}
