package net.contextfw.benchmark.views;

import java.util.List;

import net.contextfw.benchmark.ProductService;
import net.contextfw.benchmark.components.PageTitle;
import net.contextfw.benchmark.dto.Product;
import net.contextfw.web.application.HttpContext;
import net.contextfw.web.application.component.Attribute;
import net.contextfw.web.application.component.Component;
import net.contextfw.web.application.component.Element;
import net.contextfw.web.application.lifecycle.PageScoped;
import net.contextfw.web.application.lifecycle.View;
import net.contextfw.web.application.lifecycle.ViewComponent;
import net.contextfw.web.application.lifecycle.ViewContext;
import net.contextfw.web.application.remote.RequestParam;

import com.google.inject.Inject;

@PageScoped
@View(url = { "/test1", "/test1/" }, parent = RootView.class)
public class Test1View extends Component implements ViewComponent {
    
    private final ProductService productService;

    private int productCount = 0;

    @RequestParam(name="count")
    public void setProductCount(Integer productCount) {
        this.productCount = productCount == null ? 0 : productCount;
        title.title = "Test1 - " + this.productCount + " products";
    }
    
    @Inject
    public Test1View(ProductService productService) {
        this.productService = productService;
    }

    // The title is actually managed by the RootView, but
    // the component PageTitle can be accessed from inner views
    @Inject
    private PageTitle title;

    @Attribute
    public String pageName() {
        return "Producs listing: " + productCount;
    }

    @Element
    public List<Product> products() {
        return productService.getProducts(productCount);
    }

    @Override
    public void initialize(ViewContext context) {
        // postConstruct();
    }
}
