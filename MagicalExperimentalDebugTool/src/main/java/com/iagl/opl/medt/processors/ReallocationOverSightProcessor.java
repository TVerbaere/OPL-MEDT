package com.iagl.opl.medt.processors;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;

public class ReallocationOverSightProcessor extends AbstractProcessor<CtClass> {

	public void process(CtClass arg0) {
		System.out.println("spoon : "+arg0.getSimpleName());
		
	}

}
