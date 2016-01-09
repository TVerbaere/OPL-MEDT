package com.iagl.opl.medt;


public class TestMEDT {
	
	public static void main(String[] args) {
				
		MagicalExperimentalDebugTool medt = new MagicalExperimentalDebugTool(ATest.class,"src/test/java/");
		
		medt.debugClass();
				
	}

}
