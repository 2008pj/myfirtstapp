package myfirstapp.ui;

import java.util.List;

import myfirstapp.sysdeal.FromInfo;
import myfirstapp.sysdeal.Tool;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.wb.swt.SWTResourceManager;

public class PopWindow {
	Tool tool = new Tool();

	public void Pop(Composite composite, String srcFileName,
			String fromFileName, String highlighterStr, String creatDate) {
		Shell dialogShell = new Shell(composite.getShell());
		dialogShell.setText("库文件重复查看");
		dialogShell.setSize(805, 600);
		dialogShell.open();

		// 窗口自动居中
		Rectangle screenSize = Display.getDefault().getClientArea();
		Rectangle frameSize = dialogShell.getBounds();
		dialogShell.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);

		dialogShell.setLayout(new GridLayout(1, false));

		final Browser browser = new Browser(dialogShell, SWT.BORDER);
		browser.setBounds(0, 0, 800, 500);

		browser.setText("<h3>送检文件：" + srcFileName + "</h3><h3>库文件："
				+ fromFileName + "</h3><p style=\"text-align:right;\">入库时间:"
				+ creatDate + "</p>" + highlighterStr);
		// browser.setUrl("http://www.baidu.com");
		Button b = new Button(dialogShell, SWT.CENTER);
		b.setText("打印");
		b.setImage(SWTResourceManager.getImage(PopWindow.class,
				"/img/printer.png"));
		b.setBounds(0, 500, 100, 30);
		b.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.execute("javascript:window.print();");

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}
}
