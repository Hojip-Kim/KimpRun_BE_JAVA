package kimp.member.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class NicknameGeneratorUtils {

    public NicknameGeneratorUtils() {
    }

    public String createRandomNickname(){
        String[] actions = {
                "코인 사고 있는",
                "코인 팔고 있는",
                "코인 주워담고 있는",
                "코인 던지고 있는",
                "코인 거래하고 있는",
                "차트 분석하고 있는",
                "코인 값 예측하고 있는",
                "차트 지켜보고 있는",
                "언제 오르나 기다리고 있는",
                "코인 전송하고 있는",
                "코인 쥐고 있는",
                "잔고 관리하고 있는",
                "코인으로 결제하고 있는",
                "환전소 찾고 있는",
                "코인에 투자하고 있는",
                "차트 눈 빠지게 보고 있는",
                "얼마 버나 계산하고 있는",
                "지갑 선택하고 있는",
                "코인 폭망하고 있는",
                "코인 대박 나고 있는",
                "몇 배 수익 벌고 있는",
                "존버하다가 잃고 있는",
                "떡상 희망하고 있는",
                "코인 공부하고 있는",
                "코인 홍보하고 있는",
                "코인 좋다고 전도하고 있는",
                "코인 검색하고 있는",
                "상승 문의하고 있는",
                "초보에게 설명하고 있는",
                "코인 광고 보고 있는",
                "수익 자랑하고 있는",
                "코인 숨겨두고 있는",
                "비트에 의존하고 있는",
                "대출로 코인 사고 있는",
                "빚내서 코인 사고 있는",
                "코인방에서 상의하고 있는",
                "망할까 고민하고 있는",
                "친구들 설득하고 있는",
                "내 코인 방어하고 있는",
                "떨어지는 거 해명하고 있는",
                "코인 가지고 논쟁하고 있는",
                "코인에 열광하고 있는",
                "코인 떨어져 좌절하고 있는",
                "코인 관련 논의하고 있는",
                "친구에게 조언하고 있는",
                "어떤 코인 살지 추천하고 있는",
                "존버 결심하고 있는",
                "코인 폭망해서 파산하고 있는",
                "떨어지면 극복하고 있는",
                "코인에 도전하고 있는",
                "떡상 쫓아가고 있는",
                "코인에 밀어넣고 있는",
                "물타기 하려고 뛰어들고 있는",
                "돈 잃고 나가떨어지고 있는",
                "대박 날거라 예고하고 있는",
                "코인판에 참견하고 있는",
                "떡상을 믿고 있는",
                "차트 신뢰하고 있는",
                "코인 값 조정하고 있는",
                "코인떡락에 호들갑 떨고 있는",
                "조바심내고 있는",
                "떡상 기다리며 조용히 하고 있는",
                "떡락에 낙담하고 있는",
                "회복하길 기원하고 있는",
                "목표 금액 달성하고 있는",
                "한탕 실행하고 있는",
                "떡락에 초조해하고 있는",
                "떡상할 때까지 기다리고 있는",
                "손절할까 말까 내려놓고 있는",
                "수익 기록하고 있는",
                "얼마 버나 측정하고 있는",
                "코인 고수들 참고하고 있는",
                "계좌에 코인 적립하고 있는",
                "다른 코인이랑 비교하고 있는",
                "코인 값 추적하고 있는",
                "코인에 몰두하고 있는",
                "코인 망해서 떠나고 있는",
                "후회하고 있는",
                "떡상해서 이기고 있는",
                "떡락해서 패배하고 있는",
                "코인 지원하고 있는",
                "코인 안된다고 부정하고 있는",
                "존버를 고집하고 있는",
                "코인 단념하고 있는",
                "다시 재도전하고 있는",
                "코인에 유혹당하고 있는",
                "떡락 방해하고 있는",
                "손절각 피하고 있는",
                "차트 평가하고 있는",
                "코인 정보 조사하고 있는",
                "계좌 정리하고 있는",
                "손절하고 계좌 삭제하고 있는",
                "코인방에서 피드백하고 있는",
                "대박날거 상상하고 있는",
                "떡락 의심하고 있는",
                "코인판에서 협력하고 있는",
                "대박에 저항하고 있는",
                "계획 수정하고 있는",
                "예상 정정하고 있는",
                "코인판 응원하고 있는"
        };

        String[] name = {
                "차트만 보는 코인충",
                "대출받은 코인충",
                "손절 못하는 코인충",
                "물타기하는 코인충",
                "희망회로 돌리는 코인충",
                "비트코인만 믿는 코인충",
                "알트코인 빠는 코인충",
                "거래소 들락날락하는 코인충",
                "하락장에 눈물 흘리는 코인충",
                "친구들한테 코인 전도하는 코인충",
                "아무 코인이나 사는 코인충",
                "호재만 기다리는 코인충",
                "유튜브만 보는 코인충",
                "커뮤니티 글만 읽는 코인충",
                "차트 분석하는 척하는 코인충",
                "돈 빌려서 코인 사는 코인충",
                "시드 다 날린 코인충",
                "에어드랍 노리는 코인충",
                "신규 상장 코인충",
                "한탕 노리는 코인충",
                "떡상만 바라보는 코인충",
                "친구한테 추천받은 코인충",
                "FOMO에 당한 코인충",
                "리플만 믿는 코인충",
                "이더리움 신봉하는 코인충",
                "텔레그램방에서 활동하는 코인충",
                "새벽에 차트 확인하는 코인충",
                "상승장에 환호하는 코인충",
                "떡락에 멘붕 오는 코인충",
                "출금 버튼 못 누르는 코인충",
                "전업 투자자 코인충",
                "파생상품까지 손댄 코인충",
                "무지성 매수하는 코인충",
                "호들하고 있는 코인충",
                "손실보고도 웃는 코인충",
                "차트 패턴 외우는 코인충",
                "거래소 쿠폰 쓰는 코인충",
                "시그널만 기다리는 코인충",
                "에브리타임에 코인글 쓰는 코인충",
                "테슬라 주식 팔고 코인 사는 코인충",
                "주식 접고 코인하는 코인충",
                "지인이랑 코인 정보 교환하는 코인충",
                "내기 코인충",
                "에어드랍 참여하는 코인충",
                "믿고 맡기는 코인충",
                "지갑 비밀번호 잊은 코인충",
                "해외 거래소 쓰는 코인충",
                "친구 따라 코인 시작한 코인충",
                "모임에서 코인 얘기하는 코인충",
                "정체불명의 코인 파는 코인충",
                "커뮤니티에서 자랑하는 코인충",
                "사이버머니만 믿는 코인충",
                "피눈물 흘리는 코인충",
                "가족들한테 숨기는 코인충",
                "배우자한테 들킨 코인충",
                "자취방 월세 밀리는 코인충",
                "노트북으로 차트 보는 코인충",
                "직장에서 몰래 차트 보는 코인충",
                "알람 맞춰두고 자는 코인충",
                "유명 유튜버 따라 하는 코인충",
                "초단타 매매하는 코인충",
                "한강뷰 찾는 코인충",
                "경매 사이트 보고 있는 코인충",
                "오픈씨에 NFT 올리는 코인충",
                "자칭 전문가 코인충",
                "포트폴리오 짜는 코인충",
                "밈코인에 몰빵하는 코인충",
                "레버리지까지 쓰는 코인충",
                "선물거래 도전하는 코인충",
                "자기 코인 찬양하는 코인충",
                "디파이 하는 코인충",
                "메타버스 코인에 빠진 코인충",
                "채굴하는 코인충",
                "인플루언서 팔로우하는 코인충",
                "낙서만 하는 코인충",
                "피쳐폰 쓰는 코인충",
                "체인 분석하는 코인충",
                "시세 변동에 울고 웃는 코인충",
                "디센트럴라이즈드 파이낸스 코인충",
                "지갑 분실한 코인충",
                "수수료 아까워하는 코인충",
                "거래소 레벨업 하는 코인충",
                "시드 늘리려는 코인충",
                "패닉셀 하는 코인충",
                "신용카드로 코인 사는 코인충",
                "손가락만 물어뜯는 코인충",
                "밈코인 쫓아다니는 코인충",
                "공부 안 하고 코인만 하는 코인충",
                "실패하고 또 사는 코인충",
                "코인 인생 역전 노리는 코인충",
                "레딧에서 정보 찾는 코인충",
                "도박판 가는 코인충",
                "수익률 자랑하는 코인충",
                "손실 감추는 코인충",
                "코인게임만 하는 코인충",
                "집에 가기 싫어하는 코인충",
                "PC방에서 코인 차트 보는 코인충"
        };


        Random random = new Random();

        int actionIdx = random.nextInt(actions.length);
        int nameIdx = random.nextInt(name.length);

        return actions[actionIdx] + name[nameIdx];

    }
}
