package com.scankart.app.model;

import org.springframework.data.jpa.repository.Modifying;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AllMerchantRepository extends JpaRepository<AllMerchant, Long>
{
 
	@Query(value = "SELECT all_merchant.* FROM all_merchant, merchant_business_types where merchant_business_types.merchant_id= all_merchant.id " + 
			"and merchant_business_types.business_type_id =?1 limit ?2,?3", nativeQuery = true)
	List<AllMerchant> getAllMerchantsByType(int businessType,int startCount, int endCount);
	
	@Query(value  = "select * from all_merchant  limit ?1,?2", nativeQuery = true)
	List<AllMerchant> getAllMerchants(int startCount, int endCount);
	
	@Query(value  = "select * from all_merchant where upper(name) like %:name% limit :startCount,:endCount", nativeQuery = true)
	List<AllMerchant> getAllMerchantsByName(@Param("startCount") int startCount,@Param("endCount") int endCount,@Param("name") String name);
	
	@Query("select count(a.id) from AllMerchant a,MerchantBusinessType b  where a.id = b.allMerchant.id and b.businessType.id=?1")
	int getAllMerchantsByTypeCount(String businessType);
	
	@Query("select count(a.id) from AllMerchant a")
	int getAllMerchantCount();
	
	@Query("select count(a.id) from AllMerchant a where upper(a.name) like %?1%")
	int getAllMerchantCountByName(String name);
	
	@Query("select a from AllMerchant a where a.id=?1")
	AllMerchant getMerchantById(int merchantId);
	
	@Query("select a from BusinessTypePl a where a.name=?1")
	BusinessTypePl getBusinessType(String type);
	
	@Query("select a.playerId from AllMerchantPlayer a where allMerchant.id=?1")
	ArrayList<String> getAllPlayersForMErchant(int merchantId);
	
}