package com.webapp.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webapp.entities.DettPromo;
import com.webapp.entities.Promo;
import com.webapp.repository.PromoRepository;

@Service
@Transactional(readOnly = true)
public class PromoServiceImpl implements PromoService
{

	@Autowired
	private PromoRepository promoRepository;
	
	@Override
	public List<Promo> SelTutti()
	{
		return promoRepository.findAll();
	}

	@Override
	public Promo SelByIdPromo(String IdPromo)
	{
		Promo promo = promoRepository.findByIdPromo(IdPromo);

		 if (promo != null)
		 {
			 List<DettPromo> PromoRows = promo.getDettPromo()
			 .stream()
			 .sorted(Comparator.comparing(DettPromo::getRiga))
			 .collect(Collectors.toList());
 
			 promo.setDettPromo(PromoRows);
		 }

		 return promo;
	}
		
	@Override
	@Transactional
	public void InsPromo(Promo promo)
	{
		promoRepository.saveAndFlush(promo);
	}

	@Override
	@Transactional
	public void DelPromo(Promo promo)
	{
		promoRepository.delete(promo);
	}

}
