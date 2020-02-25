package com.payroll.employee;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestMethodOrder(OrderAnnotation.class)
class EmployeeApplicationTests {
	
	private static String baseUrl = "http://localhost:8080";

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	@Order(1)
	void Create() throws URISyntaxException {
		URI	uri = new URI(baseUrl+ "/employees");
		
        Employee employee = new Employee("Larry Bird", "Fordward");
         
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");      
 
        HttpEntity<Employee> request = new HttpEntity<>(employee, headers);
         
        ResponseEntity<String> result = this.restTemplate.postForEntity(uri, request, String.class);
         
        //Verify request succeed
        assertThat(result.getStatusCodeValue() == 201);
        
	}
	
	@Test
	@Order(2)
	void Read() throws URISyntaxException {
		//curl -v localhost:8080/employees/{id}
		String url = baseUrl+ "/employees/{id}";
		
		HashMap<String,String> params = new HashMap<String, String>();
		params.put("id", "3");
		
        ResponseEntity<String> result = this.restTemplate.getForEntity(url, String.class, params);
         
        //Verify request succeed
        assertThat(result.getStatusCodeValue() == 200 
        		&& result.getBody().contains("Larry Bird"));	
   }
	
	@Test
	@Order(3)
	void Update() throws URISyntaxException {
		String url = baseUrl+ "/employees/{id}";
		
        Employee employee = new Employee("Michael Jordan", "TheBest");
         
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");      
 
        HttpEntity<Employee> request = new HttpEntity<>(employee, headers);
        
		HashMap<String,String> params = new HashMap<String, String>();
		params.put("id", "3");
         
        this.restTemplate.put(url, request, params); 
         
        //Leemos el id 3 y comprobamos que es Michael
        ResponseEntity<String> result = this.restTemplate.getForEntity(url, String.class, params);
        
        //Verify request succeed
        assertThat(result.getStatusCodeValue() == 200 
        		&& result.getBody().contains("Michael Jordan"));	

	}
	
	@Test
	@Order(4)
	void Delete() throws URISyntaxException {
		//curl -X DELETE localhost:8080/employees/3 $ curl localhost:8080/employees/3 Could not find employee 3
		String url = baseUrl+ "/employees/{id}";
		
		HashMap<String,String> params = new HashMap<String, String>();
		params.put("id", "3");
		     
		this.restTemplate.delete(url, params);
		
		//Comprobamos buscando el registro borrado		
		ResponseEntity<String> result = this.restTemplate.getForEntity(url, String.class, params);
		
		//Se lanza un excepción, "Could not find employee with id: 3"
		assertThat(result.getStatusCodeValue() == 500);	
	}


}
