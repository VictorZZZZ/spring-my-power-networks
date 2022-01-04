package net.energo.grodno.pes.sms.utils.statistics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.energo.grodno.pes.sms.entities.Res;
import net.energo.grodno.pes.sms.Services.ResService;
import org.springframework.beans.factory.annotation.Autowired;

@NoArgsConstructor
@Getter
@Setter
public class ResStatistics {
    @Autowired
    ResService resService;

    public Res res;

    public Integer previousMonthSmsCount;

    public Integer thisMonthSmsCount;

    public double spentMoneyThisMonth;

    public Long abonentsCount;

    public Long getAbonentsCount() {
        return res.getCachedAbonentsCount();
    }
}
