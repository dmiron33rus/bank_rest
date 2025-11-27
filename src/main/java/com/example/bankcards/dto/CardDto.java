package com.example.bankcards.dto;

import com.example.bankcards.entity.enums.CardStatus;

import java.math.BigDecimal;

/**
 * Data Transfer Object для представления банковской карты.
 * Содержит основную информацию о карте для отображения клиенту
 *
 * @param id      уникальный идентификатор карты
 * @param ownerId идентификатор владельца карты
 * @param number  замаскированный номер карты (формат: **** **** **** 1234)
 * @param status  текущий статус карты
 * @param balance текущий баланс карты
 */
public record CardDto(
        long id,
        long ownerId,
        String number,
        CardStatus status,
        BigDecimal balance
) {
}
