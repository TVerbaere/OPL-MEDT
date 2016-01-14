package com.iagl.opl.medt;

public class Launcher {
	
	public static void main(String[] args) {
		
		if (args.length == 2) {

			MagicalExperimentalDebuggingTool medt = new MagicalExperimentalDebuggingTool(args[0], args[1]);
		
			medt.debugClass();
		
		}
				
	}

}
