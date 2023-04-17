package com.elseplus.createconfig.test;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * @program: javafx_desktop_apptemplate
 * @description:
 * @author: fangqing.fan#hotmail.com
 * @create: 2023/3/24 15:38
 **/
public class GlobTest {

 @Test
 public void test() throws ParserConfigurationException, IOException, SAXException {

  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
  DocumentBuilder db = dbf.newDocumentBuilder();
  Document doc = db.parse("../pom.xml");
  Element rootElement = doc.getDocumentElement();
  String groupId = parseElementContent(rootElement, "groupId");
  String artifactId = parseElementContent(rootElement, "artifactId");
  String version = parseElementContent(rootElement, "version");
  String packaging = parseElementContent(rootElement, "packaging");

  System.out.println("groupId:"+groupId);
  System.out.println("artifactId:"+artifactId);
  System.out.println("version:"+version);
  System.out.println("packaging:"+packaging);
 }

 private static String parseElementContent(Element element, String nodeName) {
  NodeList nodeList = element.getElementsByTagName(nodeName);
  for (int i = 0; i < nodeList.getLength(); i++) {
   String parentName = nodeList.item(i).getParentNode().getNodeName();
   if ("project".equals(parentName)) {
    return nodeList.item(i).getTextContent();
   }
  }
  return "";
 }

}
