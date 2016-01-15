package com.iagl.opl.medt;

public class Launcher {
	
	public static void main(String[] args) throws Exception {
		
		if (args.length == 2) {

			MagicalExperimentalDebuggingTool medt = new MagicalExperimentalDebuggingTool(args[0], args[1]);
		
			medt.debugClass();
		
		}else if (args.length == 3) {

			MagicalExperimentalDebuggingTool medt = new MagicalExperimentalDebuggingTool(args[0], args[1], args[2]);
		
			medt.debugClass();
		
		}
		else{
			
			System.out.println( "Bad Argument, Template : medt.jar PATH_TO_TEST_CLASS_DIR   PACKAGE.TEST_CLASS [PACKAGE.TESTED_CLASS]");
		}
				
	}

}
