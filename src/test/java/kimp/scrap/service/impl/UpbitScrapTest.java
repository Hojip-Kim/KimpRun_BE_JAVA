package kimp.scrap.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {kimp.scrap.component.impl.exchange.UpbitScrapTest.class})
public class UpbitScrapTest {

    @Autowired
    private kimp.scrap.component.impl.exchange.UpbitScrapTest upbitScrap; // 실제 스프링 빈

    @Test
    void testGetPageSource() throws Exception {

    }
}
