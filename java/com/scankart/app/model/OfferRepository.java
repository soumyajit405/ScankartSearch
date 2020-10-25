package com.scankart.app.model;

import org.springframework.data.jpa.repository.Modifying;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long>
{
 	
	@Query("select a from UserOffer a where a.offer.id=?1 ")
	UserOffer getUserOffer(int offerId);
	
}