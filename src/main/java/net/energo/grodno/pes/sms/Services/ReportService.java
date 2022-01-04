package net.energo.grodno.pes.sms.Services;

import net.energo.grodno.pes.sms.repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class ReportService {
    private OrderItemRepository orderItemRepository;

    @Autowired
    public ReportService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public int getSmsCountForPeriodAndResId(Timestamp date_from, Timestamp date_to, Integer resId) {
        return orderItemRepository.getSmsCountForPeriodAndResId(date_from, date_to, resId);
    }
}
