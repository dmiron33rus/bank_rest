package com.example.bankcards.service;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardDto;
import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Сервис для управления банковскими картами.
 * Предоставляет функционал для административного управления картами и операций пользователей
 */
public interface CardService {

    /**
     * Получает список всех банковских карт в системе
     *
     * @return список всех карт
     */
    List<CardDto> getAllCards();

    /**
     * Создает новую банковскую карту для пользователя
     *
     * @param cardDto данные для создания карты
     * @return созданная карта
     * @throws com.example.bankcards.exception.UserNotFoundException если пользователь не найден
     * @throws com.example.bankcards.exception.InvalidCardNumberException если не корректный номер карты
     */
    CardDto createCard(CreateCardDto cardDto);

    /**
     * Блокирует карту по идентификатору
     *
     * @param cardId идентификатор карты
     * @return обновленная карта
     * @throws com.example.bankcards.exception.CardNotFoundException если карта не найдена
     */
    CardDto blockCard(Long cardId);

    /**
     * Активирует ранее заблокированную карту
     *
     * @param cardId идентификатор карты
     * @return обновленная карта
     * @throws com.example.bankcards.exception.CardNotFoundException если карта не найдена
     */
    CardDto activateCard(Long cardId);

    /**
     * Удаляет карту из системы
     *
     * @param cardId идентификатор карты
     * @throws com.example.bankcards.exception.CardNotFoundException если карта не найдена
     */
    void deleteCard(Long cardId);

    /**
     * Обрабатывает запрос пользователя на блокировку карты
     *
     * @param userId идентификатор пользователя
     * @param cardId идентификатор карты
     * @return обновленная карта
     * @throws com.example.bankcards.exception.CardNotFoundException если карта не найдена
     * @throws com.example.bankcards.exception.ForbiddenOperationException если пользователь не владелец карты
     */
    CardDto requestBlock(Long userId, Long cardId);

    /**
     * Выполняет перевод средств между картами пользователя
     *
     * @param userId идентификатор пользователя, инициирующего перевод
     * @param dto данные перевода
     * @throws com.example.bankcards.exception.CardNotFoundException если карта не найдена
     * @throws com.example.bankcards.exception.InsufficientFundsException если недостаточно средств
     * @throws com.example.bankcards.exception.InvalidTransferException если перевод невозможен
     * @throws com.example.bankcards.exception.ForbiddenOperationException если нет доступа к картам
     */
    void transfer(Long userId, TransferDto dto);

    /**
     * Получает карты пользователя с пагинацией и фильтрацией по статусу
     *
     * @param ownerId идентификатор владельца карт
     * @param status статус карты для фильтрации (опционально)
     * @param pageable параметры пагинации
     * @return страница с картами пользователя
     * @throws com.example.bankcards.exception.UserNotFoundException если пользователь не найден
     */
    Page<CardDto> getUserCards(Long ownerId, CardStatus status, Pageable pageable);
}
