package com.scankart.app.model;

import org.springframework.data.jpa.repository.Modifying;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AllOrderItemsRepository extends JpaRepository<AllInstoreOrderItem, Long>
{
 
	@Query("select a from AllInventory a where a.id=?1")
	AllInventory getInvName(int merchantInvId);
	
	@Query("select COALESCE(max(id),0) from AllInstoreOrderItem a")
	int getCountOfOrderItems();
	
	@Modifying
	@Query("delete from AllInstoreOrderItem a where a.allOrder.id =?1 ")
	void deleteOrderItems(int orderId);
}