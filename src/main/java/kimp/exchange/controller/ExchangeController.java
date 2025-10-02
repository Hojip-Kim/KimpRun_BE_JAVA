package kimp.exchange.controller;

import kimp.exchange.dto.exchange.request.ExchangeCreateRequestDto;
import kimp.exchange.dto.exchange.response.ExchangeDto;
import kimp.exchange.service.ExchangeService;
import kimp.exchange.vo.*;
import kimp.exception.response.ApiResponse;
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
    public ApiResponse<List<ExchangeDto>> getExchange() {
        List<ExchangeDto> exchangeResponseDtos = exchangeService.getExchanges();
        return ApiResponse.success(exchangeResponseDtos);
    }

    @GetMapping("/{id}")
    public ApiResponse<ExchangeDto> getExchange(@PathVariable("id") Long id) {
        GetExchangeVo vo = new GetExchangeVo(id);
        ExchangeDto exchange = exchangeService.getExchange(vo);
        return ApiResponse.success(exchange);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER','OPERATOR')")
    @PostMapping("/create")
    public ApiResponse<ExchangeDto> createExchange(@AuthenticationPrincipal UserDetails UserDetails , @RequestBody ExchangeCreateRequestDto request) {

        CreateExchangeVo vo = new CreateExchangeVo(request);
        ExchangeDto exchangeResponseDto = this.exchangeService.createExchange(vo);
        return ApiResponse.success(exchangeResponseDto);
    }
}
