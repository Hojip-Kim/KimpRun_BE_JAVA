package kimp.websocket.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.websocket.dto.response.BinanceReceiveDto;
import kimp.websocket.dto.response.BinanceStreamDto;
import kimp.market.handler.BinanceWebsocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;


@Slf4j
//@Component
public class BinanceWebSocketClient extends WebSocketClient {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BinanceWebsocketHandler binanceWebsocketHandler;

    public BinanceWebSocketClient(String serverUri, BinanceWebsocketHandler binanceWebsocketHandler) throws URISyntaxException {
        super(new URI(serverUri));
        this.binanceWebsocketHandler = binanceWebsocketHandler;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info(">>> Connected to Binance Websocket Successful");
    }

    @Override
    public void onMessage(String message) {
        try {
            BinanceReceiveDto binanceReceiveDto = objectMapper.readValue(message, BinanceReceiveDto.class);
            BinanceStreamDto binanceDto = new BinanceStreamDto(binanceReceiveDto.getToken().replace("USDT", ""), binanceReceiveDto.getPrice());
            binanceWebsocketHandler.inputDataToHashMap(binanceDto);
        }catch(Exception e){
            e.printStackTrace();
        }
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
