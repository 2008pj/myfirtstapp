package myfirstapp;

import myfirstapp.sys.App;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

	public ApplicationWorkbenchWindowAdvisor(
			IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(
			IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		Rectangle screenSize = Display.getDefault().getClientArea();
		configurer.setInitialSize(new Point(screenSize.width, screenSize.height));
		// ��ʾ����������
		configurer.setShowCoolBar(false);
		// ��ʾ״̬������
		configurer.setShowStatusLine(true);
		// ��ʾ�˵�����
		configurer.setShowMenuBar(true);
		// ��ʾ����������
		configurer.setShowProgressIndicator(true);
		// ����title
		configurer.setTitle(App.PDFREPORT_TITLE);

		// �����Զ�����
		Shell shell = configurer.getWindow().getShell();
		//shell.setBounds(0, 0, 500, 200);
		
		Rectangle frameSize = shell.getBounds();
//		shell.setLocation((screenSize.width - frameSize.width) / 2,
//				(screenSize.height - frameSize.height) / 2);
		shell.setLocation(0,0);

	}

}
