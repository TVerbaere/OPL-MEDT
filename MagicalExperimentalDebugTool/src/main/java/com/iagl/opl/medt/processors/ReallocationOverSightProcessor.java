package com.iagl.opl.medt.processors;

import java.util.List;
import java.util.Set;

import com.iagl.opl.medt.MagicalExperimentalDebugTool;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;

public class ReallocationOverSightProcessor extends AbstractProcessor<CtClass> {
	
	@Override
	public boolean isToBeProcessed(CtClass element) {
		
		String name = MagicalExperimentalDebugTool.getTestedClass().getSimpleName();
		
		String packageName = MagicalExperimentalDebugTool.getTestedClass().getPackage().getName();
		
		if (name.equals(element.getSimpleName()) && packageName.equals(element.getPackage().getQualifiedName()))
			return true;
		else
			return false;
		
	}

	public void process(CtClass element) {
		
		Set<CtMethod> methods = element.getAllMethods();
		
		Set<String> toSpoon = MagicalExperimentalDebugTool.getTestedProblematicMethods();
		
		for (CtMethod m : methods) {
			
			if (toSpoon.contains(m.getSimpleName())) {
				Filter<CtInvocation> filter = new TypeFilter(CtInvocation.class);
				List<CtInvocation> invocations = m.getElements(filter);
				
				for (CtInvocation invocation : invocations) {
	
					if (invocation.getParent() instanceof CtBlock) {
						
						if (correctFormat(invocation)) {
						
							String new_code = String.format("%s = %s", invocation.getTarget().toString(),
									invocation.toString());
													
							CtCodeSnippetStatement newStatement = getFactory().Core().createCodeSnippetStatement();
							newStatement.setValue(new_code);
							
							invocation.replace(newStatement);
						}
					}
					
				}
				
			}			
			
		}
				
	}

	private boolean correctFormat(CtInvocation invocation) {
				
		String name = invocation.getExecutable().getSimpleName();
		String type = invocation.getType().getQualifiedName();
				
		if (!type.equals("java.lang.String"))
			return false;
		
		if (name.equals("concat"))
			return true;
		
		if (name.equals("substring"))
			return true;
		
		if (name.equals("replace"))
			return true;
		
		if (name.equals("replaceAll"))
			return true;
		
		if (name.equals("replaceFirst"))
			return true;
		
		if (name.equals("subSequence"))
			return true;
		
		if (name.equals("toLowerCase"))
			return true;
		
		if (name.equals("toUpperCase"))
			return true;

		return false;
	}

}
