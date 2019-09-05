package com.dzf.zxkj.base.framework.processor;

import com.dzf.zxkj.base.framework.util.EncodingUtils;
import com.dzf.zxkj.base.framework.util.XMLUtils;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;


public class XMLProcessor extends OutputStreamProcessor {

    /**
     *
     */
    private final static String EMPTY_URI = "", DEFAULT_ENCODING = "UTF-8",
            DEFAULT_ROOT = "result-set", DEFAULT_ROW = "row";

    /**
     *
     */
    private TransformerHandler th;

    /**
     *
     */
    private Templates template;

    /**
     *
     */
    private String rootElement, rowElement;

    /**
     *
     */
    private Map xslParams;

    /**
     * @param rootElement
     * @param rowElement
     */
    public void SetRootRowNames(String rootElement, String rowElement) {
        this.rootElement = rootElement;
        this.rowElement = rowElement;
    }

    /**
     * @param f
     * @throws TransformerConfigurationException
     */
    public void setXSL(File f) throws TransformerConfigurationException {

        this.template = XMLUtils.createTemplate(f);
    }

    /**
     * @param is
     * @throws TransformerConfigurationException
     */
    public void setXSL(InputStream is) throws TransformerConfigurationException {
        this.template = XMLUtils.createTemplate(is);
    }

    /**
     * @param xslParams
     */
    public void setXslParams(Map xslParams) {
        this.xslParams = xslParams;
    }

    /**
     *
     */
    public Object handleRS(ResultSet rs) throws SQLException {
        try {
            if (this.rootElement == null || this.rowElement == null) {
                this.rootElement = DEFAULT_ROOT;
                this.rowElement = DEFAULT_ROW;
            }

            if (this.template == null) {
                this.th = XMLUtils.createTransformerHandler();
                Transformer serializer = this.th.getTransformer();
                serializer.setOutputProperty(OutputKeys.ENCODING,
                        DEFAULT_ENCODING);
            } else {
                this.th = XMLUtils.createTransformerHandler(this.template);
                if (this.xslParams != null) {
                    Transformer serializer = this.th.getTransformer();
                    Object[] keys = this.xslParams.keySet().toArray();
                    for (int i = 0; i < keys.length; i++) {
                        String key = keys[i].toString();
                        Object value = this.xslParams.get(keys[i]);

                        serializer.setParameter(key, value);
                    }
                }
            }
            this.th.setResult(new StreamResult(this.outputStreamPtr));

            AttributesImpl atts = new AttributesImpl();
            this.th.startDocument();
            this.th.startElement(EMPTY_URI, this.rootElement, this.rootElement,
                    atts);

            ResultSetMetaData rsMeta = rs.getMetaData();
            while (rs.next()) {
                this.th.startElement(EMPTY_URI, this.rowElement,
                        this.rowElement, atts);

                for (int i = 1; i <= rsMeta.getColumnCount(); i++) {
                    if (rs.getObject(i) != null) {
                        String colname = rsMeta.getColumnName(i);
                        String colValue = null;
                        if (rsMeta.getColumnType(i) == Types.BINARY
                                || rsMeta.getColumnType(i) == Types.LONGVARBINARY) {
                            colValue = EncodingUtils.base64Encode(rs
                                    .getBytes(i));
                        } else {
                            colValue = new String(rs.getString(i).getBytes(
                                    DEFAULT_ENCODING));
                        }

                        this.th.startElement(EMPTY_URI, colname, colname, atts);
                        this.th.characters(colValue.toCharArray(), 0, colValue
                                .length());
                        this.th.endElement(EMPTY_URI, colname, colname);
                    }
                }
                this.th.endElement(EMPTY_URI, this.rowElement, this.rowElement);
            }

            this.th.endElement(EMPTY_URI, this.rootElement, this.rootElement);
            this.th.endDocument();
        } catch (Exception e) {

        }
        return null;
    }

}
