package net.sf.gazpachosurvey.dto;

import java.io.Serializable;

public interface Identifiable extends Serializable {

    Integer getId();

    boolean isNew();

}
