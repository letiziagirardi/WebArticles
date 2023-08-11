package com.webapp.controller;

import java.util.List;
import java.util.OptionalDouble;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.entities.DettPromo;
 
import com.webapp.service.DettPromoService;

@RestController
@RequestMapping("/prezzo")
public class PrezziPromoController
{
	private static final Logger logger = LoggerFactory.getLogger(PrezziPromoController.class);

	@Autowired
	private DettPromoService dettPromoService;

	@RequestMapping(value = "/promo/fidelity/{codfid}/{codart}", method = RequestMethod.GET)
	public OptionalDouble getPricePromoFid(@PathVariable("codfid") String CodFid, 
		@PathVariable("codart") String CodArt)  
	{
		OptionalDouble Prezzo = null;
		
		List<DettPromo> dettPromo = dettPromoService.SelDettPromoByCodFid(CodFid);
		
		if (dettPromo != null)
		{
			Prezzo = dettPromo.stream()
					.filter(v -> v.getCodart().equals(CodArt))
					.mapToDouble(v -> Double.parseDouble(v.getOggetto().replace(",", "."))).min();
			
			logger.info("Price Promo Fidelity: " + Prezzo);
		
		}
		
		return Prezzo;	
	}

	/*
	 * The 'getPricePromoCodArt' method calculates and retrieves the minimum promotional price for a specific product 
	 * based on its code. It does this by fetching detailed promotional information, filtering the relevant data, 
	 * and calculating the minimum price. 
	 */

	@RequestMapping(value = "/promo/codice/{codart}", method = RequestMethod.GET)
	public OptionalDouble getPricePromoCodArt(@PathVariable("codart") String CodArt)  
	{
		OptionalDouble Prezzo = null;
		
		List<DettPromo> dettPromo = dettPromoService.SelDettPromoByCode(CodArt);

		logger.info("DettPromo: ", dettPromo);
		
		if (dettPromo != null)
		{
			Prezzo = dettPromo.stream()
					.filter(v -> v.getCodfid() == null)
					.mapToDouble(v -> Double.parseDouble(v.getOggetto().replace(",", "."))).min();
			
			logger.info("Promo Price Article: " + Prezzo);
		
		}
		
		return Prezzo;	
	}
}
