package com.iagl.opl.MagicalExperimentalDebugTool;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class ATest {
A a;
	
	@Before
	public void before(){
		a= new A();
	}
	
	@Test
	public void testUpJohn() {
		assertTrue(a.getJohn()==1);
		a.upJohn();
		
		assertTrue(a.getJohn()==2);

	}

	@Test
	public void testEraseToto() {

		assertTrue(a.getToto() != null);
		
		a.eraseToto();
		
		assertTrue(a.getToto()==null);
	
	}

	@Test
	public void testTotoSize() {
		
		assertTrue(a.getToto() != null);
		
		assertTrue(a.totoSize() == 4);
	}

}
