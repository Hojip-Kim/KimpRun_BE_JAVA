package kimp.exchange.controller;

import kimp.exchange.dto.exchange.request.ExchangeCreateRequestDto;
import kimp.exchange.dto.exchange.response.ExchangeDto;
import kimp.exchange.service.ExchangeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("exchange")
public class ExchangeController {

    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<ExchangeDto>> getExchange() {
        List<ExchangeDto> exchangeResponseDtos = exchangeService.getExchanges();

        return ResponseEntity.ok(exchangeResponseDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExchangeDto> getExchange(@PathVariable("id") Long id) {
        ExchangeDto exchange = exchangeService.getExchange(id);
        return ResponseEntity.ok(exchange);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR')")
    @PostMapping("/create")
    public ResponseEntity<ExchangeDto> createExchange(@AuthenticationPrincipal UserDetails UserDetails , @RequestBody ExchangeCreateRequestDto request) {

        ExchangeDto exchangeResponseDto = this.exchangeService.createExchange(request);

        return ResponseEntity.ok(exchangeResponseDto);
    }
}
