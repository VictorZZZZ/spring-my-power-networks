package net.energo.grodno.pes.smsSender.utils.statistics;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.energo.grodno.pes.smsSender.Services.ResService;
import net.energo.grodno.pes.smsSender.entities.Res;
import org.springframework.beans.factory.annotation.Autowired;

@NoArgsConstructor
@Getter
@Setter
public class ResStatistics {
    @Autowired
    ResService resService;

    public Res res;

    public int previousMonthSmsCount;

    public int thisMonthSmsCount;

    public double spentMoneyThisMonth;

    public Long abonentsCount;

    public Long getAbonentsCount() {
        return res.getCachedAbonentsCount();
    }
}
