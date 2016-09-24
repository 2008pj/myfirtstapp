package myfirstapp.action;

import myfirstapp.sys.App;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

public class CreatIndexAction extends Action {
private IWorkbenchWindow workbenchWindow;  
	
	public CreatIndexAction(IWorkbenchWindow window){
		this.workbenchWindow=window;
	}
	@Override
	public void run() {
		try {
			workbenchWindow.getActivePage().showView(
					App.App_view_creatIndex_ID);
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
