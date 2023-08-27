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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/prezzo")
public class PrezziPromoController
{
	private static final Logger logger = LoggerFactory.getLogger(PrezziPromoController.class);

	@Autowired
	private DettPromoService dettPromoService;

	/*
	 * The getPricePromoCodArt method handles the retrieval of the minimum promotional price for a specific article. 
	 * It interacts with a service to fetch the promotional details, filters and calculates 
	 * the minimum promotional price. 
	 * If no promotional prices are found, it returns null.
	 */
	@ApiOperation(
		value = "Get the minimum promotional price for Article with code CodArt",
		response = OptionalDouble.class,
		produces = "application/json")
	@ApiResponses(value =
	{ @ApiResponse(code = 200, message = "Best Price found"),
		@ApiResponse(code = 404, message = "Best Price not found")})
	
	@RequestMapping(value = "/promo/codice/{codart}", method = RequestMethod.GET)
	public OptionalDouble getPricePromoCodArt(@PathVariable("codart") String CodArt)  
	{
		OptionalDouble Prezzo = null;
		
		List<DettPromo> dettPromo = dettPromoService.SelDettPromoByCode(CodArt);
		logger.info("Promo List: " + dettPromo);

		
		if (dettPromo != null)
		{
			Prezzo = dettPromo.stream()
					.filter(v -> v.getCodfid() == null)
					.mapToDouble(v -> Double.parseDouble(v.getOggetto().replace(",", "."))).min();
			
			logger.info("Price Article given the Promo : " + Prezzo);
		
		}
		
		return Prezzo;	
	}
}
