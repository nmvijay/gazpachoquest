package net.sf.gazpachoquest.repository.i18;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import net.sf.gazpachoquest.domain.i18.QuestionnaireDefinitionTranslation;
import net.sf.gazpachoquest.qbe.SearchParameters;
import net.sf.gazpachoquest.test.dbunit.support.ColumnDetectorXmlDataSetLoader;
import net.sf.gazpachoquest.types.Language;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/jpa-test-context.xml", "classpath:/datasource-test-context.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@DatabaseSetup("QuestionnaireDefinitionTranslationRepository-dataset.xml")
@DatabaseTearDown("QuestionnaireDefinitionTranslationRepository-dataset.xml")
@DbUnitConfiguration(dataSetLoader = ColumnDetectorXmlDataSetLoader.class)
public class QuestionnaireDefinitionTranslationRepositoryTest {

    @Autowired
    private QuestionnaireDefinitionTranslationRepository questionnaireDefinitionTranslationRepository;

    @Test
    public void findByExampleTest() {
        QuestionnaireDefinitionTranslation example = QuestionnaireDefinitionTranslation.with().language(Language.ES)
                .build();

        List<QuestionnaireDefinitionTranslation> translations = questionnaireDefinitionTranslationRepository
                .findByExample(example, new SearchParameters());

        assertThat(translations).contains(QuestionnaireDefinitionTranslation.with().id(8).build());
    }

}
