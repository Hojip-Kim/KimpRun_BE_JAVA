package kimp.websocket.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.components.Dollar;
import kimp.market.components.impl.market.Binance;
import kimp.market.dto.coin.common.market.BinanceDto;
import kimp.market.handler.BinanceWebsocketHandler;
import kimp.websocket.dto.response.BinanceReceiveDto;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.*;

@Slf4j
public class BinanceWebSocketClient extends WebSocketClient {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BinanceWebsocketHandler binanceWebsocketHandler;
    private final Dollar dollar;
    private final Binance binance;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Future<?> reconnectTask;

    private static volatile boolean isConnected = false;
    private static volatile boolean isReconnecting = false;

    public BinanceWebSocketClient(String serverUri, BinanceWebsocketHandler binanceWebsocketHandler, Dollar dollar, Binance binance) throws URISyntaxException {
        super(new URI(serverUri));
        this.binanceWebsocketHandler = binanceWebsocketHandler;
        this.dollar = dollar;
        this.binance = binance;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        isConnected = true;
        log.info(">>> [바이낸스] 웹소켓 연결 성공");
    }

    @Override
    public void onMessage(String message) {
        try {
            BinanceReceiveDto binanceReceiveDto = objectMapper.readValue(message, BinanceReceiveDto.class);
            String token = binanceReceiveDto.getToken().replace("USDT", "");
            BigDecimal binancePrice = binanceReceiveDto.getPrice().multiply(BigDecimal.valueOf(dollar.getUSDKRW()));
//            BinanceStreamDto binanceDto = new BinanceStreamDto(
//                    binanceReceiveDto.getToken().replace("USDT", ""),
//                    binancePrice
//            );
            BinanceDto foundBinanceDto = binance.binanceDtosMap.get(token);

            BinanceDto binanceDto = new BinanceDto(
                    token,
                    foundBinanceDto.getTradeVolume24(),
                    foundBinanceDto.getChangeRate().divide(new BigDecimal(10)),
                    foundBinanceDto.getHighestPricePer52(),
                    foundBinanceDto.getLowestPricePer52(),
                    foundBinanceDto.getOpening_price(),
                    binancePrice,
                    foundBinanceDto.getRate_change(),
                    foundBinanceDto.getAcc_trade_price24()
            );
            binanceWebsocketHandler.inputDataToHashMap(binanceDto);
        } catch (Exception e) {
            log.error("[바이낸스] 메시지 파싱 실패: {}", message, e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        isConnected = false;
        log.warn(">>> [바이낸스] 웹소켓 연결 종료. 코드: {}, 원인: {}", code, reason);
        attemptReConnect();
    }

    @Override
    public void onError(Exception ex) {
        log.error("[바이낸스] 웹소켓 에러 발생: {}", ex.getMessage(), ex);
        if (!this.isOpen()) {
            isConnected = false;
            attemptReConnect();
        }
    }

    @Override
    public void onWebsocketPong(WebSocket conn, org.java_websocket.framing.Framedata f) {
        log.info("[바이낸스] 웹소켓 Pong 메시지 수신 완료");
    }

    public void attemptReConnect(){
        if (reconnectTask != null && !reconnectTask.isDone()) {
            reconnectTask.cancel(true); // 기존 재연결 작업 취소
        }

        reconnectTask = executor.submit(() -> {
            try {
                while (!isConnected) {
                    try {
                        Thread.sleep(5000);
                        log.info("[바이낸스] 웹소켓 재연결 시도");
                        this.reconnectBlocking();
                        if (this.isOpen() && checkNetworkConnect()) {
                            isConnected = true;
                            log.info("[바이낸스] 웹소켓 재연결 성공");
                        } else {
                            log.warn("[바이낸스] 연결은 성공하였으나, 네트워크 상태 불안정. 네트워크 연결 다시 시도");
                            isConnected = false;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // 스레드의 인터럽트 상태를 유지하며 스레드를 종료
                        log.error("[바이낸스] 웹소켓 재 연결 방해됨.", e);
                        break;
                    } catch (Exception e) {
                        log.error("[바이낸스] 웹소켓 재연결 실패", e);
                    }
                }
            } catch (Exception ex) {
                log.error("[바이낸스] 웹소켓 재연결 중 문제 발생", ex);
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
            log.error("[바이낸스] 네트워크 상태 확인 중 방해됨", e);
            Thread.currentThread().interrupt();
            return false;
        }catch(Exception e){
            log.error("[바이낸스] 네트워크 상태 확인 중 오류 발생", e);
            return false;
        }
    }
}
