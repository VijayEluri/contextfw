package net.contextfw.web.commons.minifier;


public class MinifierConf {

    public static final MinifierFilter ALL = new MinifierFilter() {
        @Override
        public boolean include(String path) {
            return true;        }

        @Override
        public boolean minify(String path) {
            return true;
        }
    };
    
    public static final MinifierFilter NO_JQUERY = new MinifierFilter() {
        @Override
        public boolean include(String path) {
            return true;
        }
        @Override
        public boolean minify(String path) {
            return !path.contains("jquery");
        }
    };
    
    private String jsPath;
    
    private String cssPath;
    
    private boolean developmentMode;
    
    private MinifierFilter jsFilter = ALL;
    
    private MinifierFilter cssFilter = ALL;
    
    private String host;
    
    public String getJsPath() {
        return jsPath;
    }
    
    public void setJsPath(String jsPath) {
        this.jsPath = jsPath;
    }
    
    public String getCssPath() {
        return cssPath;
    }
    
    public void setCssPath(String cssPath) {
        this.cssPath = cssPath;
    }
    
    public boolean isDevelopmentMode() {
        return developmentMode;
    }
    
    public void setDevelopmentMode(boolean developmentMode) {
        this.developmentMode = developmentMode;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setHost(String host) {
        this.host = host;
    }
    
    public void setJsFilter(MinifierFilter jsFilter) {
        if (jsFilter == null) {
            throw new IllegalArgumentException("jsFilter cannot be null");
        }
        this.jsFilter = jsFilter;
    }
    
    public MinifierFilter getJsFilter() {
        return jsFilter;
    }
    
    public void setCssFilter(MinifierFilter cssFilter) {
        if (cssFilter == null) {
            throw new IllegalArgumentException("CssFilter cannot be null");
        }
        this.cssFilter = cssFilter;
    }
    public MinifierFilter getCssFilter() {
        return cssFilter;
    }
}
