package com.contact.app;

import com.contact.app.domain.Contact;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertNotNull;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = AppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppApplicationTests {

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	private int port;

	private String getRootUrl() {
		return "http://localhost:" + port;
	}

	private static HttpHeaders headers;

	@BeforeAll
	public static void setAuthorizationToken() {
		headers = new HttpHeaders();
		List<String> auth = new LinkedList<>();
		auth.add("Basic YWRtaW46bmltZGE=");
		headers.put("Authorization", auth);
	}

	@Test
	public void getContacts() {
		HttpEntity entity = new HttpEntity<>(null, headers);
		ResponseEntity<Object> response = restTemplate.exchange(getRootUrl(), HttpMethod.GET, entity, Object.class);
		assertNotNull(response.getBody());
	}

	@Test
	public void testCrudContact() {
		Contact contact = new Contact();
		contact.setEmail("admin@gmail.com");
		contact.setName("admin");
		HttpEntity entity = new HttpEntity<>(contact, headers);
		ResponseEntity<Contact> createResponse = restTemplate.postForEntity(getRootUrl(), entity, Contact.class);
		assertNotNull(createResponse);
		assertNotNull(createResponse.getBody());
		contact = createResponse.getBody();
		restTemplate.put(getRootUrl(), contact);
		restTemplate.delete(getRootUrl(), contact.getId());
	}

}
