package org.jbpm.designer.server.service;

import org.jboss.errai.bus.server.annotations.Service;
import org.jbpm.designer.service.DiscoverService;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueTypeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
@ApplicationScoped
public class DefaultDiscoverService implements DiscoverService {
    @Inject
    private RefactoringQueryService queryService;

    @Override
    public List<String> getExistingDataTypes() {
        final List<RefactoringPageRow> results2 = queryService.query("DesignerFindTypesQuery",
                new HashSet<ValueIndexTerm>() {{
                    add(new ValueTypeIndexTerm("*"));
                }},
                true);
        final List<String> dataTypeNames = new ArrayList<String>();
        for ( RefactoringPageRow row : results2 ) {
            dataTypeNames.add( (String) row.getValue() );
        }
        Collections.sort( dataTypeNames );
        dataTypeNames.add(0, "java.util.List");
        dataTypeNames.add(0, "String");
        dataTypeNames.add(0, "Object");
        dataTypeNames.add(0, "Integer");
        dataTypeNames.add(0, "Float");
        dataTypeNames.add(0, "Double");
        dataTypeNames.add(0, "Boolean");
        return dataTypeNames;
    }
}
