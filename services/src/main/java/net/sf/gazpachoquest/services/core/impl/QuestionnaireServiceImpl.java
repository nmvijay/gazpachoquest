/*******************************************************************************
 * Copyright (c) 2014 antoniomariasanchez at gmail.com.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     antoniomaria - initial API and implementation
 ******************************************************************************/
package net.sf.gazpachoquest.services.core.impl;

import java.util.Optional;

import net.sf.gazpachoquest.domain.core.Breadcrumb;
import net.sf.gazpachoquest.domain.core.Questionnaire;
import net.sf.gazpachoquest.domain.core.QuestionnaireAnswers;
import net.sf.gazpachoquest.domain.core.QuestionnaireDefinition;
import net.sf.gazpachoquest.qbe.SearchParameters;
import net.sf.gazpachoquest.repository.QuestionnaireDefinitionRepository;
import net.sf.gazpachoquest.repository.QuestionnaireRepository;
import net.sf.gazpachoquest.repository.dynamic.QuestionnaireAnswersRepository;
import net.sf.gazpachoquest.services.QuestionnaireService;
import net.sf.gazpachoquest.types.EntityStatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionnaireServiceImpl extends AbstractPersistenceService<Questionnaire> implements QuestionnaireService {

    @Autowired
    private QuestionnaireAnswersRepository questionnaireAnswersRepository;

    @Autowired
    private QuestionnaireDefinitionRepository questionnaireDefinitionRepository;

    @Autowired
    public QuestionnaireServiceImpl(final QuestionnaireRepository questionnaireRepository) {
        super(questionnaireRepository);
    }

    @Override
    @Transactional(readOnly = false)
    public Questionnaire save(final Questionnaire questionnaire) {
        Questionnaire existing = null;
        if (questionnaire.isNew()) {
            if (questionnaire.getStatus() == null) {
                questionnaire.setStatus(EntityStatus.DRAFT);
            } else if (questionnaire.getStatus().equals(EntityStatus.CONFIRMED)) {
                // Create answers holder
                QuestionnaireAnswers questionnaireAnswers = new QuestionnaireAnswers();
                questionnaireAnswers = questionnaireAnswersRepository.save(questionnaire.getQuestionnaireDefinition()
                        .getId(), questionnaireAnswers);
                questionnaire.setAnswersId(questionnaireAnswers.getId());
            }
            existing = repository.save(questionnaire);
        } else {
            existing = repository.findOne(questionnaire.getId());
            if (questionnaire.getStatus() != null) {
                existing.setStatus(questionnaire.getStatus());
            }
            for (Breadcrumb breadcrumb : questionnaire.getBreadcrumbs()) {
                if (!breadcrumb.isNew()) {
                    continue;
                }
                existing.getBreadcrumbs().add(breadcrumb);
                // existing.addBreadcrumb(breadcrumb);
            }
        }
        return existing;
    }

    @Override
    @Transactional(readOnly = false)
    public void removeBreadcrumb(final Integer questionnaireId, Breadcrumb breadcrumb) {
        Questionnaire existing = repository.findOne(questionnaireId);
        existing.getBreadcrumbs().remove(breadcrumb);
    }

    @Override
    @Transactional(readOnly = false)
    public QuestionnaireDefinition getDefinition(final Integer questionnaireId) {
        QuestionnaireDefinition example = new QuestionnaireDefinition();
        example.addQuestionnaire(Questionnaire.with().id(questionnaireId).build());
        Optional<QuestionnaireDefinition> definition = questionnaireDefinitionRepository.findOneByExample(example,
                new SearchParameters());
        return definition.orElseThrow(() -> new EmptyResultDataAccessException(String.format(
                "No %s entity for {} with id %s found!", QuestionnaireDefinition.class, Questionnaire.class,
                questionnaireId), 1));
    }
}
