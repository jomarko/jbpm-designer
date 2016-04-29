package org.jbpm.designer.model.operation;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;

@Portable
@Bindable
public class SwaggerPath {
    private SwaggerOperation get;
    private SwaggerOperation delete;
    private SwaggerOperation post;

    public SwaggerOperation getGet() {
        return get;
    }

    public void setGet(SwaggerOperation get) {
        this.get = get;
    }

    public SwaggerOperation getDelete() {
        return delete;
    }

    public void setDelete(SwaggerOperation delete) {
        this.delete = delete;
    }

    public SwaggerOperation getPost() {
        return post;
    }

    public void setPost(SwaggerOperation post) {
        this.post = post;
    }
}
