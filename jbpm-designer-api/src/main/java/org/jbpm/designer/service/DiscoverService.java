package org.jbpm.designer.service;

import org.jboss.errai.bus.server.annotations.Remote;

import java.util.List;

@Remote
public interface DiscoverService {
    List<String> getExistingDataTypes();
}
