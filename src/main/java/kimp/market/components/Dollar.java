package kimp.market.components;

/**
 * 달러 환율 정보를 조회하는 컴포넌트 인터페이스
 */
public interface Dollar {
    
    /**
     * API를 통해 현재 달러/원 환율을 조회
     * 
     * @return 현재 달러/원 환율
     */
    double getApiDollar();
}