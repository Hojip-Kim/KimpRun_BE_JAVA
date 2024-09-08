package kimp.websocket.config;

import kimp.market.handler.BinanceWebsocketHandler;
import kimp.market.handler.UpbitWebSocketHandler;
import kimp.chat.handler.ChatWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {

    private final UpbitWebSocketHandler upbitWebSocketHandler;
    private final BinanceWebsocketHandler binanceWebsocketHandler;

    private final ChatWebSocketHandler chatWebSocketHandler;

    public WebsocketConfig(UpbitWebSocketHandler upbitWebSocketHandler, BinanceWebsocketHandler binanceWebsocketHandler, ChatWebSocketHandler chatWebSocketHandler) {
        this.upbitWebSocketHandler = upbitWebSocketHandler;
        this.binanceWebsocketHandler = binanceWebsocketHandler;
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(upbitWebSocketHandler, "/upbit")
                .setAllowedOrigins("*");
        registry
                .addHandler(binanceWebsocketHandler, "/binance")
                .setAllowedOrigins("*");
        registry
                .addHandler(chatWebSocketHandler, "/chatService")
                .setAllowedOrigins("*");


    }
}
