package com.webapp.service;

import java.util.List;

import com.webapp.entities.DettPromo;
 
public interface DettPromoService
{	
	
	List<DettPromo> SelDettPromoByCode(String Codice);
	
}
