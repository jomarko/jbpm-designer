package org.jbpm.designer.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
@Portable
public class User extends Participant {

    public User() {
    }

    public User(String name) {
        super(name);
    }
}
