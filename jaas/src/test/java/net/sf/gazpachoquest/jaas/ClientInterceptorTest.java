package net.sf.gazpachoquest.jaas;

import java.util.Collections;

import net.sf.gazpachoquest.api.AuthenticationResource;
import net.sf.gazpachoquest.api.QuestionnairResource;
import net.sf.gazpachoquest.cxf.interceptor.HmacAuthInterceptor;
import net.sf.gazpachoquest.dto.answers.Answer;
import net.sf.gazpachoquest.dto.answers.TextAnswer;
import net.sf.gazpachoquest.dto.auth.Account;

import org.apache.cxf.jaxrs.client.Client;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.Test;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

public class ClientInterceptorTest {
    // public static final String BASE_URI = "http://gazpachoquest.rest.antoniomaria.eu.cloudbees.net/api";
    public static final String BASE_URI = "http://localhost:8080/gazpachoquest-rest-web/api";


    @Test
    public void saveAnswerTest() {
        QuestionnairResource questionnairResource = JAXRSClientFactory.create(BASE_URI, QuestionnairResource.class,
                Collections.singletonList(new JacksonJsonProvider()), null);

        Client client = WebClient.client(questionnairResource);
        ClientConfiguration config = WebClient.getConfig(client);

        // config.getHttpConduit().setAuthSupplier(new MyHttpAuthSupplier());
        Answer answer = new TextAnswer("Antonio Maria");
        Integer questionnairId = 11;
        String questionCode = "Q1";
        String apiKey = "1234";
        String secret = "123434";
        config.getOutInterceptors().add(new HmacAuthInterceptor(apiKey, secret));
        questionnairResource.saveAnswer(answer, questionnairId, questionCode);
    }

    @Test
    public void authenticateTest() {
        AuthenticationResource authenticationResource = JAXRSClientFactory.create(BASE_URI,
                AuthenticationResource.class, Collections.singletonList(new JacksonJsonProvider()), null);
        Client client = WebClient.client(authenticationResource);
        ClientConfiguration config = WebClient.getConfig(client);
        Account account = authenticationResource.authenticate("YAS5ICHRBE");
    }
}