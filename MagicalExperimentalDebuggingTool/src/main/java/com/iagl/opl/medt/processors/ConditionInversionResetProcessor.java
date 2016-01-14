package com.iagl.opl.medt.processors;

import java.util.List;

import com.iagl.opl.medt.MagicalExperimentalDebuggingTool;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;

public class ConditionInversionResetProcessor extends AbstractProcessor<CtMethod> {
	
	@Override
	public boolean isToBeProcessed(CtMethod element)  {

		//We get the name of the tested class
		String name = MagicalExperimentalDebuggingTool.getTestedClass().getSimpleName();

		// we get the package of the tested class
		String packageName = MagicalExperimentalDebuggingTool.getTestedClass().getPackage().getName();

		//if the name and the package of the tested class is same as the class of the method to be processed then we can start the processor, else we pass to the next method
		if (name.equals(element.getSimpleName()) && packageName.equals(((CtClass)element.getParent()).getPackage().getQualifiedName()))
			return true;
		//if the name of the method is the same as the current_method, we can start else passe to the next method
		else if (MagicalExperimentalDebuggingTool.getCurrentMethod().equals(element.getSimpleName()))
			return true;
		else
			return false;

	}

	/**
	 *
	 * for each method, we look for the method to spoon
	 * 
	 * 
	 */
	public void process(CtMethod element) {
		
		// For the method to spoon, get all conditions thanks to a filter
		Filter<CtIf> filter = new TypeFilter(CtIf.class);
		List<CtIf> ifs = element.getElements(filter);

		int i = 0;
		
		for (CtIf _if : ifs) {

			i++;
			
			// we check if the candidate is concerned by the change
			if (MagicalExperimentalDebuggingTool.getActualPermutation().x <= i &&
					MagicalExperimentalDebuggingTool.getActualPermutation().y >= i) {
					
				// we check if the candidate is concerned by the change
				if (MagicalExperimentalDebuggingTool.getActualPermutation().x <= i &&
					MagicalExperimentalDebuggingTool.getActualPermutation().y >= i) {
					
					
					//we try to change (inverse) the condition
					String new_code = _if.getCondition().toString().substring(2, _if.getCondition().toString().length()-1);
			
					CtCodeSnippetExpression<Boolean> newExpression = getFactory().Core().createCodeSnippetExpression();
					newExpression.setValue(new_code);
							
					_if.setCondition(newExpression);
				}
			
			}
		}

	}			

}
