package com.example.bankcards.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO для создания новой банковской карты.
 * Содержит данные, необходимые для создания карты в системе
 *
 * @param ownerId идентификатор владельца карты
 * @param number номер карты в открытом виде (должен содержать 16-19 цифр)
 * @param expiryDate дата окончания действия карты в формате ГГГГ-ММ-ДД
 * @param balance начальный баланс карты (может быть 0 для новой карты)
 */
public record CreateCardDto(
        Long ownerId,
        String number,
        LocalDate expiryDate,
        BigDecimal balance
) {
}
