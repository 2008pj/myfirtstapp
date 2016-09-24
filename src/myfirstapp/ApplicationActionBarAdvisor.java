package myfirstapp;

import myfirstapp.action.CreatIndexAction;
import myfirstapp.action.ParameterSet;
import myfirstapp.sysdeal.PropertiesReader;
import myfirstapp.ui.StatusBarContribution;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	private StatusBarContribution statusBarContribution;
	private Action parameterSetAction;//��������
	private Action creatIndexAction; //��������
	
	
	
    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    protected void makeActions(IWorkbenchWindow window) {
    	String msg="������������  Email��gorack@163.com";
    	statusBarContribution=new StatusBarContribution(msg);
    	statusBarContribution.setVisible(true);
    	
    	parameterSetAction=new ParameterSet(window);
    	parameterSetAction.setId("parameterSet");
    	parameterSetAction.setText("��������");
    	
    	creatIndexAction=new CreatIndexAction(window);
    	creatIndexAction.setId("indexManage");
    	creatIndexAction.setText("��������");
    	
    	register(parameterSetAction); 
    	register(creatIndexAction); 
    }

    protected void fillMenuBar(IMenuManager menuBar) {
    	MenuManager parameterSetMenu=new MenuManager("��������","parameterMenu");
    	parameterSetMenu.add(parameterSetAction);
    	
    	MenuManager indexManageMenu=new MenuManager("��������","parameterMenu");
    	indexManageMenu.add(creatIndexAction);
    	
    	menuBar.add(parameterSetMenu);
    	menuBar.add(indexManageMenu);
    }

	@Override
	protected void fillStatusLine(IStatusLineManager statusLine) {
		statusLine.add(statusBarContribution);
	}

	@Override
	protected void fillCoolBar(ICoolBarManager coolBar) {
	}
	
    
}
