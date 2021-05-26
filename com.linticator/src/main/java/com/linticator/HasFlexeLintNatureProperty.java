package com.linticator;

import org.eclipse.core.expressions.PropertyTester;

import com.linticator.actions.RunFlexeLintHandler;


public class HasFlexeLintNatureProperty extends PropertyTester {

	public HasFlexeLintNatureProperty() {
	}

	@Override
	public boolean test(final Object receiver, final String property, final Object[] args, final Object expectedValue) {
		return !RunFlexeLintHandler.getCurrentlyActiveFlexeLintProjects().isEmpty();
	}
}
