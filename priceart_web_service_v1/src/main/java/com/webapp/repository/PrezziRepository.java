package com.webapp.repository;

 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webapp.entities.DettListini;

public interface PrezziRepository extends JpaRepository<DettListini, Long>
{
	public DettListini findByCodArtAndIdList(String CodArt, String IdList); 

	@Modifying
	@Query(value = "DELETE FROM dettlistini WHERE CodArt = :codart AND IdList = :idlist", nativeQuery = true)
	void DelRowDettList(@Param("codart") String CodArt, @Param("idlist") String IdList);

}
