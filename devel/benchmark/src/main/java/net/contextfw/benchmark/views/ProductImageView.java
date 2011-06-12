package net.contextfw.benchmark.views;

import net.contextfw.benchmark.ProductImageResponder;
import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.lifecycle.PageScoped;
import net.contextfw.web.application.lifecycle.ResourceView;
import net.contextfw.web.application.lifecycle.View;

import com.google.inject.Inject;

@PageScoped
@View(url = "regex:/productImage/.+.jpg")
public class ProductImageView extends Component implements ResourceView {

    private final HttpContext httpContext;

    @Inject
    public ProductImageView(HttpContext httpContext) {
        this.httpContext = httpContext;
    }
    
    @Override
    public Object getResponse() {
        String name = httpContext.getRequest().getRequestURI();
        int first = name.lastIndexOf("/") + 1;
        int last = name.lastIndexOf(".");
        return new ProductImageResponder(name.substring(first, last));
    }
}
