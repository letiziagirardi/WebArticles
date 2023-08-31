package org.springframework.cloud.springcloudstartergateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;

@Configuration
public  class CustomReactorNettyWebSocketClient   {
	
	@Value("${maxframepayloadlenght}")
	private int maxFramePayloadLenght;
	

	@Primary
    @Bean
    public ReactorNettyWebSocketClient webSocketServiceCleint() {
    	ReactorNettyWebSocketClient reactorNettyRequestUpgradeStrategy = new ReactorNettyWebSocketClient();
    	reactorNettyRequestUpgradeStrategy.setMaxFramePayloadLength(maxFramePayloadLenght);
    	return reactorNettyRequestUpgradeStrategy;
    }
 
	
}
