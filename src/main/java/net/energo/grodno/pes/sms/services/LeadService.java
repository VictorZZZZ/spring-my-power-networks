package net.energo.grodno.pes.sms.services;

import net.energo.grodno.pes.sms.entities.Lead;
import net.energo.grodno.pes.sms.repositories.LeadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeadService {
    private final LeadRepository leadRepository;

    @Autowired
    public LeadService(LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public List<Lead> getAll() {
        return leadRepository.findAll();
    }

    public long count() {
        return leadRepository.count();
    }

    public void saveOne(Lead lead) {
        leadRepository.save(lead);
    }

    public Lead getOne(Long id) {
        return leadRepository.getOne(id);
    }

    public void deleteOne(Long id) {
        leadRepository.deleteById(id);
    }
}
