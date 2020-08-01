package io.jmix.samples.ui.util;

import com.google.common.collect.ImmutableMap;
import io.jmix.core.Resources;
import io.jmix.core.common.xmlparsing.Dom4jTools;
import io.jmix.samples.ui.config.MenuItem;
import io.jmix.ui.WindowInfo;
import io.jmix.ui.screen.MapScreenOptions;
import io.jmix.ui.screen.ScreenOptions;
import io.jmix.ui.sys.XmlInheritanceProcessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component(SamplerHelper.NAME)
public class SamplerHelper {

    public static final String NAME = "sampler_SamplerHelper";

    protected static final Map<String, Object> EMPTY_MAP = new HashMap<>();

    private static final Log logger = LogFactory.getLog(SamplerHelper.class);

    @Autowired
    protected Resources resources;
    @Autowired
    protected ObjectProvider<XmlInheritanceProcessor> xmlInheritanceProcessor;
    @Autowired
    protected Dom4jTools dom4JTools;

    public String getSampleBrowserId() {
        return "sample-browser";
    }

    public String getFileContent(String src) {
        return resources.getResourceAsString(src);
    }

    public ScreenOptions getScreenOptions(MenuItem item) {
        return new MapScreenOptions(ImmutableMap.of("windowId", item.getId()));
    }

    protected String getMessagePack(Element root) {
        return root.attributeValue("messagesPack");
    }

    public String packageToPath(String pkg) {
        return pkg.replaceAll("[.]", "/");
    }

    public String getFileName(String src) {
        Path p = Paths.get(src);
        return p.getFileName().toString();
    }

    @Nullable
    public String getFileExtension(String src) {
        String fileName = getFileName(src);
        int index = fileName.lastIndexOf(".");
        if (index >= 0) {
            return fileName.substring(index + 1);
        }
        return null;
    }

    @Nullable
    public String findMessagePack(WindowInfo info) {
        Element root = getWindowElement(info.getTemplate());
        return (root != null)
                ? getMessagePack(root)
                : null;
    }

    @Nullable
    protected Element getWindowElement(String src) {
        String text = resources.getResourceAsString(src);
        if (StringUtils.isNotEmpty(text)) {
            try {
                Document document = dom4JTools.readDocument(text);
                XmlInheritanceProcessor processor = xmlInheritanceProcessor.getObject(document, EMPTY_MAP);
                Element root = processor.getResultRoot();
                if (root.getName().equals("window")) {
                    return root;
                }
            } catch (RuntimeException e) {
                logger.error("Can't parse screen file: " + src);
            }
        } else {
            logger.error("File doesn't exist or empty: " + src);
        }
        return null;
    }
}
