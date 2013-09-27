package net.sf.gazpachosurvey.services.impl;

import net.sf.gazpachosurvey.domain.support.Persistable;
import net.sf.gazpachosurvey.dto.Identifiable;
import net.sf.gazpachosurvey.repository.support.LocalizedRepository;
import net.sf.gazpachosurvey.services.LocalizedPersistenceService;
import net.sf.gazpachosurvey.types.Language;

public abstract class AbstractLocalizedPersistenceService<T extends Persistable, D extends Identifiable> extends
        AbstractPersistenceService<T, D> implements LocalizedPersistenceService<D> {

    protected LocalizedRepository<T> localizedRepository;

    protected AbstractLocalizedPersistenceService(LocalizedRepository<T> repository, Class<T> entityClazz,
            Class<D> dtoClazz) {
        super(repository, entityClazz, dtoClazz);
        localizedRepository = repository;
    }

    @Override
    public D findOne(Integer id, Language language) {
        T entity = localizedRepository.findOne(id, language);
        D dto = null;
        if (entity != null) {
            dto = mapper.map(entity, dtoClazz);
        }
        return dto;
    }

}
