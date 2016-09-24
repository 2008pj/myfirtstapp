package myfirstapp.ui;

import java.util.List;

import myfirstapp.eventListener.Plugin;
import myfirstapp.sysdeal.CopiedFileSentents;
import myfirstapp.sysdeal.CopiedFilesTool;
import myfirstapp.sysdeal.CopiedSentent;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;

public class Detail extends ViewPart  implements IPropertyChangeListener {
	Table table;
	Browser browser;

	int fileIndex;//
	public Detail() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(2, true));// 内层布局分为1列，宽度等分
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true)); // 本组件水平对齐，上下对齐，高度自适应，宽度填满父容器
		table = new Table(parent, SWT.BORDER | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setHeaderVisible(true);
		TableColumn tc1 = new TableColumn(table, SWT.CENTER);
		TableColumn tc2 = new TableColumn(table, SWT.LEFT);
		TableColumn tc3 = new TableColumn(table, SWT.LEFT);
		tc2.setText("涉嫌抄袭句子");
		tc3.setText("出处");
		tc1.setWidth(30);
		tc2.setWidth(400);
		tc3.setWidth(100);
		table.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				int index = Integer.parseInt(table.getItem(
						table.getSelectionIndex()).getText(0));
				
				List<CopiedFileSentents> list = CopiedFilesTool.getInstance()
						.getFilesList();
				CopiedFileSentents c=list.get(fileIndex);
				List<CopiedSentent> l2=c.getCopiedStentsList();
				
				browser.setText(l2.get(index).getHighlighterString());

			}
		});

		browser = new Browser(parent, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// 注册事件
		Plugin.getInstance().addPropertyChangeListener(this);

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * 监听事件处理
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals("HelloRcpEvent")) {
			Object val = event.getNewValue();
			fileIndex=Integer.parseInt((String) val)-1;
			table.removeAll();
			List<CopiedFileSentents> list = CopiedFilesTool.getInstance()
					.getFilesList();
			CopiedFileSentents c=list.get(fileIndex);
			List<CopiedSentent> l2=c.getCopiedStentsList();
			 TableItem item;
			 int tableIndex = 0;
			 for (final CopiedSentent sentent : l2) {
			 item = new TableItem(table, SWT.None);
			 item.setText(0, tableIndex + "");
			 item.setText(1, sentent.getOriginalSentents());
			 item.setText(2, sentent.getFileName());
			 tableIndex++;
			 }
		}

	}

}
