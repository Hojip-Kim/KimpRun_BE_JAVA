package kimp.scrap.dto.bithumb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BithumbProps {
    private BithumbPageProps pageProps;
    @JsonProperty("__N_SSP")
    private boolean nssp;
}
