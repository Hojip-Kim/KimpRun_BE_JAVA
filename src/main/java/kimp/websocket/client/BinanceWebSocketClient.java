package kimp.websocket.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.handler.BinanceWebsocketHandler;
import kimp.websocket.dto.response.BinanceReceiveDto;
import kimp.websocket.dto.response.BinanceStreamDto;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.*;

@Slf4j
public class BinanceWebSocketClient extends WebSocketClient {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BinanceWebsocketHandler binanceWebsocketHandler;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Future<?> reconnectTask;

    private static final int RECONNECT_DELAY_SECONDS = 5;

    private final Object reconnectLock = new Object();
    private volatile boolean isReconnecting = false;

    private volatile boolean isConnected = false;

    public BinanceWebSocketClient(String serverUri, BinanceWebsocketHandler binanceWebsocketHandler) throws URISyntaxException {
        super(new URI(serverUri));
        this.binanceWebsocketHandler = binanceWebsocketHandler;
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
            BinanceStreamDto binanceDto = new BinanceStreamDto(
                    binanceReceiveDto.getToken().replace("USDT", ""),
                    binanceReceiveDto.getPrice()
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

    private boolean checkNetworkConnect() {
        try {
            this.sendPing();
            Thread.sleep(3000); // 3초 대기
            return this.isOpen();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("[바이낸스] 네트워크 상태 확인 중 인터럽트 발생", e);
            return false;
        } catch (Exception e) {
            log.error("[바이낸스] 네트워크 상태 확인 중 오류 발생", e);
            return false;
        }
    }

    public void attemptReConnect() {
        synchronized (reconnectLock) {
            if (isReconnecting) {
                // 이미 재연결 중이면 추가 작업하지 않음
                return;
            }
            isReconnecting = true;
        }

        if (reconnectTask != null && !reconnectTask.isDone()) {
            reconnectTask.cancel(true); // 기존 재연결 작업 취소
        }

        reconnectTask = executor.submit(() -> {
            try {
                while (!isConnected) {
                    try {
                        Thread.sleep(RECONNECT_DELAY_SECONDS * 1000L);
                        log.info("[바이낸스] 웹소켓 재연결 시도");

                        // 이전 연결이 열려 있다면 닫기
                        if (this.isOpen()) {
                            this.closeBlocking();
                        }

                        // 재연결 시도
                        this.reconnectBlocking();

                        if (this.isOpen() && checkNetworkConnect()) {
                            isConnected = true;
                            log.info("[바이낸스] 웹소켓 재연결 성공");
                        } else {
                            log.warn("[바이낸스] 재연결 성공했지만 네트워크 상태 불안정. 재시도 예정");
                            isConnected = false;
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.error("[바이낸스] 웹소켓 재연결 중단됨", e);
                        break;
                    } catch (Exception e) {
                        log.error("[바이낸스] 웹소켓 재연결 실패", e);
                    }
                }
            } finally {
                synchronized (reconnectLock) {
                    isReconnecting = false;
                }
            }
        });
    }
}
