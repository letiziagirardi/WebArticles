package com.webapp.service;

import java.util.List;

import com.webapp.entities.Promo;
 
public interface PromoService
{
	public List<Promo> SelTutti();

	public Promo SelByIdPromo(String IdPromo);

	public void InsPromo(Promo promo);

	public void DelPromo(Promo promo);

}
