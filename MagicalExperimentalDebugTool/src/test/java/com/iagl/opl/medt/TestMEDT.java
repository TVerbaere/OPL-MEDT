package com.iagl.opl.medt;

import java.util.List;

public class TestMEDT {
	
	public static void main(String[] args) {
		MagicalExperimentalDebugTool medt = new MagicalExperimentalDebugTool(ATest.class);
		
		System.out.println("Classe testée : "+medt.getTestedClass());
		
		List<Integer> failed = medt.runTestClass();
		
		for (Integer _assert : failed)
			System.out.println("assert line : "+_assert);
		
		for (String method : medt.getProblematicMethods())
			System.out.println("method failed : "+method);
		
		for (String method : medt.getTestedProblematicMethods())
			System.out.println("method tested : "+method);
		

		
		medt.debugClass();
		
		int res = medt.regressions(failed);
		
		switch(res) {
		case 0: System.out.println("no regression"); break;
		case -1: System.out.println("regression"); break;
		default: System.out.println("improve program"); break;
		}

	}

}
