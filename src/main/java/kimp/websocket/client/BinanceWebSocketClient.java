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

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> pingTask;
    private ScheduledFuture<?> reconnectTask;

    private static final int RECONNECT_DELAY_SECONDS = 5;
    private static final int PING_INTERVAL_SECONDS = 60; // 60초마다 Ping 전송

    private final Object reconnectLock = new Object();
    private boolean isReconnecting = false;

    private volatile boolean isConnected = false;

    public BinanceWebSocketClient(String serverUri, BinanceWebsocketHandler binanceWebsocketHandler) throws URISyntaxException {
        super(new URI(serverUri));
        this.binanceWebsocketHandler = binanceWebsocketHandler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        isConnected = true;
        log.info(">>> [바이낸스] 웹소켓 연결 성공");
        schedulePing();
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
            log.error("Failed to parse message: {}", message, e);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        isConnected = false;
        log.warn(">>> [바이낸스] 웹소켓 연결 종료. 코드: {}, 원인: {}", code, reason);
        cancelPing();
        attemptReConnect();
    }

    @Override
    public void onError(Exception ex) {
        isConnected = false;
        log.error("[바이낸스] 웹소켓 에러 발생: {}", ex.getMessage(), ex);
        // 에러 발생 시 연결 상태 변경 및 재연결 시도
        if (!this.isOpen()) {
            attemptReConnect();
        }
    }

    @Override
    public void onWebsocketPong(WebSocket conn, org.java_websocket.framing.Framedata f) {
        log.info("[바이낸스] 웹소켓 Pong메시지 수신완료");
    }

    private void schedulePing() {
        pingTask = scheduler.scheduleAtFixedRate(() -> {
            if (this.isOpen()) {
                try {
                    log.debug("[바이낸스] ping 메시지 송신 성공");
                    this.sendPing();
                } catch (Exception e) {
                    log.error("[바이낸스] ping 메시지 송신 실패", e);
                }
            }
        }, PING_INTERVAL_SECONDS, PING_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private void cancelPing() {
        if (pingTask != null && !pingTask.isCancelled()) {
            pingTask.cancel(true);
        }
    }

    private void attemptReConnect() {
        synchronized (reconnectLock) {
            if (isReconnecting) {
                log.info("[바이낸스] 이미 재 연결 시도중.");
                return;
            }
            isReconnecting = true;
        }

        reconnectTask = scheduler.schedule(() -> {
            log.info("[바이낸스] 웹소켓 재연결중...");
            try {
                this.reconnectBlocking();
                if (this.isOpen()) {
                    isConnected = true;
                    log.info("[바이낸스] 웹소켓 재연결 성공");
                    schedulePing();
                } else {
                    log.warn("[바이낸스] 외부 요인(네트워크)으로 인한 재연결 실패. 재 연결 재시도...");
                    // 재연결 실패 시 재시도
                    scheduler.schedule(this::attemptReConnect, RECONNECT_DELAY_SECONDS, TimeUnit.SECONDS);
                }
            } catch (InterruptedException e) {
                log.error("[바이낸스] 웹소켓 재연결 방해됨", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("[바이낸스] 웹소켓 재연결 Exception", e);
                // 재연결 실패 시 재시도
                scheduler.schedule(this::attemptReConnect, RECONNECT_DELAY_SECONDS, TimeUnit.SECONDS);
            } finally {
                synchronized (reconnectLock) {
                    isReconnecting = false;
                }
            }
        }, RECONNECT_DELAY_SECONDS, TimeUnit.SECONDS);
    }

}
