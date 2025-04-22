package kimp.exchange.service;

import kimp.exchange.dto.notice.NoticeParsedData;

import java.util.List;

public interface ScrapService {

    List<List<NoticeParsedData>> getNotices();

}
