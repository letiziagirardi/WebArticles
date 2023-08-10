package com.webapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.entities.DettPromo;
import com.webapp.repository.DettPromoRepository;
 
@Service
@Transactional(readOnly = true)
public class DettPromoServiceImpl implements DettPromoService
{

	@Autowired
	private DettPromoRepository dettPromoRepository;
		
	@Override
	@Cacheable(value = "przpromo_cache",sync = true)
	public List<DettPromo> SelDettPromoByCodFid(String CodFid)
	{
		List<DettPromo> retVal = dettPromoRepository.findDettPromoActive()
		.stream()
		.filter(v -> v.getCodfid() != null)
		.filter(v -> v.getCodfid().equals(CodFid)) 
		.collect(Collectors.toList());

		return retVal;
	}
	
	@Override
	@Cacheable(value = "przpromo_cache",sync = true)
	public List<DettPromo> SelDettPromoByCode(String Codice)
	{
		List<DettPromo> retVal = dettPromoRepository.findDettPromoActive()
		.stream()
		.filter(v -> v.getCodart().equals(Codice)) 
		.collect(Collectors.toList());

		return retVal;
	}
}
