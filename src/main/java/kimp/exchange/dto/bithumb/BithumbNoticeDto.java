package kimp.exchange.dto.bithumb;

import com.fasterxml.jackson.annotation.JsonProperty;
import kimp.exchange.dto.notice.ExchangeNoticeDto;
import kimp.market.Enum.MarketType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/*
*
* {
    "props": {
        "pageProps": {
            "status": "ok",
            "noticeList": [
                {
                    "id": 1648694,
                    "boardType": "1",
                    "categoryName1": "신규서비스",
                    "categoryName2": null,
                    "title": "친구초대 서비스 오픈 - 빗썸이 처음인 친구 초대하고 최대 1억 받아 가세요! (안내 사항 일부 변경)",
                    "topFixYn": "Y",
                    "publicationDateTime": "2025-06-09 17:03:16",
                    "modifyDateTime": "2025-06-09 17:23:38",
                    "modifyDateTimeExposureYn": "Y"
                },
                {
                    "id": 1648715,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "가상자산 출금 지연 제도 재시행 안내",
                    "topFixYn": "Y",
                    "publicationDateTime": "2025-06-02 14:30:00",
                    "modifyDateTime": "2025-06-02 14:56:51",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1648709,
                    "boardType": "1",
                    "categoryName1": "이벤트",
                    "categoryName2": null,
                    "title": "거래할수록 매일 커지는 상금! 제2회 빗썸 거래왕 안내 (오픈 일정 연기 및 리그 추가 안내)",
                    "topFixYn": "Y",
                    "publicationDateTime": "2025-05-30 18:18:13",
                    "modifyDateTime": "2025-06-03 11:00:27",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1648126,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "빗썸 TOP 트레이더 리포트(4월) : 돈 잘 버는 사람들의 투자 비법 대공개!",
                    "topFixYn": "Y",
                    "publicationDateTime": "2025-05-02 17:00:28",
                    "modifyDateTime": "2025-05-07 10:07:51",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1648076,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "법인(법집행기관) 계좌 개설 안내",
                    "topFixYn": "Y",
                    "publicationDateTime": "2025-04-08 11:25:58",
                    "modifyDateTime": "2025-05-15 19:55:57",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1648063,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "빗썸, 국내 가상자산 거래소 '유동성 1위' 달성",
                    "topFixYn": "Y",
                    "publicationDateTime": "2025-04-03 17:03:55",
                    "modifyDateTime": "2025-04-07 11:05:24",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1647601,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "KB국민은행 원화 입출금 서비스 오픈 안내",
                    "topFixYn": "Y",
                    "publicationDateTime": "2025-03-24 11:00:19",
                    "modifyDateTime": "2025-05-12 12:03:06",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1645205,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "안쓰면 손해! 빗썸 고객님이라면 꼭 써야하는 필수 서비스를 소개합니다! (업데이트)",
                    "topFixYn": "Y",
                    "publicationDateTime": "2025-02-20 17:00:16",
                    "modifyDateTime": "2025-04-07 11:06:16",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1647001,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "100만원 미만 가상자산 출금 방식 변경 안내 (04/01 오픈)",
                    "topFixYn": "Y",
                    "publicationDateTime": "2025-02-14 18:00:10",
                    "modifyDateTime": "2025-05-23 19:14:56",
                    "modifyDateTimeExposureYn": "Y"
                },
                {
                    "id": 1645391,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "창립 11주년 기념 - 두번째, 거래소 이동 지원금 대상 확대 및 혜택 업그레이드! 3억원 상당의 투자지원금을 드립니다!",
                    "topFixYn": "Y",
                    "publicationDateTime": "2025-01-09 18:55:48",
                    "modifyDateTime": "2025-05-08 13:55:14",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1645297,
                    "boardType": "1",
                    "categoryName1": "이벤트",
                    "categoryName2": null,
                    "title": "창립 11주년 기념 - 일곱번째, 빗썸으로 입금하면 최대 100만원 상당의 혜택을 드립니다!",
                    "topFixYn": "Y",
                    "publicationDateTime": "2024-12-06 19:57:31",
                    "modifyDateTime": "2025-06-02 12:01:04",
                    "modifyDateTimeExposureYn": "Y"
                },
                {
                    "id": 1645178,
                    "boardType": "1",
                    "categoryName1": "신규서비스",
                    "categoryName2": null,
                    "title": "창립 11주년 기념 - 두번째, 거래소 이동 지원금 프로그램 오픈! 지금 빗썸으로 오시면 최소 100만원 ~ 최대 20억원 상당을 지원해 드립니다!",
                    "topFixYn": "Y",
                    "publicationDateTime": "2024-10-30 19:46:00",
                    "modifyDateTime": "2025-04-11 15:22:57",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1644003,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "빗썸 사칭 주의 안내 (06/13 업데이트)",
                    "topFixYn": "Y",
                    "publicationDateTime": "2024-10-02 19:27:01",
                    "modifyDateTime": "2025-06-13 11:11:08",
                    "modifyDateTimeExposureYn": "Y"
                },
                {
                    "id": 1644950,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "｢가상자산 이용자 보호 등에 관한 법률｣ 시행에 따른 거래시 유의사항 안내",
                    "topFixYn": "Y",
                    "publicationDateTime": "2024-07-18 15:30:34",
                    "modifyDateTime": "2024-10-28 17:00:05",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1640868,
                    "boardType": "1",
                    "categoryName1": "거래유의",
                    "categoryName2": "거래지원종료",
                    "title": "거래유의종목 및 거래지원 종료 일정 안내",
                    "topFixYn": "Y",
                    "publicationDateTime": "2024-07-05 10:00:00",
                    "modifyDateTime": "2025-06-09 17:02:04",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1642918,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "가상자산 거래에 관한 위험 고지",
                    "topFixYn": "Y",
                    "publicationDateTime": "2022-05-20 18:00:01",
                    "modifyDateTime": "2022-05-20 18:00:01",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1648803,
                    "boardType": "1",
                    "categoryName1": "신규서비스",
                    "categoryName2": null,
                    "title": "상승신호 서비스 오픈! 상승 가능성이 높은 가상자산을 알려드립니다!",
                    "topFixYn": "N",
                    "publicationDateTime": "2025-06-12 19:01:26",
                    "modifyDateTime": "2025-06-12 19:09:47",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1648794,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "KB국민은행 시스템 점검 작업으로 인한 계좌연결 서비스 일시 중단 안내",
                    "topFixYn": "N",
                    "publicationDateTime": "2025-06-12 16:00:36",
                    "modifyDateTime": "2025-06-12 18:35:58",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1648795,
                    "boardType": "1",
                    "categoryName1": "이벤트",
                    "categoryName2": null,
                    "title": "버블맵스(BMT) 메이커 리워드 에어드랍 이벤트",
                    "topFixYn": "N",
                    "publicationDateTime": "2025-06-12 12:00:00",
                    "modifyDateTime": "2025-06-12 11:19:28",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1648793,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "이오스트(IOST) 에어드랍 3차 지급 안내",
                    "topFixYn": "N",
                    "publicationDateTime": "2025-06-11 16:25:00",
                    "modifyDateTime": "2025-06-11 16:04:58",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1648728,
                    "boardType": "1",
                    "categoryName1": "입출금",
                    "categoryName2": null,
                    "title": "디와이디엑스(DYDX) 메인넷 전환으로 인한 입출금 중지 안내 (6/11 재개)",
                    "topFixYn": "N",
                    "publicationDateTime": "2025-06-11 14:00:00",
                    "modifyDateTime": "2025-06-11 12:21:36",
                    "modifyDateTimeExposureYn": "Y"
                },
                {
                    "id": 1648786,
                    "boardType": "1",
                    "categoryName1": "마켓 추가",
                    "categoryName2": null,
                    "title": "도그위프햇(WIF), 포켓네트워크(POKT) 원화 마켓 추가",
                    "topFixYn": "N",
                    "publicationDateTime": "2025-06-10 11:14:38",
                    "modifyDateTime": "2025-06-10 18:02:06",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1648783,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "빗썸 이용약관 개정 안내",
                    "topFixYn": "N",
                    "publicationDateTime": "2025-06-09 20:05:11",
                    "modifyDateTime": "2025-06-09 20:05:11",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1648780,
                    "boardType": "1",
                    "categoryName1": "입출금",
                    "categoryName2": null,
                    "title": "제타체인(ZETA) 입출금 일시 중지 안내 (06/13 재개)",
                    "topFixYn": "N",
                    "publicationDateTime": "2025-06-09 18:30:00",
                    "modifyDateTime": "2025-06-13 09:24:26",
                    "modifyDateTimeExposureYn": "Y"
                },
                {
                    "id": 1648779,
                    "boardType": "1",
                    "categoryName1": "입출금",
                    "categoryName2": null,
                    "title": "세이(SEI) 입출금 일시 중지 안내 (06/11 재개)",
                    "topFixYn": "N",
                    "publicationDateTime": "2025-06-09 18:00:00",
                    "modifyDateTime": "2025-06-11 09:15:57",
                    "modifyDateTimeExposureYn": "Y"
                },
                {
                    "id": 1648782,
                    "boardType": "1",
                    "categoryName1": "거래유의",
                    "categoryName2": null,
                    "title": "알렉스(ALEX) 거래유의종목 지정",
                    "topFixYn": "N",
                    "publicationDateTime": "2025-06-09 17:01:14",
                    "modifyDateTime": "2025-06-12 07:11:55",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1648781,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "6월 1주차 가스(GAS) 에어드랍 지급 안내",
                    "topFixYn": "N",
                    "publicationDateTime": "2025-06-09 15:15:00",
                    "modifyDateTime": "2025-06-09 15:10:55",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1648778,
                    "boardType": "1",
                    "categoryName1": "입출금",
                    "categoryName2": null,
                    "title": "이오스트(IOST) 네트워크 전환으로 인한 입출금 중지 안내",
                    "topFixYn": "N",
                    "publicationDateTime": "2025-06-09 12:00:00",
                    "modifyDateTime": "2025-06-09 10:25:08",
                    "modifyDateTimeExposureYn": "N"
                },
                {
                    "id": 1648776,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "알렉스(ALEX) 유의촉구 및 알렉스(ALEX), 스택스(STX) 입출금 일시 중단 안내 (06/12 재개)",
                    "topFixYn": "N",
                    "publicationDateTime": "2025-06-06 20:15:05",
                    "modifyDateTime": "2025-06-12 14:04:03",
                    "modifyDateTimeExposureYn": "Y"
                },
                {
                    "id": 1648766,
                    "boardType": "1",
                    "categoryName1": "안내",
                    "categoryName2": null,
                    "title": "스테이킹 이용약관 개정 안내",
                    "topFixYn": "N",
                    "publicationDateTime": "2025-06-05 18:00:00",
                    "modifyDateTime": "2025-06-05 18:02:19",
                    "modifyDateTimeExposureYn": "N"
                }
            ],
            "totalCount": 4501,
            "categories": [
                {
                    "id": 5,
                    "name": "거래유의"
                },
                {
                    "id": 6,
                    "name": "거래지원종료"
                },
                {
                    "id": 15,
                    "name": "공시"
                },
                {
                    "id": 9,
                    "name": "마켓 추가"
                },
                {
                    "id": 2,
                    "name": "신규서비스"
                },
                {
                    "id": 1,
                    "name": "안내"
                },
                {
                    "id": 4,
                    "name": "업데이트"
                },
                {
                    "id": 8,
                    "name": "이벤트"
                },
                {
                    "id": 7,
                    "name": "입출금"
                },
                {
                    "id": 3,
                    "name": "점검"
                }
            ]
        },
        "__N_SSP": true
    },
    "page": "/notice",
    "query": {
        "page": "1"
    },
    "buildId": "8MFDhw5p_TIggsWNVI0-Q",
    "isFallback": false,
    "isExperimentalCompile": false,
    "gssp": true,
    "scriptLoader": []
}
* */

@Getter
@NoArgsConstructor
public class BithumbNoticeDto extends ExchangeNoticeDto<String> {

    @JsonProperty("props")
    private BithumbProps props;
    private String page;
    private BithumbQuery query;
    private String buildId;
    private boolean isFallback;
    private boolean isExperimentalCompile;
    private boolean gssp;
    private List<Object> scriptLoader;

    public BithumbNoticeDto(BithumbProps props, String page, BithumbQuery query, String buildId, boolean isFallback, boolean isExperimentalCompile, boolean gssp, List<Object> scriptLoader) {
        this.props = props;
        this.page = page;
        this.query = query;
        this.buildId = buildId;
        this.isFallback = isFallback;
        this.isExperimentalCompile = isExperimentalCompile;
        this.gssp = gssp;
        this.scriptLoader = scriptLoader;
    }
}
