package net.sf.gazpachosurvey.services.impl;

import net.sf.gazpachosurvey.domain.support.AbstractInvitation;
import net.sf.gazpachosurvey.dto.InvitationDTO;
import net.sf.gazpachosurvey.repository.InvitationRepository;
import net.sf.gazpachosurvey.services.InvitationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvitationServiceImpl extends
        AbstractPersistenceService<AbstractInvitation, InvitationDTO> implements
        InvitationService {

    @Autowired
    protected InvitationServiceImpl(InvitationRepository repository) {
        super(repository, AbstractInvitation.class, InvitationDTO.class);
    }

}
