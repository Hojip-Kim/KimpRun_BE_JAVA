package unit.kimp.external.entity;

import kimp.cmc.entity.coin.CmcCoin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("CmcCoin Entity 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class CmcCoinTest {

    
    @Test
    @DisplayName("deleteCmcPlatform 메소드 테스트 - null 파라미터")
    void shouldThrowExceptionWhenDeleteCmcPlatformWithNullParameter() {
        // Given
        CmcCoin cmcCoin = new CmcCoin(
                1L, "logo.png", "Bitcoin", "BTC", "bitcoin", Boolean.TRUE, Boolean.TRUE, Boolean.TRUE,
                LocalDateTime.now().minusDays(1), LocalDateTime.now()
        );
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            cmcCoin.deleteCmcPlatform(null);
        });
    }
}