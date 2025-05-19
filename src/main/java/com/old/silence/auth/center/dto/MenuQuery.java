package com.old.silence.auth.center.dto;


import com.old.silence.auth.center.enums.MenuType;
import com.old.silence.data.commons.annotation.RelationalQueryProperty;
import com.old.silence.data.commons.converter.Part;

/**
 * @author moryzang
 */
public class MenuQuery {
    @RelationalQueryProperty(type = Part.Type.STARTING_WITH)
    private String name;

    @RelationalQueryProperty(type = Part.Type.SIMPLE_PROPERTY)
    private MenuType type;

    @RelationalQueryProperty(type = Part.Type.STARTING_WITH)
    private String routePath;

    @RelationalQueryProperty(type = Part.Type.SIMPLE_PROPERTY)
    private Boolean status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MenuType getType() {
        return type;
    }

    public void setType(MenuType type) {
        this.type = type;
    }

    public String getRoutePath() {
        return routePath;
    }

    public void setRoutePath(String routePath) {
        this.routePath = routePath;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
