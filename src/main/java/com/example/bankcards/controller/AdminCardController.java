package com.example.bankcards.controller;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CreateCardDto;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cards")
@Tag(name = "Администрирование карт",
        description = "API для административного управления банковскими картами")
public class AdminCardController {

    private final CardService cardService;

    public AdminCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping
    @Operation(summary = "Получить все карты",
            description = "Возвращает список всех банковских карт в системе")
    public List<CardDto> getAll() {
        return cardService.getAllCards();
    }

    @PostMapping
    @Operation(summary = "Создать новую карту",
            description = "Создаёт новую банковскую карту для пользователя")
    public CardDto create(@RequestBody CreateCardDto cardDto) {
        return cardService.createCard(cardDto);
    }

    @PatchMapping("/{id}/block")
    @Operation(summary = "Заблокировать карту",
            description = "Блокирует карту по ID")
    public CardDto block(@PathVariable Long id) {
        return cardService.blockCard(id);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Активировать карту", description = "Активирует ранее блокированную карту по ID")
    public CardDto activate(@PathVariable Long id) {
        return cardService.activateCard(id);
    }

    @DeleteMapping("/{id}/delete")
    @Operation(summary = "Удалить карту", description = "Удаляет карту по ID")
    public void delete(@PathVariable Long id) {
        cardService.deleteCard(id);
    }

    @PatchMapping("/{userId}/request-block")
    @Operation(summary = "Заблокировать карту", description = "Запрос пользователя на блокировку карты")
    public CardDto requestBlock(@PathVariable Long userId, @RequestParam Long cardId) {
        return cardService.requestBlock(userId, cardId);
    }
}
