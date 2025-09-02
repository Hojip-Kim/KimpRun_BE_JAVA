package kimp.websocket.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.components.impl.market.Upbit;
import kimp.market.dto.coin.common.market.UpbitDto;
import kimp.market.controller.MarketDataStompController;
import kimp.websocket.dto.response.UpbitReceiveDto;
import kimp.websocket.dto.request.TicketMessage;
import kimp.websocket.dto.request.TradeSubscribe;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class UpbitWebsocketClient extends WebSocketClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MarketDataStompController marketDataStompController;
    private final Upbit upbit;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> reconnectTask;

    private static volatile boolean isConnected = false;
    private static volatile boolean isReconnecting = false;

    public UpbitWebsocketClient(String serverUri, MarketDataStompController marketDataStompController, Upbit upbit) throws URISyntaxException {
        super(new URI(serverUri));
        this.marketDataStompController = marketDataStompController;
        this.upbit = upbit;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info(">>> [업비트] 웹소켓 연결 성공");

        isConnected = true;

        try {

            List<String> codes = upbit.getMarketList().getCryptoList();
            TicketMessage ticketMessage = new TicketMessage("test");
            TradeSubscribe tradeSubscribe = new TradeSubscribe("ticker", codes, false, true);

            String message = objectMapper.writeValueAsString(Arrays.asList(ticketMessage, tradeSubscribe));
            this.send(message);

        } catch (Exception e) {
            log.error("[업비트] 웹소켓 연결 실패", e);
        }
    }

    @Override
    public void onMessage(String message) {

    }

    @Override
    public void onMessage(ByteBuffer message) {

        String receivedString = StandardCharsets.UTF_8.decode(message).toString();
        if(!"{\"status\":\"UP\"}".equals(receivedString)) {
            try {
                UpbitReceiveDto upbitReceiveDto = objectMapper.readValue(receivedString, UpbitReceiveDto.class);

                UpbitDto upbitDto = new UpbitDto(upbitReceiveDto.getCode().replace("KRW-", ""), upbitReceiveDto.getTradeVolume(), upbitReceiveDto.getSignedChangeRate(), upbitReceiveDto.getHighest52WeekPrice(), upbitReceiveDto.getLowest52WeekPrice(), upbitReceiveDto.getOpeningPrice(), upbitReceiveDto.getTradePrice(), upbitReceiveDto.getChange(), upbitReceiveDto.getAccTradePrice24h());

                marketDataStompController.inputDataToHashMap(upbitDto);

            } catch (Exception e) {
                log.error("[업비트] 웹소켓 DTO 변환 실패", e);
            }
        }else{
            synchronized (this) {
                isConnected = true;
                // 응답 도착하면 대기중 스레드를 꺠움
                this.notify();
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info(">>> [업비트] 웹소켓 연결 종료 성공");
        isConnected = false;
        attemptReConnect();
    }

    @Override
    public void onError(Exception ex) {
        log.error("[업비트] 웹소켓 에러 : " + ex.getMessage(), ex);
        isConnected = false;
        attemptReConnect();
    }

    /**
     * 별도의 스레드를 추가적으로 생성하여 재연결 로직 실행
     * 만일, 스레드를 생성하지않고 메인스레드를 사용한다면 reconnect가 되기 전까지 스레드 하나가 blocking상태로 존재할 것.
     *
     */
    public void attemptReConnect(){
        if (reconnectTask != null && !reconnectTask.isDone()) {
            reconnectTask.cancel(true); // 기존 재연결 작업 취소
        }

        reconnectTask = executor.submit(() -> {
            try {
                while (!isConnected) {
                    try {
                        Thread.sleep(5000);
                        log.info("[업비트] 웹소켓 재연결 시도");
                        this.reconnectBlocking();
                        if (this.isOpen() && checkNetworkConnect()) {
                            isConnected = true;
                            log.info("[업비트] 웹소켓 재연결 성공");
                        } else {
                            log.warn("[업비트] 연결은 성공하였으나, 네트워크 상태 불안정. 네트워크 연결 다시 시도");
                            isConnected = false;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // 스레드의 인터럽트 상태를 유지하며 스레드를 종료
                        log.error("[업비트] 웹소켓 재 연결 방해됨.", e);
                        break;
                    } catch (Exception e) {
                        log.error("[업비트] 웹소켓 재연결 실패", e);
                    }
                }
            } catch (Exception ex) {
                log.error("[업비트] 웹소켓 재연결 중 문제 발생", ex);
            } finally {
                if (isConnected) {
                    isReconnecting = false;
                }
            }
        });
    }


    private boolean checkNetworkConnect() {
        try{
            this.send("PING");

            // 3초간 blocking
            // ('pong'을 받고, isConnected를 true로 만드는 시간을 3초 간 기다림)
            synchronized (this){
                this.wait(3000);
            }
            return isConnected;
        } catch(InterruptedException e){
            log.error("[업비트] 네트워크 상태 확인 중 방해됨", e);
            Thread.currentThread().interrupt();
            return false;
        }catch(Exception e){
            log.error("[업비트] 네트워크 상태 확인 중 오류 발생", e);
            return false;
        }
    }

    @Scheduled(fixedRate = 30*1000)
    public void sendPingMessage(){
        if (isConnected){
            try{
                this.send("PING");
                log.info("[업비트] 연결 수립을 위한 PING 메시지 송신");
            }catch(Exception e){
                log.error("[업비트] PING 메시지 송신 실패", e);
            }
        }else{
            log.warn("[업비트] 웹소켓 서버 연결 안됨");
        }
    }
}