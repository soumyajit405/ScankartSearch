package com.scankart.app.model;

import org.springframework.data.jpa.repository.Modifying;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AllOrderRepository extends JpaRepository<AllOrder, Long>
{
 	
	@Query("select COALESCE(max(id),0) from AllOrder ")
	int findOrderCount();
	
	@Query("select a from AllOrder a, AllInstoreOrderStatus b  where a.allMerchant.id=?1 and b.orderStatusPl.name=?2 and b.allOrder.id=a.id")
	List<AllOrder> getOrderByMerchant(int merchantId, String status);
	
	@Query("select count(a) from AllOrder a where a.allMerchant.id=?1 and a.status=?2")
	int getOrderCount(int merchantId, String status);
	
	@Query("select count(a) from AllOrder a where a.allUser.id=?1 and a.status=?2")
	int getOrderCountForUser(int merchantId, String status);
	
	@Query(value="select * from all_orders a  where a.all_merchant_id=?1 and a.status=?2 order by purchase_date desc limit ?3,?4", nativeQuery=true)
	List<AllOrder> getOrderByMerchant(int merchantId, String status, int startCount, int endCount);
	
	@Query(value="select * from all_orders a  where a.all_users_id=?1 and a.status=?2 order by purchase_date desc limit ?3,?4", nativeQuery=true)
	List<AllOrder> getOrderByUser(int merchantId, String status, int startCount, int endCount);
	
	@Query("select distinct a from AllOrder a, AllUser b , AllOrderAttr c , AllMerchant d where  a.id=c.allOrder.id and a.allUser.id = b.id  and a.allMerchant.id = d.id  and (b.name=?1 or c.value=?1 or b.phone=?1 or a.id=?1) and d.id=?2 and a.status=?3")
	List<AllOrder> searchOrderByMerchant (String value, int merchnatId,String status);
	
	@Query("select a from AllOrder a where a.id =?1")
	AllOrder getOrderDetails(int orderId);
	
	@Query(value="select b.* from all_orders a,all_instore_order_status  b where a.id =b.all_instore_order_id and a.id =?1 order by b.date desc limit 1", nativeQuery=true)
	AllInstoreOrderStatus getOrderStatus(int orderId);
	
	@Query("select a from OrderTypePl a where a.name =?1")
	OrderTypePl getOrderType(String type);
	
	@Query("select a from AdminConfigurations a where a.name  in ?1")
	List<AdminConfigurations> getConfigurations(List<String> configurations);
	
	@Modifying
	@Query("update  AllOrder a set a.status =?1 where a.id =?2")
	void updateOrderStatus(String status, int orderId);
}