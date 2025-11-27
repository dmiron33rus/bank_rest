package com.example.bankcards.dto;

import java.math.BigDecimal;

/**
 * DTO для выполнения перевода средств между банковскими картами.
 * Содержит информацию о карте-отправителе, карте-получателе и сумме перевода
 *
 * @param fromCardId идентификатор карты-отправителя (с которой списываются средства)
 * @param toCardId идентификатор карты-получателя (на которую зачисляются средства)
 * @param amount сумма перевода (должна быть положительной)
 */
public record TransferDto(
        Long fromCardId,
        Long toCardId,
        BigDecimal amount
) {
}
