package com.toni.issue_parser;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.toni.issue_parser.IssueEntity;
import com.toni.issue_parser.IssueParser;
import com.toni.issue_parser.ProcessedComment;

public class IssueParserTest {

	static IssueEntity issueEntity;
	static List<IssueEntity> issueEntities;

	@BeforeClass
	public static void init() throws URISyntaxException {
		issueEntity = new IssueEntity();
		issueEntities = new ArrayList<IssueEntity>();

		issueEntity.setSummary("entrysummary");
		issueEntity.setKey("entrykey");
		issueEntity.setUri(new URI("https://jira.atlassian.com"));
		issueEntity.setType("entrytype");
		issueEntity.setPriority("enrtypriority");
		issueEntity.setDescription("entrydescription");
		issueEntity.setReporterName("entryreporter");
		issueEntity.setCreationDate("2017-03-11");
		List<ProcessedComment> comments = new ArrayList<ProcessedComment>();
		comments.add(new ProcessedComment("entrycommentbody1", "entryauthorname1"));
		comments.add(new ProcessedComment("entrycommentbody2", "entryauthorname2"));
		issueEntity.setComments(comments);

		issueEntities.add(issueEntity);
	}

	@Test
	public void testAllAttributesJSON() throws IOException, ParseException, JSONException {
		IssueParser.parseToJSON(issueEntities);
		String text = new String(Files.readAllBytes(Paths.get("issues.json")), StandardCharsets.UTF_8);
		JSONArray arr = new JSONArray(text);
		JSONObject json = (JSONObject) arr.get(0);

		assertTrue(json.has("summary"));
		assertTrue(json.has("key"));
		assertTrue(json.has("uri"));
		assertTrue(json.has("type"));
		assertTrue(json.has("priority"));
		assertTrue(json.has("description"));
		assertTrue(json.has("reporterName"));
		assertTrue(json.has("creationDate"));
		assertTrue(json.has("comments"));
	}

	@Test
	public void testRightValuesJSON() throws IOException, ParseException, JSONException {

		IssueParser.parseToJSON(issueEntities);
		String text = new String(Files.readAllBytes(Paths.get("issues.json")), StandardCharsets.UTF_8);
		JSONArray arr = new JSONArray(text);
		JSONObject json = (JSONObject) arr.get(0);

		assertEquals(issueEntity.getSummary(), json.get("summary"));
		assertEquals(issueEntity.getKey(), json.get("key").toString());
		assertEquals(issueEntity.getUri().toString(), json.get("uri"));
		assertEquals(issueEntity.getType(), json.get("type").toString());
		assertEquals(issueEntity.getPriority(), json.get("priority"));
		assertEquals(issueEntity.getDescription(), json.get("description"));
		assertEquals(issueEntity.getReporterName(), json.get("reporterName"));
		assertEquals(issueEntity.getCreationDate(), json.get("creationDate"));

		JSONArray comments = json.getJSONArray("comments");
		assertEquals(2, comments.length());

		JSONObject com1 = (JSONObject) comments.get(0);
		String expectedText1 = issueEntities.get(0).getComments().get(0).getText();
		assertEquals(expectedText1, com1.get("text"));
		String expectedAuthorName1 = issueEntities.get(0).getComments().get(0).getAuthorName();
		assertEquals(expectedAuthorName1, com1.get("authorName"));

		JSONObject com2 = (JSONObject) comments.get(1);
		String expectedText2 = issueEntities.get(0).getComments().get(1).getText();
		assertEquals(expectedText2, com2.get("text"));
		String expectedAuthorName2 = issueEntities.get(0).getComments().get(1).getAuthorName();
		assertEquals(expectedAuthorName2, com2.get("authorName"));
	}

	@Test
	public void testRightValuesXML() throws ParserConfigurationException, SAXException, IOException, JAXBException {
		IssueParser.parseToXML(issueEntities);

		File file = new File("issues.xml");
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);

		NodeList nodeList = doc.getElementsByTagName("issueEntity");

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				assertEquals(issueEntity.getSummary(), element.getElementsByTagName("summary").item(0).getTextContent());
				assertEquals(issueEntity.getKey(), element.getElementsByTagName("key").item(0).getTextContent());
				assertEquals(issueEntity.getUri().toString(), element.getElementsByTagName("uri").item(0).getTextContent());
				assertEquals(issueEntity.getType(), element.getElementsByTagName("type").item(0).getTextContent());
				assertEquals(issueEntity.getPriority(), element.getElementsByTagName("priority").item(0).getTextContent());
				assertEquals(issueEntity.getDescription(), element.getElementsByTagName("description").item(0).getTextContent());
				assertEquals(issueEntity.getReporterName(), element.getElementsByTagName("reporterName").item(0).getTextContent());
				assertEquals(issueEntity.getCreationDate(), element.getElementsByTagName("creationDate").item(0).getTextContent());
				
				String expectedText1 = issueEntities.get(0).getComments().get(0).getText();
				String expectedAuthorName1 = issueEntities.get(0).getComments().get(0).getAuthorName();
				assertTrue(element.getElementsByTagName("comments").item(0).getTextContent().contains(expectedText1));
				assertTrue(element.getElementsByTagName("comments").item(0).getTextContent().contains(expectedAuthorName1));
				
				String expectedText2 = issueEntities.get(0).getComments().get(1).getText();
				String expectedAuthorName2 = issueEntities.get(0).getComments().get(1).getAuthorName();
				assertTrue(element.getElementsByTagName("comments").item(1).getTextContent().contains(expectedText2));
				assertTrue(element.getElementsByTagName("comments").item(1).getTextContent().contains(expectedAuthorName2));
				

			}
		}

	}

}
