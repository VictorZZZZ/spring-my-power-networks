package net.energo.grodno.pes.sms.repositories;

import net.energo.grodno.pes.sms.entities.TextTemplate;
import net.energo.grodno.pes.sms.entities.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateRepository extends JpaRepository<TextTemplate,Long> {

    @Query("SELECT t FROM TextTemplate t WHERE t.user = :user ORDER BY t.created DESC")
    List<TextTemplate> findByUser(@Param("user") User user);
}
