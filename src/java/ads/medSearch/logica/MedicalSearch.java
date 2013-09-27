/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ads.medSearch.logica;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Charic D. Farinango
 */
public class MedicalSearch {

    public static String url = "http://wsearch.nlm.nih.gov/ws/query?";

    public static ArrayList<String> getMedicalTopics(String keyword) {
        HttpClient client = new DefaultHttpClient();
        String responseBody = null;

        keyword = keyword.replaceAll(" ", "+");
        HttpGet request = new HttpGet(url + "db=healthTopicsSpanish&term=" + keyword + "&rettype=all" + "&retmax=1");
        try {
            HttpResponse response = client.execute(request);
            responseBody = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            Logger.getLogger(MedicalSearch.class.getName()).log(Level.SEVERE, url); 
        }
        return processXML(responseBody);
    }

    private static ArrayList<String> processXML(String xml) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        String definition = null;
        String name = null;
        String title = null;
        String synonym = null;
        String category = null;
        ArrayList<String> information = new ArrayList<String>();
        String contador;

        try {
            db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
            doc.getDocumentElement().normalize(); 

            Element rootElement = doc.getDocumentElement();
            NodeList list = rootElement.getElementsByTagName("list");
            Element e = (Element) list.item(0);
            NodeList document = e.getElementsByTagName("document");
            Element e1 = (Element) document.item(0);
            NodeList contents = e1.getElementsByTagName("content");
            for (int i = 0; i < contents.getLength(); i++) {
                Element e2 = (Element) contents.item(i);
                name = e2.getAttribute("name");
                if ("title".equals(name)) {
                    title = e2.getTextContent();
                    information.add(title);
                } else if ("altTitle".equals(name)) {
                    synonym = e2.getTextContent();
                    information.add(synonym);

                } else if ("healthTopic".equals(name)) {
                    contador = "definition";
                    information.add(contador);
                    NodeList health_topic = e2.getElementsByTagName("health-topic");
                    Element e3 = (Element) health_topic.item(0);
                    NodeList full_summary = e3.getElementsByTagName("full-summary");
                    NodeList site = e3.getElementsByTagName("site");
                    Element e4 = (Element) full_summary.item(0);
                    definition = e4.getTextContent();
                    definition = definition.replaceAll("\\<.*?>", "");
                    definition = definition.replaceAll("&.*?;", "");
                    information.add(definition);
                    contador = "overview";
                    information.add(contador);

                    for (int j = 0; j < site.getLength(); j++) {
                        Element e5 = (Element) site.item(j);
                        NodeList information_category = e5.getElementsByTagName("information-category");
                        Element e6 = (Element) information_category.item(0);
                        category = e6.getTextContent();

                        if ("Resúmenes".equals(category)) {
                            String titleOverview = null;
                            String urlOverview = null;

                            titleOverview = e5.getAttribute("title");
                            urlOverview = e5.getAttribute("url");
                            information.add(titleOverview);
                            information.add(urlOverview);
                        } else if ("Últimas noticias".equals(category)) {
                            String noticia = null;
                            String urlNoticia = null;

                            noticia = e5.getAttribute("title");
                            urlNoticia = e5.getAttribute("url");
                            information.add(noticia);
                            information.add(urlNoticia);
                        }
                    }
                }
        }


    }
    catch (SAXException e) {
            Logger.getLogger(MedicalSearch.class.getName()).log(Level.SEVERE, null, e);
    }
    catch (IOException e) {
            Logger.getLogger(MedicalSearch.class.getName()).log(Level.SEVERE, xml, e);
    }
    catch (ParserConfigurationException e) {
            Logger.getLogger(MedicalSearch.class.getName()).log(Level.SEVERE, xml, e);
    }
    return  information ;
}
}