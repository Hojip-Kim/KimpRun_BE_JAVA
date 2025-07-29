package unit.kimp.market.entity;

import kimp.cmc.entity.coin.CmcCoinInfo;
import kimp.cmc.entity.coin.CmcCoinMeta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CmcCoinInfo Entity 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class CmcCoinInfoTest {

    @Test
    @DisplayName("CmcCoinInfo 기본 생성자 테스트")
    void shouldTestDefaultConstructor() {
        // When
        CmcCoinInfo cmcCoinInfo = new CmcCoinInfo();

        // Then
        assertThat(cmcCoinInfo.getId()).isEqualTo(0);
        assertThat(cmcCoinInfo.getDescription()).isNull();
        assertThat(cmcCoinInfo.isInfiniteSupply()).isFalse();
        assertThat(cmcCoinInfo.getIsFiat()).isEqualTo(0);
        assertThat(cmcCoinInfo.getLastUpdated()).isNull();
    }

    @Test
    @DisplayName("CmcCoinInfo 생성자 테스트")
    void shouldTestConstructor() {
        // Given
        CmcCoinMeta cmcCoinMeta = new CmcCoinMeta();
        String description = "Bitcoin is a cryptocurrency.";
        boolean infiniteSupply = false;
        int isFiat = 0;
        LocalDateTime lastUpdated = LocalDateTime.now();

        // When
        CmcCoinInfo cmcCoinInfo = new CmcCoinInfo(
                description,
                infiniteSupply,
                isFiat,
                lastUpdated
        );

        // Then
        assertThat(cmcCoinInfo.getDescription()).isEqualTo(description);
        assertThat(cmcCoinInfo.isInfiniteSupply()).isEqualTo(infiniteSupply);
        assertThat(cmcCoinInfo.getIsFiat()).isEqualTo(isFiat);
        assertThat(cmcCoinInfo.getLastUpdated()).isEqualTo(lastUpdated);
        assertThat(cmcCoinInfo.getCmcCoinMeta()).isNull();
    }

    @Test
    @DisplayName("TimeStamp 상속 테스트")
    void shouldTestTimeStampInheritance() {
        // When
        CmcCoinInfo cmcCoinInfo = new CmcCoinInfo();

        // Then
        assertThat(cmcCoinInfo.getRegistedAt()).isNull();
        assertThat(cmcCoinInfo.getUpdatedAt()).isNull();
    }
}