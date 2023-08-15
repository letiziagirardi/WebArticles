package com.webapp.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
	public List<DettPromo> SelDettPromoByCode(String Codice)
	{
		List<DettPromo> retVal = dettPromoRepository.findDettPromoActive()
		.stream()
		.filter(v -> v.getCodart().equals(Codice)) 
		.collect(Collectors.toList());

		return retVal;
	}
}
