package com.linticator.view.console;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;

import com.linticator.actions.ShowConsoleAction;

public class ShowConsolePageParticipant implements IConsolePageParticipant {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public void init(IPageBookViewPage page, IConsole console) {
		IToolBarManager mgr = page.getSite().getActionBars().getToolBarManager();
		mgr.add(new ShowConsoleAction());
	}

	@Override
	public void dispose() {
	}

	@Override
	public void activated() {
	}

	@Override
	public void deactivated() {
	}

}
