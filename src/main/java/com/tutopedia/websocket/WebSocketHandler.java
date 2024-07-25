package com.tutopedia.websocket;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.tutopedia.model.WsData;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebSocketHandler extends TextWebSocketHandler {
	private int nrOfSeconds;
	
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WebSocketHandler(int seconds) {
    	this.nrOfSeconds = seconds;
	}

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        for(;;) {
            WsData data = new WsData("ping");
            try {
                TextMessage message = new TextMessage(objectMapper.writeValueAsString(data));
                session.sendMessage(message);
                Thread.sleep(nrOfSeconds * 1000);
            } catch (IOException | IllegalStateException | InterruptedException e) {
            }
        }
    }
    
    
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
    }
}
