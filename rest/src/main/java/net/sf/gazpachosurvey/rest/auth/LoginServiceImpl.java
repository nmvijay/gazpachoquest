package net.sf.gazpachosurvey.rest.auth;

public class LoginServiceImpl {

    private static String RESPONDENT_USERNAME = "respondent";

    public void doLogin(String userName, String password) {
        if (RESPONDENT_USERNAME.equals(userName)) {
            //doRespondentLogin(userName, password);
        }
    }
}
