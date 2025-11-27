package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardDto;
import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.util.CardCrypto;
import com.example.bankcards.util.CardMasker;
import com.example.bankcards.util.Luhn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardCrypto cardCrypto;
    private static final Logger log = LoggerFactory.getLogger(CardServiceImpl.class);

    public CardServiceImpl(CardRepository cardRepository, UserRepository userRepository, CardCrypto cardCrypto) {
        this.cardRepository = cardRepository;
        this.userRepository = userRepository;
        this.cardCrypto = cardCrypto;
    }

    @Override
    public List<CardDto> getAllCards() {
        return cardRepository.findAll()
                .stream().
                map(c -> new CardDto(
                        c.getId(),
                        c.getOwner().getId(),
                        CardMasker.mask(cardCrypto.decrypt(c.getCardNumber())),
                        c.getStatus(),
                        c.getBalance()))
                .toList();
    }

    @Override
    public CardDto createCard(CreateCardDto cardDto) {
        log.debug("Добавление карты для пользователя: {}", cardDto.ownerId());
        var card = new Card();
        card.setOwner(userRepository.findById(cardDto.ownerId())
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден")));
        if (!Luhn.isValid(cardDto.number())) {
            throw new InvalidCardNumberException("Некорректный номер карты");
        }
        card.setCardNumber(cardCrypto.encrypt(cardDto.number()));
        card.setExpirationDate(cardDto.expiryDate());
        card.setStatus(CardStatus.ACTIVE);
        card.setBalance(cardDto.balance());
        var savedCard = cardRepository.save(card);
        String masked = CardMasker.mask(cardDto.number());
        log.debug("Карта успешно создана: {}", savedCard.getId());
        return new CardDto(savedCard.getId(), savedCard.getOwner().getId(),
                masked, savedCard.getStatus(), savedCard.getBalance());
    }

    @Override
    public CardDto blockCard(Long cardId) {
        log.debug("Запрос на блокировку карты: {}", cardId );
        var card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена"));
        card.setStatus(CardStatus.BLOCKED);
        var savedCard = cardRepository.save(card);
        String masked = CardMasker.mask(cardCrypto.decrypt(savedCard.getCardNumber()));
        log.debug("Заблокирована карта с id: {}", cardId);
        return new CardDto(savedCard.getId(), savedCard.getOwner().getId(),
                masked, savedCard.getStatus(), savedCard.getBalance());
    }

    @Override
    public CardDto activateCard(Long cardId) {
        log.debug("Запрос активации карты с id: {}", cardId );
        var card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена"));
        card.setStatus(CardStatus.ACTIVE);
        var savedCard = cardRepository.save(card);
        String masked = CardMasker.mask(cardCrypto.decrypt(savedCard.getCardNumber()));
        log.debug("Карта с id: {} активирована", cardId);
        return new CardDto(savedCard.getId(), savedCard.getOwner().getId(),
                masked, savedCard.getStatus(), savedCard.getBalance());
    }

    @Override
    public void deleteCard(Long cardId) {
        log.debug("Запрос на удаление карты id: {}", cardId );
        if (!cardRepository.existsById(cardId)) {
            throw new CardNotFoundException("Карта не найдена");
        }
        cardRepository.deleteById(cardId);
        log.debug("Карта удалена: {}", cardId );
    }

    @Override
    public CardDto requestBlock(Long userId, Long cardId) {
        log.debug("Запрос на блокировку карты от пользователя: {}, {}", cardId, userId);
        var card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена"));
        if (!card.getOwner().getId().equals(userId)) {
            throw new ForbiddenOperationException("Нельзя блокировать чужую карту");
        }
        card.setStatus(CardStatus.BLOCKED);
        var savedCard = cardRepository.save(card);
        String masked = CardMasker.mask(cardCrypto.decrypt(savedCard.getCardNumber()));
        log.debug("Карта успешно заблокирована: {}", cardId);
        return new CardDto(savedCard.getId(), savedCard.getOwner().getId(),
                masked, savedCard.getStatus(), savedCard.getBalance());
    }

    @Override
    public void transfer(Long userId, TransferDto dto) {
        log.debug("Запрос на перевод средств от пользователя: {}, {}", userId, dto);
        if (dto.fromCardId().equals(dto.toCardId())) {
            throw new InvalidTransferException("Карты совпадают");
        }
        if (dto.amount() == null || dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("Сумма должна быть больше 0");
        }
        var from = cardRepository.findByIdAndOwnerId(userId, dto.fromCardId())
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена"));
        var to = cardRepository.findByIdAndOwnerId(userId, dto.toCardId())
                .orElseThrow(() -> new CardNotFoundException("Карта не найдена"));
        if (from.getStatus() != CardStatus.ACTIVE || to.getStatus() != CardStatus.ACTIVE) {
            throw new InvalidTransferException("Обе карты должны быть активированы");
        }
        if (from.getBalance().compareTo(dto.amount()) < 0) {
            throw new InsufficientFundsException("Недостаточно средств");
        }
        from.setBalance(from.getBalance().subtract(dto.amount()));
        to.setBalance(to.getBalance().add(dto.amount()));
        cardRepository.save(from);
        cardRepository.save(to);
        log.debug("Перевод выполнен");
    }

    @Override
    public Page<CardDto> getUserCards(Long ownerId, CardStatus status, Pageable pageable) {
        var page = (status == null)
                ? cardRepository.findByOwnerId(ownerId, pageable)
                : cardRepository.findByOwnerIdAndStatus(ownerId, status, pageable);
        return page.map(c -> new CardDto(
                c.getId(),
                c.getOwner().getId(),
                CardMasker.mask(cardCrypto.decrypt(c.getCardNumber())),
                c.getStatus(),
                c.getBalance()
        ));
    }
}
