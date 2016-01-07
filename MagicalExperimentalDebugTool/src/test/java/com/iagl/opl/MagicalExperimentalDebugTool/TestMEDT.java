package com.iagl.opl.MagicalExperimentalDebugTool;

import java.util.List;

public class TestMEDT {
	
	public static void main(String[] args) {
		MEDT medt = new MEDT(ATest.class);
		
		List<Integer> failed = medt.runTestClass();
		
		for (Integer _assert : failed)
			System.out.println("assert failed line : "+_assert);
		
		medt.debugClass();
		
		int res = medt.regressions(failed);
		
		switch(res) {
		case 0: System.out.println("no regression"); break;
		case -1: System.out.println("regression"); break;
		default: System.out.println("improve program"); break;
		}

	}

}
