package kimp.websocket.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import kimp.market.service.UpbitService;
import kimp.websocket.dto.response.SimpleUpbitDto;
import kimp.websocket.dto.response.UpbitReceiveDto;
import kimp.websocket.TicketMessage;
import kimp.websocket.TradeSubscribe;
import kimp.websocket.handler.WebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.math.BigDecimal;
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
    private final UpbitService upbitService;

    public UpbitWebsocketClient(String serverUri, WebSocketHandler webSocketHandler, UpbitService upbitService) throws URISyntaxException {
        super(new URI(serverUri));
        this.webSocketHandler = webSocketHandler;
        this.upbitService = upbitService;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info(">>> Connected to Upbit Websocket Successful");



        try {
            List<String> codes = upbitService.getUpbitMarketData().getMarketList();
            TicketMessage ticketMessage = new TicketMessage("test");
            TradeSubscribe tradeSubscribe = new TradeSubscribe("ticker", codes, false, true);

            String message = objectMapper.writeValueAsString(Arrays.asList(ticketMessage, tradeSubscribe));
            this.send(message);

        } catch (Exception e) {
            log.error("Failed to send subscribe message", e);
        }
    }

    @Override
    public void onMessage(ByteBuffer message) {
        String receivedString = StandardCharsets.UTF_8.decode(message).toString();
        try {
            UpbitReceiveDto upbitReceiveDto = objectMapper.readValue(receivedString, UpbitReceiveDto.class);


            BigDecimal rate = upbitReceiveDto.getChange().equals("FALL") ?
                    upbitReceiveDto.getChangeRate().negate() :
                    upbitReceiveDto.getChangeRate();


            SimpleUpbitDto simpleUpbitDto = new SimpleUpbitDto(upbitReceiveDto.getCode(), upbitReceiveDto.getTradeVolume(), rate.multiply(new BigDecimal("100")), upbitReceiveDto.getHighest52WeekPrice(), upbitReceiveDto.getLowest52WeekPrice(), upbitReceiveDto.getOpeningPrice(), upbitReceiveDto.getTradePrice(), upbitReceiveDto.getChange());
            String dtoJson = objectMapper.writeValueAsString(simpleUpbitDto);
            webSocketHandler.sendMessageToAll(dtoJson);

        } catch (Exception e) {
            log.error("Failed to convert message to Dto", e);
        }
    }

    @Override
    public void onMessage(String message) {
//
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.info(">>> DisConnected to Upbit Websocket Successful");
    }

    @Override
    public void onError(Exception ex) {
        log.error("An error occurred: " + ex.getMessage(), ex);
    }


}