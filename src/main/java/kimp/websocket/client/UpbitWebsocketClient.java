package kimp.websocket.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.components.Upbit;
import kimp.websocket.dto.response.UpbitDto;
import kimp.websocket.dto.response.UpbitReceiveDto;
import kimp.websocket.dto.request.TicketMessage;
import kimp.websocket.dto.request.TradeSubscribe;
import kimp.websocket.handler.WebSocketHandler;
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

@Slf4j
public class UpbitWebsocketClient extends WebSocketClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final WebSocketHandler webSocketHandler;
    private final Upbit upbit;

    private static volatile boolean isConnected = false;
    private static volatile boolean isReconnecting = false;

    public UpbitWebsocketClient(String serverUri, WebSocketHandler webSocketHandler, Upbit upbit) throws URISyntaxException {
        super(new URI(serverUri));
        this.webSocketHandler = webSocketHandler;
        this.upbit = upbit;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info(">>> Connected to Upbit Websocket Successful");

        isConnected = true;



        try {
            List<String> codes = upbit.getMarketList().getMarkets();
            TicketMessage ticketMessage = new TicketMessage("test");
            TradeSubscribe tradeSubscribe = new TradeSubscribe("ticker", codes, false, true);

            String message = objectMapper.writeValueAsString(Arrays.asList(ticketMessage, tradeSubscribe));
            this.send(message);

        } catch (Exception e) {
            log.error("Failed to send subscribe message", e);
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

                webSocketHandler.inputDataToHashMap(upbitDto);

            } catch (Exception e) {
                log.error("Failed to convert message to Dto", e);
            }
        }else{
            synchronized (this) {
                isConnected = true;
                this.notify(); // 응답 도착하면 대기중 스레드를 꺠움
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info(">>> DisConnected to Upbit Websocket Successful");
        isConnected = false;
        attemptReConnect();
    }

    @Override
    public void onError(Exception ex) {
        log.error("An error occurred: " + ex.getMessage(), ex);
        isConnected = false;
        attemptReConnect();
    }

    /**
     * 별도의 스레드를 추가적으로 생성하여 재연결 로직 실행
     * 만일, 스레드를 생성하지않고 메인스레드를 사용한다면 reconnect가 되기 전까지 스레드 하나가 blocking상태로 존재할 것.
     *
     */
    public void attemptReConnect(){
        if(isReconnecting){
            return;
        }
        isReconnecting = true;
        new Thread(() -> {
            try {
                log.info("재연결 시도중...");
                while (!isConnected) {
                    try {
                        Thread.sleep(5000);
                        log.info("재연결 시도");
                        this.reconnectBlocking();
                        if (this.isOpen() && checkNetworkConnect()) {
                            isConnected = true;
                            log.info("재연결 성공");
                        }else{
                            log.warn("연결은 성공하였으나, 네트워크 상태 불안정. 네트워크 연결 다시 시도");
                            isConnected = false;
                        }
                    } catch (InterruptedException e) {
                        log.error("재 연결 방해됨.", e);
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        log.error("재연결 실패", e);
                    }
                }
            } catch (Exception ex) {
                log.error("재연결 중 문제 발생", ex);
            } finally {
                if(isConnected) {
                    isReconnecting = false;
                }
            }
        }).start();
    }


    private boolean checkNetworkConnect() {
        try{
            this.send("PING");

            synchronized (this){
                this.wait(10000);
            }
            return isConnected;
        } catch(InterruptedException e){
            log.error("네트워크 상태 확인 중 방해됨", e);
            Thread.currentThread().interrupt();
            return false;
        }catch(Exception e){
            log.error("네트워크 상태 확인 중 오류 발생", e);
            return false;
        }
    }

    @Scheduled(fixedRate = 30*1000)
    public void sendPingMessage(){
        if (isConnected){
            try{
                this.send("PING");
                log.info("PING message set to server");
            }catch(Exception e){
                log.error("Failed to send PING message", e);
            }
        }else{
            log.warn("Websocket server is not connected");
        }
    }
}