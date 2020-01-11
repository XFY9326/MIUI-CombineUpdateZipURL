package tool.xfy9326.miui.getupdateurl.tools;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PrefStringReader {
    private static final String TAG_STRING = "string";
    private static final String ATTR_NAME = "name";

    private Element mainElement;

    public PrefStringReader(String xmlContent) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
        mainElement = document.getDocumentElement();
    }

    @Nullable
    public String getString(@NonNull String key) {
        NodeList nodeList = mainElement.getElementsByTagName(TAG_STRING);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (key.equals(item.getAttributes().getNamedItem(ATTR_NAME).getNodeValue())) {
                return item.getTextContent();
            }
        }
        return null;
    }
}
