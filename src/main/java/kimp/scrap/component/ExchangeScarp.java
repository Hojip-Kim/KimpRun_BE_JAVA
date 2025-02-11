package kimp.scrap.component;

import java.io.IOException;
import java.net.URI;

public interface ExchangeScarp<T> {

    public T getNoticeFromAPI(URI webSiteUrl) throws IOException;
}
