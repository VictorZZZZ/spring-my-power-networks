package net.energo.grodno.pes.sms.repositories;

import net.energo.grodno.pes.sms.entities.Order;
import net.energo.grodno.pes.sms.entities.users.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    long count();

    @Query("SELECT o FROM Order o WHERE o.user = :userId ORDER BY o.created DESC")
    Page<Order> findPageByUserId(@Param("userId") User user, Pageable pageable);

    Page<Order> findAllByOrderByCreatedDesc(Pageable pageable);


}
