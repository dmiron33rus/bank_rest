package com.example.bankcards.service.impl;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardDto;
import com.example.bankcards.dto.TransferDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.*;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardCrypto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    private CardCrypto cardCrypto;

    private CardServiceImpl cardService;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        cardCrypto = new CardCrypto("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=");
        cardService = new CardServiceImpl(cardRepository, userRepository, cardCrypto);
    }

    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void createCard_ok() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("alice");
        owner.setPassword("enc");
        owner.setRole(Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> {
            Card saved = inv.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        CreateCardDto dto = new CreateCardDto(
                1L,
                "4532015112830366",
                LocalDate.now().plusYears(2),
                new BigDecimal("100.00")
        );

        CardDto created = cardService.createCard(dto);

        assertEquals(10L, created.id());
        assertEquals(1L, created.ownerId());
        assertEquals(CardStatus.ACTIVE, created.status());
        assertEquals(new BigDecimal("100.00"), created.balance());

        verify(userRepository).findById(1L);
        verify(cardRepository).save(any(Card.class));
        verifyNoMoreInteractions(cardRepository, userRepository);
    }

    @Test
    void requestBlock_onlyOwnerAllowed_okAfterFix() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("alice");
        owner.setPassword("enc");
        owner.setRole(Role.USER);

        String encNumber = cardCrypto.encrypt("4111111111111111");

        Card existing = new Card();
        existing.setId(11L);
        existing.setOwner(owner);
        existing.setCardNumber(encNumber);
        existing.setExpirationDate(LocalDate.now().plusYears(2));
        existing.setStatus(CardStatus.ACTIVE);
        existing.setBalance(new BigDecimal("10.00"));

        when(cardRepository.findById(11L)).thenReturn(Optional.of(existing));
        when(cardRepository.save(any(Card.class))).thenAnswer(inv -> inv.getArgument(0));

        CardDto result = cardService.requestBlock(1L, 11L);

        assertEquals(CardStatus.BLOCKED, result.status());

        ArgumentCaptor<Card> captor = ArgumentCaptor.forClass(Card.class);
        verify(cardRepository).findById(11L);
        verify(cardRepository).save(captor.capture());

        Card saved = captor.getValue();
        assertEquals(11L, saved.getId());
        assertEquals(1L, saved.getOwner().getId());
        assertEquals(CardStatus.BLOCKED, saved.getStatus());

        verifyNoMoreInteractions(cardRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void requestBlock_forbiddenForForeignCard() {
        User owner = new User();
        owner.setId(2L);
        owner.setUsername("bob");
        owner.setPassword("enc");
        owner.setRole(Role.USER);

        String encNumber = cardCrypto.encrypt("4111111111111111");

        Card existing = new Card();
        existing.setId(11L);
        existing.setOwner(owner);
        existing.setCardNumber(encNumber);
        existing.setExpirationDate(LocalDate.now().plusYears(2));
        existing.setStatus(CardStatus.ACTIVE);
        existing.setBalance(new BigDecimal("10.00"));

        when(cardRepository.findById(11L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> cardService.requestBlock(1L, 11L))
                .isInstanceOf(ForbiddenOperationException.class)
                .hasMessageContaining("Нельзя блокировать чужую карту");

        verify(cardRepository).findById(11L);
        verifyNoMoreInteractions(cardRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void transfer_betweenOwnCards_okAfterFix() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("alice");
        owner.setPassword("enc");
        owner.setRole(Role.USER);

        Card from = new Card();
        from.setId(101L);
        from.setOwner(owner);
        from.setCardNumber(cardCrypto.encrypt("4111111111111111"));
        from.setExpirationDate(LocalDate.now().plusYears(2));
        from.setStatus(CardStatus.ACTIVE);
        from.setBalance(new BigDecimal("100.00"));

        Card to = new Card();
        to.setId(102L);
        to.setOwner(owner);
        to.setCardNumber(cardCrypto.encrypt("5555555555554444"));
        to.setExpirationDate(LocalDate.now().plusYears(2));
        to.setStatus(CardStatus.ACTIVE);
        to.setBalance(new BigDecimal("50.00"));

        when(cardRepository.findByIdAndOwnerId(1L, 101L)).thenReturn(Optional.of(from));
        when(cardRepository.findByIdAndOwnerId(1L, 102L)).thenReturn(Optional.of(to));

        cardService.transfer(1L, new TransferDto(101L,
                102L,
                new BigDecimal("60.00")));

        assertEquals(new BigDecimal("40.00"), from.getBalance());
        assertEquals(new BigDecimal("110.00"), to.getBalance());

        verify(cardRepository).findByIdAndOwnerId(1L, 101L);
        verify(cardRepository).findByIdAndOwnerId(1L, 102L);
        verify(cardRepository).save(from);
        verify(cardRepository).save(to);
        verifyNoMoreInteractions(cardRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void transfer_forbidden_ifAnyCardNotOwned() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("alice");
        owner.setPassword("enc");
        owner.setRole(Role.USER);

        User other = new User();
        other.setId(2L);
        other.setUsername("bob");
        other.setPassword("enc");
        other.setRole(Role.USER);

        Card from = new Card();
        from.setId(101L);
        from.setOwner(owner);
        from.setCardNumber(cardCrypto.encrypt("4111111111111111"));
        from.setExpirationDate(LocalDate.now().plusYears(2));
        from.setStatus(CardStatus.ACTIVE);
        from.setBalance(new BigDecimal("100.00"));

        Card to = new Card();
        to.setId(102L);
        to.setOwner(other);
        to.setCardNumber(cardCrypto.encrypt("5555555555554444"));
        to.setExpirationDate(LocalDate.now().plusYears(2));
        to.setStatus(CardStatus.ACTIVE);
        to.setBalance(new BigDecimal("50.00"));

        when(cardRepository.findByIdAndOwnerId(1L, 101L)).thenReturn(Optional.of(from));
        when(cardRepository.findByIdAndOwnerId(1L, 102L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.transfer(1L,
                new TransferDto(101L,
                        102L,
                        new BigDecimal("10.00"))))
                .isInstanceOf(CardNotFoundException.class) // 🔧 Изменилось исключение!
                .hasMessageContaining("Карта не найдена");

        verify(cardRepository).findByIdAndOwnerId(1L, 101L);
        verify(cardRepository).findByIdAndOwnerId(1L, 102L);
        verifyNoMoreInteractions(cardRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void transfer_insufficientFunds() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("alice");
        owner.setPassword("enc");
        owner.setRole(Role.USER);

        Card from = new Card();
        from.setId(101L);
        from.setOwner(owner);
        from.setCardNumber(cardCrypto.encrypt("4111111111111111"));
        from.setExpirationDate(LocalDate.now().plusYears(2));
        from.setStatus(CardStatus.ACTIVE);
        from.setBalance(new BigDecimal("10.00"));

        Card to = new Card();
        to.setId(102L);
        to.setOwner(owner);
        to.setCardNumber(cardCrypto.encrypt("5555555555554444"));
        to.setExpirationDate(LocalDate.now().plusYears(2));
        to.setStatus(CardStatus.ACTIVE);
        to.setBalance(new BigDecimal("50.00"));

        when(cardRepository.findByIdAndOwnerId(1L, 101L)).thenReturn(Optional.of(from));
        when(cardRepository.findByIdAndOwnerId(1L, 102L)).thenReturn(Optional.of(to));

        assertThatThrownBy(() -> cardService.transfer(1L,
                new TransferDto(101L,
                        102L,
                        new BigDecimal("60.00"))))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining("Недостаточно средств");

        verify(cardRepository).findByIdAndOwnerId(1L, 101L);
        verify(cardRepository).findByIdAndOwnerId(1L, 102L);
        verifyNoMoreInteractions(cardRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void transfer_sameCardIds() {
        assertThatThrownBy(() -> cardService.transfer(1L,
                new TransferDto(101L,
                        101L,
                        new BigDecimal("10"))))
                .isInstanceOf(InvalidTransferException.class)
                .hasMessageContaining("Карты совпадают");

        verifyNoInteractions(cardRepository, userRepository);
    }

    @Test
    void deleteCard_ok_and_notFound() {
        when(cardRepository.existsById(10L)).thenReturn(true);

        cardService.deleteCard(10L);

        verify(cardRepository).existsById(10L);
        verify(cardRepository).deleteById(10L);
        verifyNoMoreInteractions(cardRepository);

        reset(cardRepository);
        when(cardRepository.existsById(10L)).thenReturn(false);

        assertThatThrownBy(() -> cardService.deleteCard(10L))
                .isInstanceOf(CardNotFoundException.class)
                .hasMessageContaining("Карта не найдена");

        verify(cardRepository).existsById(10L);
        verifyNoMoreInteractions(cardRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void createCard_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        CreateCardDto dto = new CreateCardDto(
                99L,
                "4532015112830366",
                LocalDate.now().plusYears(2),
                new BigDecimal("100.00")
        );

        assertThatThrownBy(() -> cardService.createCard(dto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь не найден");

        verify(userRepository).findById(99L);
        verifyNoInteractions(cardRepository);
    }
}
