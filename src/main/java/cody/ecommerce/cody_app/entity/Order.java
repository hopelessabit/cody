package cody.ecommerce.cody_app.entity;

import cody.ecommerce.cody_app.constant.OrderMainStatusEnum;
import cody.ecommerce.cody_app.dto.request.order.CreateOrderRequest;
import cody.ecommerce.cody_app.entity.sub_entity.OrderItem;
import cody.ecommerce.cody_app.entity.sub_entity.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {
    @Column(name = "seller_id", nullable = true, length = 50)
    private String sellerId;

    @Column(name = "buyer_id", nullable = false, length = 50)
    private String buyerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", referencedColumnName = "id", nullable = true, insertable = false, updatable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private User buyer;

    @Column(name = "total_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "buyer_rating")
    private Integer buyerRating;

    @Column(name = "buyer_name", length = 100)
    private String buyerName;

    @Column(name = "buyer_phone", length = 20)
    private String buyerPhone;

    @Column(name = "address_url", length = 500)
    @Nationalized
    private String addressUrl;

    @Column(name = "decline_reason", length = 500)
    private String declineReason;

    @Column(name= "main_status", length = 20)
    @Enumerated(EnumType.STRING)
    private OrderMainStatusEnum mainStatus;

    @Column(name = "note", length = 1000)
    private String note;

    //TODO: change nullalbe to True
    @Column(name = "isCombo", nullable = false, columnDefinition = "BIT DEFAULT 0")
    private Boolean isCombo;

    @Column(name = "custom_combo_name", length = 255)
    private String customComboName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OrderStatus> orderStatuses;

    public static Order from(CreateOrderRequest request) {
        Order order = new Order();
        order.setMainStatus(OrderMainStatusEnum.PS);
        order.setTotalPrice(BigDecimal.ZERO); // This should be calculated based on items
        order.setBuyerRating(null); // Rating is not set at creation
        order.setBuyerName(request.getBuyerName());
        order.setBuyerPhone(request.getBuyerPhone());
        order.setAddressUrl(request.getAddressUrl());
        order.setNote(request.getNote());
        order.setIsCombo(request.getIsCombo());
        order.setCustomComboName(request.getCustomComboName());
        return order;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Order order = (Order) o;
        return getId() != null && Objects.equals(getId(), order.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}