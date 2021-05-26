package com.linticator.astquickfixes;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;

import com.linticator.Linticator;
import com.linticator.astquickfixes.addreturnstatement.AddReturnStatement;
import com.linticator.astquickfixes.declareconst.DeclareMemberFunctionConst;
import com.linticator.astquickfixes.declareconst.DeclareRefParameterConst;
import com.linticator.astquickfixes.declarevirtual.DeclareVirtual;
import com.linticator.astquickfixes.mixedrelational.CastRelationalExpressionToMatchingType;
import com.linticator.astquickfixes.removeunused.RemoveUnusedVariable;
import com.linticator.astquickfixes.typemismatch.CastAssignedExpression;

public class ViolationResolutionGenerator implements IMarkerResolutionGenerator {

	private static final Map<Integer, Set<Class<?>>> QUICK_FIXES;
	static {
		final Map<Integer, Set<Class<?>>> quickFixes = new HashMap<Integer, Set<Class<?>>>();
		addResolution(quickFixes, 1764, DeclareRefParameterConst.class);
		addResolution(quickFixes, 1762, DeclareMemberFunctionConst.class);
		addResolution(quickFixes, 529, RemoveUnusedVariable.class);
		addResolution(quickFixes, 533, AddReturnStatement.class);
		addResolution(quickFixes, 574, CastRelationalExpressionToMatchingType.class);
		addResolution(quickFixes, 64, CastAssignedExpression.class);
		addResolution(quickFixes, 1093, DeclareVirtual.class);
		QUICK_FIXES = Collections.unmodifiableMap(quickFixes);
	}

	@Override
	public IMarkerResolution[] getResolutions(final IMarker marker) {
		final int code = marker.getAttribute(IMarker.PROBLEM, 0);
		return getResolutionsForCode(marker, code);
	}

	private IMarkerResolution[] getResolutionsForCode(final IMarker marker, final int code) {
		final Set<IMarkerResolution> resolutions = new LinkedHashSet<IMarkerResolution>();
		final Set<Class<?>> quickFixes = QUICK_FIXES.get(code);
		if (quickFixes != null) {
			for (final Class<?> c : quickFixes) {
				try {
					final Constructor<?> constructor = c.getConstructor(IMarker.class, int.class);
					resolutions.add((IMarkerResolution) constructor.newInstance(marker, code));
				} catch (final Exception e) {
					Linticator.getDefault().handleError(ViolationResolutionGenerator.class.getName(), e);
				}
			}
		}
		return resolutions.toArray(new IMarkerResolution[resolutions.size()]);
	}

	private static void addResolution(final Map<Integer, Set<Class<?>>> quickFixes, final int code,
			final Class<?> markerResolution) {
		if (quickFixes.get(code) == null) {
			quickFixes.put(code, new LinkedHashSet<Class<?>>());
		}
		quickFixes.get(code).add(markerResolution);
	}

}