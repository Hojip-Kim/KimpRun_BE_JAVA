package kimp.test;

import jakarta.annotation.PostConstruct;
import kimp.community.dto.category.request.CreateCategoryRequestDto;
import kimp.community.service.CategoryService;
import kimp.exception.KimprunException;
import kimp.scrap.component.ExchangeScrap;
import kimp.scrap.component.impl.exchange.ExchangeScrapAbstract;
import kimp.scrap.dto.binance.BinanceNoticeDto;
import kimp.scrap.dto.bithumb.BithumbNoticeDto;
import kimp.scrap.dto.coinone.CoinoneNoticeDto;
import kimp.notice.dto.notice.NoticeParsedData;
import kimp.scrap.dto.upbit.UpbitNoticeDto;
import kimp.exchange.service.ExchangeService;
import kimp.notice.service.NoticeService;
import kimp.exchange.service.ScrapService;
import kimp.exchange.service.impl.ExchangeNoticePacadeService;
import kimp.market.Enum.MarketType;
import kimp.market.dto.coin.common.ServiceCoinDto;
import kimp.market.service.serviceImpl.CoinExchangePacadeService;
import kimp.user.entity.ActivityRank;
import kimp.user.entity.MemberRole;
import kimp.user.entity.SeedMoneyRange;
import kimp.user.enums.UserRole;
import kimp.user.service.AdminService;
import kimp.user.service.MemberRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class testDataInit {

    private final CategoryService categoryService;
    private final NoticeService noticeService;
    private final ScrapService scrapService;
    private final ExchangeNoticePacadeService exchangeNoticePacadeService;
    private final ExchangeService exchangeService;
    private final CoinExchangePacadeService coinExchangePacadeService;
    private final MemberRoleService memberRoleService;
    private final AdminService adminService;


    private final ExchangeScrap<UpbitNoticeDto> upbitScrapComponent;
    private final ExchangeScrap<BithumbNoticeDto> bithumbScrapComponent;
    private final ExchangeScrap<CoinoneNoticeDto> coinoneScrapComponent;
    private final ExchangeScrap<BinanceNoticeDto> binanceScrapComponent;

    public testDataInit(CategoryService categoryServer, NoticeService noticeService, ScrapService scrapService, ExchangeNoticePacadeService exchangeNoticePacadeService, ExchangeService exchangeService, CoinExchangePacadeService coinExchangePacadeService, MemberRoleService memberRoleService, AdminService adminService, ExchangeScrapAbstract<UpbitNoticeDto> upbitScrap, ExchangeScrapAbstract<BithumbNoticeDto> bithumbScrap, ExchangeScrapAbstract<CoinoneNoticeDto> coinoneScrap, ExchangeScrapAbstract<BinanceNoticeDto> binanceScrap) {
        this.categoryService = categoryServer;
        this.noticeService = noticeService;
        this.scrapService = scrapService;
        this.exchangeNoticePacadeService = exchangeNoticePacadeService;
        this.exchangeService = exchangeService;
        this.coinExchangePacadeService = coinExchangePacadeService;
        this.memberRoleService = memberRoleService;
        this.adminService = adminService;

        this.upbitScrapComponent = upbitScrap;
        this.bithumbScrapComponent = bithumbScrap;
        this.coinoneScrapComponent = coinoneScrap;
        this.binanceScrapComponent = binanceScrap;
    }

    @PostConstruct
    private void coinExchangePacadeServiceInit(){
        MarketType[] marketTypes = MarketType.values();

        for(MarketType marketType : marketTypes){
            List<MarketType> marketTypeList = new ArrayList<>();
            marketTypeList.add(marketType);
            List<ServiceCoinDto> serviceCoinDtoList = this.coinExchangePacadeService.getCoinsByExchange(marketType);
            if(serviceCoinDtoList != null){
                this.coinExchangePacadeService.createCoinBulk(marketTypeList, serviceCoinDtoList);
            }
        }
    }

    @PostConstruct
    private void init() {
        log.info("공지사항 초기 데이터 세팅 시작");
        
        // Python 서비스 의존성이 있는 초기화는 예외 처리
        try {
            initializeUpbitNotices();
        } catch (Exception e) {
            log.warn("Upbit 공지사항 초기화 실패 (Python 서비스 연결 필요): {}", e.getMessage());
        }

        try {
            initializeCoinoneNotices();
        } catch (Exception e) {
            log.warn("Coinone 공지사항 초기화 실패 (Python 서비스 연결 필요): {}", e.getMessage());
        }

        // 현재 주석 처리된 거래소들도 동일하게 처리

        try {
            initializeBinanceNotices();
        } catch (Exception e) {
            log.warn("Binance 공지사항 초기화 실패 (Python 서비스 연결 필요): {}", e.getMessage());
        }

        try {
            initializeBithumbNotices();
        } catch (Exception e) {
            log.warn("Bithumb 공지사항 초기화 실패 (Python 서비스 연결 필요): {}", e.getMessage());
        }

        log.info("공지사항 초기 데이터 세팅 완료 (Python 서비스 연결 실패 시 스킵됨)");
    }

    /**
     * Upbit 공지사항 초기화
     */
    private void initializeUpbitNotices() throws IOException {
        log.debug("Upbit 공지사항 초기화 시작");
        List<NoticeParsedData> upbitNoticeParsedDataList = upbitScrapComponent.parseNoticeData();
        
        if (!upbitNoticeParsedDataList.isEmpty()) {
            upbitScrapComponent.setNewParsedData(upbitNoticeParsedDataList);
            exchangeNoticePacadeService.createNoticesBulk(upbitScrapComponent.getMarketType(), upbitNoticeParsedDataList);
            log.info("Upbit 공지사항 {} 개 초기화 완료", upbitNoticeParsedDataList.size());
        } else {
            log.warn("Upbit 공지사항 데이터가 비어있음");
        }
    }

    /**
     * Coinone 공지사항 초기화
     */
    private void initializeCoinoneNotices() throws IOException {
        log.debug("Coinone 공지사항 초기화 시작");
        List<NoticeParsedData> coinoneNoticeParsedDataList = coinoneScrapComponent.parseNoticeData();
        
        if (!coinoneNoticeParsedDataList.isEmpty()) {
            coinoneScrapComponent.setNewParsedData(coinoneNoticeParsedDataList);
            exchangeNoticePacadeService.createNoticesBulk(coinoneScrapComponent.getMarketType(), coinoneNoticeParsedDataList);
            log.info("Coinone 공지사항 {} 개 초기화 완료", coinoneNoticeParsedDataList.size());
        } else {
            log.warn("Coinone 공지사항 데이터가 비어있음");
        }
    }

    /**
     * Binance 공지사항 초기화
     */
    private void initializeBinanceNotices() throws IOException {
        log.debug("Binance 공지사항 초기화 시작");
        List<NoticeParsedData> binanceNoticeParsedDataList = binanceScrapComponent.parseNoticeData();
        
        if (!binanceNoticeParsedDataList.isEmpty()) {
            binanceScrapComponent.setNewParsedData(binanceNoticeParsedDataList);
            exchangeNoticePacadeService.createNoticesBulk(binanceScrapComponent.getMarketType(), binanceNoticeParsedDataList);
            log.info("Binance 공지사항 {} 개 초기화 완료", binanceNoticeParsedDataList.size());
        } else {
            log.warn("Binance 공지사항 데이터가 비어있음");
        }
    }

    /**
     * Bithumb 공지사항 초기화
     */
    private void initializeBithumbNotices() throws IOException {
        log.debug("Bithumb 공지사항 초기화 시작");
        List<NoticeParsedData> bithumbNoticeParsedDataList = bithumbScrapComponent.parseNoticeData();
        
        if (!bithumbNoticeParsedDataList.isEmpty()) {
            bithumbScrapComponent.setNewParsedData(bithumbNoticeParsedDataList);
            exchangeNoticePacadeService.createNoticesBulk(bithumbScrapComponent.getMarketType(), bithumbNoticeParsedDataList);
            log.info("Bithumb 공지사항 {} 개 초기화 완료", bithumbNoticeParsedDataList.size());
        } else {
            log.warn("Bithumb 공지사항 데이터가 비어있음");
        }
    }

    @PostConstruct
    private void categoryServiceInit(){
        String[] initCategory = {"전체","코인","보안","기타"};

        for(String category : initCategory){
            this.categoryService.createCategory(new CreateCategoryRequestDto(category));
        }
    }

    @PostConstruct
    private void memberRoleServiceInit() {
        for(UserRole request : UserRole.values()) {
            String randomUuid = UUID.randomUUID().toString();
            try {
                MemberRole memberRole = memberRoleService.getRoleByName(request);
            }catch(KimprunException e) {
                memberRoleService.createRole(randomUuid, request);
            }
        }
    }

    @PostConstruct
    private void activityRankInit() {
        log.info("ActivityRank 초기 데이터 세팅 시작");
        String[] activityGrades = {"새싹", "일반회원", "우수회원", "마스터", "운영자"};
        
        for(String grade : activityGrades) {
                Optional<ActivityRank> foundActivityRank = adminService.getActivityRankByGrade(grade);
                if(foundActivityRank.isPresent()){
                    log.debug("ActivityRank '{}' 이미 존재함", grade);
                    continue;
                }else {
                    ActivityRank activityRank = adminService.createActivityRank(grade);
                    log.info("ActivityRank '{}' 생성 완료", grade);
                }
            }
        log.info("ActivityRank 초기 데이터 세팅 완료");
    }

    @PostConstruct
    private void seedMoneyRangeInit() {
        log.info("SeedMoneyRange 초기 데이터 세팅 시작");
        String[][] seedMoneyData = {
                {"0 ~ 1000만원", "Bronze"},
                {"1000만원 ~ 5000만원", "Silver"},
                {"5000만원 ~ 1억원", "Gold"},
                {"1억원 ~ 5억원", "Platinum"},
                {"5억원 ~ 10억원", "Diamond"},
                {"10억원 ~ 100억원", "Master"},
                {"100억원 이상", "King"}
        };

        for (String[] data : seedMoneyData) {
            String range = data[0];
            String rank = data[1];

            Optional<SeedMoneyRange> foundSeedMoneyRange = adminService.getSeedMoneyRangeByRank(rank);
            if (foundSeedMoneyRange.isPresent()) {
                log.debug("SeedMoneyRange '{}' 이미 존재함", rank);
                continue;
            } else {
                SeedMoneyRange seedMoneyRange = adminService.createSeedMoneyRange(range, rank);
                log.info("SeedMoneyRange '{}' ('{}') 생성 완료", rank, range);
            }
            log.info("SeedMoneyRange 초기 데이터 세팅 완료");
        }
    }
}
