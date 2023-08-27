package com.webapp.security;

import java.net.URI;
import java.net.URISyntaxException;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webapp.exception.NoConnexException;
import com.webapp.userconf.UserConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RemoteAuth
{
    private static final Logger logger = LoggerFactory.getLogger(RemoteAuth.class);

	@Autowired
    private UserConfig Config;
    
    public Utenti GetHttpValue(String UserId) 
		throws NoConnexException 
  	{
		URI url = null;

		try 
		{
			String SrvUrl = Config.getSrvUrl();

			url = new URI(SrvUrl + UserId);
		} 
		catch (URISyntaxException e) 
		{
			 
			e.printStackTrace();
		}

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(Config.getUserId(), Config.getPassword()));

		Utenti utente = null;

		try 
		{
			utente = restTemplate.getForObject(url, Utenti.class);	
			
		} 
		catch (Exception e) 
		{
			String ErrMsg = String.format("Failed to connect to authentication service!!");
			
			logger.warn(ErrMsg);
			
			throw new NoConnexException(ErrMsg);
		}

		return utente;
    }
}