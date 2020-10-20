package net.energo.grodno.pes.smsSender.repositories;

import net.energo.grodno.pes.smsSender.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    /**
     * Возвращает результаты хранимой функции из базы данных.
     * Функция вычисляет количество отправленных СМС определенным РЭСом за период времени c dateFrom по dateTo
     * @param dateFrom
     * @param dateTo
     * @param resId
     * @return
     */
    @Query(value = "SELECT sms_count(:date_from,:date_to,:resId)", nativeQuery = true)
    int getSmsCountForPeriodAndResId(@Param("date_from") Timestamp dateFrom, @Param("date_to") Timestamp dateTo, @Param("resId") Integer resId);

}
