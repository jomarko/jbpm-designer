package org.jbpm.designer.client.shared;

import org.jboss.errai.databinding.client.api.Bindable;

@Bindable
public class User extends Participant {
    public User() {
    }

    public User(String name) {
        super(name);
    }
}
