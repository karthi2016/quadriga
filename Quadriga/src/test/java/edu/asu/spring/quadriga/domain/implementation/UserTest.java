package edu.asu.spring.quadriga.domain.implementation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.asu.spring.quadriga.domain.IQuadrigaRoles;

/*
* @Description 	: tests getters and setters for user name, username, password, email and quadrigaRoles
* 
* @author		: Rohit Pendbhaje
* 
*/

public class UserTest {
	
	private User user;
	private List<IQuadrigaRoles> quadrigaRoles; 

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		
		this.user = new User();
		
		this.quadrigaRoles = new ArrayList<IQuadrigaRoles>();
		quadrigaRoles.add(new QuadrigaRole());
		quadrigaRoles.add(new QuadrigaRole());
				
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetName() {
		
		user.setName(null);
		assertEquals(user.getName(), null);
		
		user.setName("john");
		assertEquals(user.getName(), "john");	
	}

	
	@Test
	public void testGetUserName() {
		
		user.setUserName(null);
		assertEquals(user.getUserName(), null);
		
		user.setUserName("j218");
		assertEquals(user.getUserName(), "j218");

		
	}

	@Test
	public void testGetPassword() {
		
		user.setPassword(null);
		assertEquals(user.getPassword(), null);
		
		user.setPassword("oreo1280");
		assertEquals(user.getPassword(), "oreo1280");
	}
	
	@Test
	public void testGetEmail() {
		
		user.setEmail(null);
		assertEquals(user.getEmail(), null);
		
		user.setEmail("xyz@lsa.com");
		assertEquals(user.getEmail(), "xyz@lsa.com");
	}

	@Test
	public void testGetQuadrigaRoles() {
		
		user.setQuadrigaRoles(null);
		assertEquals(user.getQuadrigaRoles(),null);
		
		user.setQuadrigaRoles(quadrigaRoles);
		assertEquals(user.getQuadrigaRoles(),quadrigaRoles);
			
	}

}
