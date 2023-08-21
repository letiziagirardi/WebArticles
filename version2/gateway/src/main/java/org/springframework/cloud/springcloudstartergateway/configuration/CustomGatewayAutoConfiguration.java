package org.springframework.cloud.springcloudstartergateway.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.config.conditional.ConditionalOnEnabledGlobalFilter;
import org.springframework.cloud.gateway.filter.WebsocketRoutingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;

@Configuration
@Import(GatewayAutoConfiguration.class)
public class CustomGatewayAutoConfiguration extends GatewayAutoConfiguration {
	
	@Value("${maxframepayloadlenght}")
	private int maxFramePayloadLenght;
	
    public CustomGatewayAutoConfiguration() {
    }

//    @Override
//    @Bean
//    public WebSocketService webSocketService() {
//        ReactorNettyRequestUpgradeStrategy reactorNettyRequestUpgradeStrategy = new ReactorNettyRequestUpgradeStrategy();
//        reactorNettyRequestUpgradeStrategy.setMaxFramePayloadLength(maxFramePayloadLenght);
//        HandshakeWebSocketService handshakeWebSocketService = new HandshakeWebSocketService(reactorNettyRequestUpgradeStrategy);
//		return handshakeWebSocketService;
//    }
    
	@Bean
	@ConditionalOnEnabledGlobalFilter(WebsocketRoutingFilter.class)
	public WebSocketService webSocketService(RequestUpgradeStrategy requestUpgradeStrategy) {
		if(requestUpgradeStrategy != null && requestUpgradeStrategy instanceof ReactorNettyRequestUpgradeStrategy ) {
			((ReactorNettyRequestUpgradeStrategy)requestUpgradeStrategy).setMaxFramePayloadLength(maxFramePayloadLenght);
		}
		
		return new HandshakeWebSocketService(requestUpgradeStrategy);
	}

    
    
}
