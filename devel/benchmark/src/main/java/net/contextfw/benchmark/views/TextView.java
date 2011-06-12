package net.contextfw.benchmark.views;

import javax.annotation.PostConstruct;

import net.contextfw.benchmark.PlainTextResponder;
import net.contextfw.benchmark.ProductService;
import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.lifecycle.PageScoped;
import net.contextfw.web.application.lifecycle.ResourceView;
import net.contextfw.web.application.lifecycle.View;
import net.contextfw.web.application.util.Request;

import com.google.inject.Inject;

@PageScoped
@View(url = "/text1", parent = RootView.class)
public class TextView extends Component implements ResourceView {

    private final ProductService productService;

    private final HttpContext httpContext;

    private int productCount = 0;

    @Inject
    public TextView(ProductService productService, HttpContext httpContext) {
        this.productService = productService;
        this.httpContext = httpContext;
    }

    @PostConstruct
    public void postConstruct() {
        productCount = new Request(httpContext.getRequest())
            .param("count").getIntValue(0);
    }

    @Override
    public Object getResponse() {
        return new PlainTextResponder("Hellow world! : " + productCount);
    }
}
