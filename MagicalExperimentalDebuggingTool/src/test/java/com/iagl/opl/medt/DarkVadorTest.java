package com.iagl.opl.medt;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class DarkVadorTest {
	
	private DarkVador dark;

	@Before
	public void before(){
		dark = new DarkVador();
	}
	
	@Test
	public void sayImYourFatherTest() {
		assertEquals("Luke, I'm your father !", dark.sayImYourFather("Luke"));
	}
	
	@Test
	public void getSideTest() {
		assertEquals("DARK", dark.getSide());
	}

}
