package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.sub_entity.OrderStatus;
import cody.ecommerce.cody_app.entity.sub_entity_id.OrderStatusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, OrderStatusId> {
    Optional<OrderStatus> findFirstByOrderIdOrderByModifiedAtDesc(String orderId);
}
