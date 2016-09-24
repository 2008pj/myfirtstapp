package myfirstapp.action;

import myfirstapp.ui.PrameterSetDialog;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

public class ParameterSet extends Action implements IWorkbenchAction {
	private IWorkbenchWindow workbenchWindow;  
	
	public ParameterSet(IWorkbenchWindow window){
		this.workbenchWindow=window;
	}
	
	@Override
	public void run() {
		
		PrameterSetDialog psd=new PrameterSetDialog(workbenchWindow.getShell());
		psd.open();
//		super.run();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}


	


}
