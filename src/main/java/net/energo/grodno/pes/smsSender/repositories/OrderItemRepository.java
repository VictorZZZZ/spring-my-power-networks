package net.energo.grodno.pes.smsSender.repositories;

import net.energo.grodno.pes.smsSender.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    /**
     * Возвращает результаты хранимой функции количество смс между датами по РЭСу
     * @param dateFrom
     * @param dateTo
     * @param resId
     * @return
     */
    @Procedure("sms_count")
    int getSmsCountForPeriodAndResId(String dateFrom,String dateTo,Integer resId);
}
