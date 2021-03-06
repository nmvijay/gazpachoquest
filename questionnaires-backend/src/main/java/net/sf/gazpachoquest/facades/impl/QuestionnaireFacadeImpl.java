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
package net.sf.gazpachoquest.facades.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.gazpachoquest.domain.core.Question;
import net.sf.gazpachoquest.domain.core.Questionnaire;
import net.sf.gazpachoquest.domain.core.QuestionnaireDefinition;
import net.sf.gazpachoquest.domain.core.Section;
import net.sf.gazpachoquest.dto.PageMetadataDTO;
import net.sf.gazpachoquest.dto.QuestionDTO;
import net.sf.gazpachoquest.dto.QuestionnaireDefinitionDTO;
import net.sf.gazpachoquest.dto.QuestionnairePageDTO;
import net.sf.gazpachoquest.dto.SectionDTO;
import net.sf.gazpachoquest.dto.answers.Answer;
import net.sf.gazpachoquest.dto.answers.BooleanAnswer;
import net.sf.gazpachoquest.dto.answers.SimpleAnswer;
import net.sf.gazpachoquest.facades.QuestionnaireFacade;
import net.sf.gazpachoquest.questionnaire.resolver.PageResolver;
import net.sf.gazpachoquest.questionnaire.resolver.ResolverSelector;
import net.sf.gazpachoquest.questionnaire.support.AnswersPopulator;
import net.sf.gazpachoquest.questionnaire.support.PageMetadataCreator;
import net.sf.gazpachoquest.questionnaire.support.PageMetadataStructure;
import net.sf.gazpachoquest.questionnaire.support.PageStructure;
import net.sf.gazpachoquest.services.QuestionService;
import net.sf.gazpachoquest.services.QuestionnaireAnswersService;
import net.sf.gazpachoquest.services.QuestionnaireDefinitionService;
import net.sf.gazpachoquest.services.QuestionnaireService;
import net.sf.gazpachoquest.services.SectionService;
import net.sf.gazpachoquest.types.Language;
import net.sf.gazpachoquest.types.NavigationAction;
import net.sf.gazpachoquest.types.RenderingMode;
import net.sf.gazpachoquest.types.Topology;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class QuestionnaireFacadeImpl implements QuestionnaireFacade {

    private static Logger logger = LoggerFactory.getLogger(QuestionnaireFacadeImpl.class);

    @Autowired
    private ResolverSelector resolverSelector;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private SectionService sectionService;

    @Autowired
    @Qualifier("questionnaireDefinitionServiceImpl")
    private QuestionnaireDefinitionService questionnaireDefinitionService;

    @Autowired
    private QuestionnaireAnswersService questionnaireAnswersService;

    @Autowired
    private AnswersPopulator answersPopulator;

    @Autowired
    private PageMetadataCreator metadataCreator;

    @Autowired
    private Mapper mapper;

    public QuestionnaireFacadeImpl() {
        super();
    }
    
    @Transactional(readOnly = true)
    @Override
    public QuestionnaireDefinitionDTO getDefinition(Integer questionnaireId) {
        QuestionnaireDefinition definition = questionnaireService.getDefinition(questionnaireId);
        QuestionnaireDefinitionDTO definitionDTO = mapper.map(definition, QuestionnaireDefinitionDTO.class);
        
        Set<Language> translations = questionnaireDefinitionService.translationsSupported(definition.getId());
        
        for (Language language : translations) {
            definitionDTO.addSupportedLanguage(language);
        }
        definitionDTO.addSupportedLanguage(definition.getLanguage());
        
        return definitionDTO;
    }

    @Override
    public QuestionnairePageDTO resolvePage(Integer questionnaireId, RenderingMode mode, Language preferredLanguage,
            NavigationAction action) {
        Questionnaire questionnaire = questionnaireService.findOne(questionnaireId);
        if (mode == null) {
            mode = questionnaire.getQuestionnaireDefinition().getRenderingMode();
        }
        Topology topology = questionnaireDefinitionService.getTopology(questionnaire.getQuestionnaireDefinition().getId());
        PageResolver resolver = resolverSelector.selectBy(mode, topology);
        logger.info("Requesting page {} for questionnaireId = {} in language {} using renderingMode = {}",
                action.toString(), questionnaireId, preferredLanguage, mode);

        PageStructure pageStructure = resolver.resolveNextPage(questionnaire, action);
        QuestionnairePageDTO page = new QuestionnairePageDTO();
        if (pageStructure == null) { // TODO Handle exception
            return page;
        }
        List<Section> sections = pageStructure.getSections();
        List<QuestionDTO> allVisibleQuestions = new ArrayList<>();
        for (Section section : sections) {
            Section localizedSection = Section.with().build();
            if (pageStructure.isSectionInfoAvailable()) {
                localizedSection = sectionService.findOne(section.getId(), preferredLanguage);
            }

            SectionDTO sectionDTO = mapper.map(localizedSection, SectionDTO.class);
            page.addSection(sectionDTO);

            List<Integer> questionIds = section.getQuestionsId();
            List<Question> fetchedQuestions = questionService.findInList(questionIds, preferredLanguage);
            Iterator<Question> questionsIterator = section.getQuestions().iterator();
            for (Question fetchedQuestion : fetchedQuestions) {
                QuestionDTO questionDTO = mapper.map(fetchedQuestion, QuestionDTO.class);
                questionDTO.setNumber(questionsIterator.next().getNumber());
                sectionDTO.addQuestion(questionDTO);
                allVisibleQuestions.add(questionDTO);
            }
        }
        answersPopulator.populate(pageStructure.getAnswers(), allVisibleQuestions);
        PageMetadataStructure metadata = pageStructure.getMetadata();
        page.setMetadata(mapper.map(metadata, PageMetadataDTO.class));
        page.setSectionInfoAvailable(pageStructure.isSectionInfoAvailable());

        logger.info("Returning page {} of {} for questionnaireId = {}", metadata.getNumber(), metadata.getCount(),
                questionnaireId);
        return page;
    }

    @Override
    public void saveAnswer(Integer questionnaireId, String questionCode, Answer answer) {
        Questionnaire questionnaire = Questionnaire.with().id(questionnaireId).build();
        if (!(answer instanceof SimpleAnswer)) {
            logger.warn("Answer {} not supported", answer);
            return;
        }
        String sufix = "";
        if (answer instanceof BooleanAnswer) {
            sufix = "_" + ((BooleanAnswer) answer).getOption();
        }
        questionnaireAnswersService.save(questionnaire, questionCode + sufix, answer.getValue());
    }
}
