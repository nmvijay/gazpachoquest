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
package net.sf.gazpachoquest.services;

import java.util.List;

import net.sf.gazpachoquest.domain.core.QuestionnairDefinition;
import net.sf.gazpachoquest.domain.core.embeddables.QuestionnairDefinitionLanguageSettings;
import net.sf.gazpachoquest.domain.i18.QuestionnairDefinitionTranslation;

public interface QuestionnairDefinitionService
        extends
        LocalizedPersistenceService<QuestionnairDefinition, QuestionnairDefinitionTranslation, QuestionnairDefinitionLanguageSettings> {

    QuestionnairDefinition confirm(QuestionnairDefinition questionnairDefinition);

    int questionGroupsCount(Integer questionnairDefinitionId);

    Integer questionsCount(Integer questionnairDefinitionId);

    /**
     * Returns questions count group by question group for a given
     * questionnairDefinitionId
     * 
     * <pre>
     * [11, 2, 0] = [questionGroupId, questionCount, order_in_questionnair]
     * [10, 3, 1]
     * [9, 2, 2]
     * </pre>
     * 
     * @param questionnairDefinitionId
     * @return
     */
    List<Object[]> questionsCountGroupByQuestionGroups(Integer questionnairDefinitionId);

}
