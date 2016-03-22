package org.jbpm.designer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
@Portable
public class TimerEvent extends StandardEvent {

    public static final String DATE = "date";
    public static final String DURATION = "duration";
    public static final String CYCLE = "cycle";

    private String timerType;

    private String timerExpression;

    public String getTimerType() {
        return timerType;
    }

    public void setTimerType(String timerType) {
        this.timerType = timerType;
    }

    public String getTimerExpression() {
        return timerExpression;
    }

    public void setTimerExpression(String timerExpression) {
        this.timerExpression = timerExpression;
    }
}
