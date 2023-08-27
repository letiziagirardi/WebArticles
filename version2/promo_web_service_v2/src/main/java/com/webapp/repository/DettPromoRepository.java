package com.webapp.repository;

import java.util.List;
import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
 

import com.webapp.entities.DettPromo;
 
public interface DettPromoRepository extends JpaRepository<DettPromo, Long>
{

	List<DettPromo> findByCodartAndInizioLessThanEqualAndFineGreaterThanEqual(String codart, Date inizio, Date fine);

}
