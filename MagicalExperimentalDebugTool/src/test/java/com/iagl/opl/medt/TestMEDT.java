package com.iagl.opl.medt;


public class TestMEDT {
	
	public static void main(String[] args) {
				
		MagicalExperimentalDebuggingTool medt = new MagicalExperimentalDebuggingTool(DarkVadorTest.class,"src/test/java/");
		
		medt.debugClass();
				
	}

}
