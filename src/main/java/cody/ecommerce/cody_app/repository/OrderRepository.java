package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findAll(Specification<Order> spec, Pageable pageable);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.orderStatuses os " +
            "LEFT JOIN FETCH o.buyer " +
            "LEFT JOIN FETCH o.seller " +
            "LEFT JOIN FETCH o.orderItems oi " +
            "LEFT JOIN FETCH oi.product " +
            "WHERE o.id IN :orderIds " +
            "ORDER BY o.createdAt DESC")
    List<Order> findOrdersWithAllDataByIds(@Param("orderIds") List<String> orderIds);

    Page<Order> findByBuyerId(String buyerId, Pageable pageable);
}
