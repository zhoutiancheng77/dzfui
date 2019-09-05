package com.dzf.zxkj.base.framework.util;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.InputStream;

public final class XMLUtils {

    /**
     *
     */
    private static final SAXTransformerFactory saxFactory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();

    /**
     *
     */
    private static TransformerFactory transformerFactory;

    /**
     * @return
     * @throws TransformerConfigurationException
     */
    public static TransformerHandler createTransformerHandler() throws TransformerConfigurationException {
        return saxFactory.newTransformerHandler();
    }

    /**
     * @return
     * @throws TransformerConfigurationException
     */
    public static TransformerHandler createTransformerHandler(Templates template)
            throws TransformerConfigurationException {
        return saxFactory.newTransformerHandler(template);
    }

    /**
     * @param is
     * @return
     * @throws TransformerConfigurationException
     */
    public static Templates createTemplate(InputStream is) throws TransformerConfigurationException {
        return createTemplate(new StreamSource(is));
    }

    /**
     * @param is
     * @return
     * @throws TransformerConfigurationException
     */
    public static Templates createTemplate(File file) throws TransformerConfigurationException {
        return createTemplate(new StreamSource(file));
    }

    /**
     * @param streamSource
     * @return
     * @throws TransformerConfigurationException
     */
    private static Templates createTemplate(StreamSource streamSource) throws TransformerConfigurationException {
        if (transformerFactory == null) {
            synchronized (XMLUtils.class) {
                if (transformerFactory == null) {
                    transformerFactory = TransformerFactory.newInstance();
                }
            }
        }
        return transformerFactory.newTemplates(streamSource);
    }
}