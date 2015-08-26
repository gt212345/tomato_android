package org.itri.tomato;

/**
 * Created by heiruwu on 7/31/15.
 */
public class AutoRunItem {
    String agentId;
    String display;
    String option;
    String conditionType;
    String condition;
    String agentParameter;
    String value;
    int hasValue;


    public AutoRunItem(String agentId, String display, String option, String conditionType, String condition, String agentParameter, String value, int hasValue) {
        this.agentId = agentId;
        this.display = display;
        this.option = option;
        this.conditionType = conditionType;
        this.condition = condition;
        this.agentParameter = agentParameter;
        this.value = value;
        this.hasValue = hasValue;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getDisplay() {
        return display;
    }

    public String getOption() {
        return option;
    }

    public String getConditionType() {
        return conditionType;
    }

    public String getCondition() {
        return condition;
    }

    public String getagentParameter() {
        return agentParameter;
    }

    public String getValue() {
        return value;
    }

    public int isHasValue() {
        return hasValue;
    }
}
