package kimp.cmc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CmcExchangeInfoResponseDto {
    private String name;
    private String slug;
    private List<String> fiats = new ArrayList<>();
    private String description;
    private String logo;
    private BigDecimal fee;
    private BigDecimal spotVolumeUsd;
    private String url;
    private Boolean isSupported;

    private LocalDateTime dateLaunched;
    private LocalDateTime updatedAt;
}
