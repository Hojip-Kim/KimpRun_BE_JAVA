package kimp.exchange.component;

import kimp.notice.dto.notice.NoticeParsedData;
import kimp.notice.dto.notice.NoticeResponseDto;
import kimp.market.Enum.MarketType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.List;

public interface ExchangeScrap<T> {

    // API를 통해 공지사항 데이터를 받아옴.
    public T getNoticeFromAPI() throws IOException;

    // 공지사항 데이터와 공지사항 절대경로(상세페이지 절대 경로)를 dto로 바꿔주는 메소드.
    public NoticeResponseDto convertNoticeDataToDto() throws IOException;

    // 공지사항 데이터를 해시화 한 후, 레디스에 저장하는 메서드
    public void setNoticeToRedis(List<NoticeParsedData> noticeParsedDataList);

    // 해시화 된 공지사항 데이터를 레디스에서 불러오는 메서드
    public String getNoticeFromRedis() throws IOException;

    // 새로운 공지사항 데이터를 뽑아내는 메서드
    // 새로운 데이터를 두 리스트의 비교 (Big(O^2)성능)의 형태로 구현
    // 이후 리팩토링을 통해 성능개선 필요
    public List<NoticeParsedData> getNewNotice(List<NoticeParsedData> recentNoticeDataList);

    public boolean isUpdatedNotice(String savedRedisHashCode, List<NoticeParsedData> recentNoticeDataList);

    // 새로운 공지사항 데이터(이전과, 새로운 데이터 중 최신데이터만)를 set하는 메서드
    public void setNewNotice(List<NoticeParsedData> notice);

    // 공지사항 데이터 (전체)를 set하는 메서드
    public void setNewParsedData(List<NoticeParsedData> parsedData);

    // 외부 API에 적용 할 헤더를 뽑는 메서드
    public HttpHeaders getHeaders();

    // 외부 API에 적용 할 메서드(GET, POST)를 뽑는 메서드
    public HttpMethod getMethod();

    // 공지사항 데이터 리스트를 뽑는 메서드
    public List<NoticeParsedData> getNoticeData();

    // 외부 공지사항 API url을 뽑는 메서드
    public String getNoticeUrl();

    // 클라우드플레어 우회가 필요한지를 결정하는 메서드
    public boolean isNecessityOfDetour();

    // 외부 API를 불러낸 데이터를 NoticeParsedData의 형태로 가공하는 메서드(다른 거래소와 공통적으로 필요한 필드)
    public List<NoticeParsedData> parseNoticeData() throws IOException;

    // 공지사항 클릭 시에 대한 상세페이지 url (절대경로, 가변하는 공지사항 값을 제외한 경로값)
    public String getAbsoluteUrl();

    public MarketType getMarketType();

    public List<NoticeParsedData> getFieldNewNotice();

}