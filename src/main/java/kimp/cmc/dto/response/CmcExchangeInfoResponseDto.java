package kimp.cmc.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
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

    public CmcExchangeInfoResponseDto(String name, String slug, List<String> fiats, String description, 
                                    String logo, BigDecimal fee, BigDecimal spotVolumeUsd, String url, 
                                    Boolean isSupported, LocalDateTime dateLaunched, LocalDateTime updatedAt) {
        this.name = name;
        this.slug = slug;
        this.fiats = fiats != null ? fiats : new ArrayList<>();
        this.description = description;
        this.logo = logo;
        this.fee = fee;
        this.spotVolumeUsd = spotVolumeUsd;
        this.url = url;
        this.isSupported = isSupported;
        this.dateLaunched = dateLaunched;
        this.updatedAt = updatedAt;
    }
}
