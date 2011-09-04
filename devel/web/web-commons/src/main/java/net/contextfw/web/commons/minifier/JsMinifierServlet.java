package net.contextfw.web.commons.minifier;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import net.contextfw.web.application.DocumentProcessor;
import net.contextfw.web.application.WebApplicationException;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import com.google.inject.Singleton;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;

@Singleton
public class JsMinifierServlet extends ContentServlet implements DocumentProcessor {

    private static final long serialVersionUID = 1L;

    private final MinifierFilter filter;

    JsMinifierServlet(String host,
            String minifiedPath,
            MinifierFilter filter,
            long started) {
        super(host, minifiedPath, started);
        this.filter = filter;
    }

    @Override
    public void process(Document document) {

        @SuppressWarnings("unchecked")
        List<Element> scripts = document.selectNodes("//html/head//script[@src]");
        StringBuilder sb = new StringBuilder();
        Element firstMinified = null;

        Iterator<Element> iter = scripts.iterator();

        while (iter.hasNext()) {
            Element script = iter.next();
            String src = script.attributeValue("src");
            if (src.startsWith("{$contextPath}") && filter.include(src)) {
                URL url = getUrl(src.replace("{$contextPath}",
                        this.getServletContext().getContextPath()));

                if (filter.minify(src)) {
                    sb.append(compress(url)).append("\n");
                } else {
                    try {
                        sb.append(IOUtils.toString(url.openStream())).append("\n");
                    } catch (IOException e) {
                        throw new WebApplicationException(e);
                    }
                }
            }

            if (firstMinified == null) {
                firstMinified = script;
            } else {
                script.detach();
            }
        }

        if (firstMinified != null) {
            firstMinified.addAttribute("src", "{$contextPath}" + getMinifiedPath());
        }

        setContent(sb.toString());
    }

    private String compress(URL url) {
        try {
            CompilerOptions options = new CompilerOptions();
            Compiler compiler = new Compiler();
            JSSourceFile source = JSSourceFile.fromInputStream(url.getFile(), url.openStream());
            compiler.compile(new JSSourceFile[] {}, new JSSourceFile[] { source }, options);
            return compiler.toSource();
        } catch (Exception e) {
            throw new WebApplicationException(url.toString(), e);
        }
    }

    @Override
    protected String getContentType() {
        return "application/javascript";
    }
}