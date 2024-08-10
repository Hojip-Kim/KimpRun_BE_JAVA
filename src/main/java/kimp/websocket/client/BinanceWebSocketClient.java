package kimp.websocket.client;

import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;


@Slf4j
//@Component
public class BinanceWebSocketClient extends WebSocketClient {

    public BinanceWebSocketClient(URI serverUri){
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info(">>> Connected to Binance Websocket Successful");
    }

    @Override
    public void onMessage(String message) {
//        log.info("Received message : {}" , message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info(">>> DisConnected to Binance Websocket Successful");
    }

    @Override
    public void onError(Exception ex) {
        log.info("An error occurred: {}", ex.getMessage());
    }
}
