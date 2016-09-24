package myfirstapp.ui;

import myfirstapp.sys.App;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

public class MenuList extends ViewPart {
	Tree tree;
	String creatIndexText = "创建索引";
	String singleFileCompareText = "单文件查重";
	String multiFileCompareText = "多文件查重";
	public MenuList() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		final Tree tree = new Tree(parent, SWT.V_SCROLL);
		tree.setHeaderVisible(false);

//		TreeItem creatIndexItem = new TreeItem(tree, 0);
//		creatIndexItem.setImage(SWTResourceManager.getImage(MenuList.class, "/img/outline.png"));
//		creatIndexItem.setText(creatIndexText);

		TreeItem singleFileCompareItem = new TreeItem(tree, 0);
		singleFileCompareItem.setImage(SWTResourceManager.getImage(MenuList.class, "/img/page.png"));
		singleFileCompareItem.setText(singleFileCompareText);

		TreeItem multiFileCompareItem = new TreeItem(tree, 0);
		multiFileCompareItem.setImage(SWTResourceManager.getImage(MenuList.class, "/img/page_copy.png"));
		multiFileCompareItem.setText(multiFileCompareText);

		tree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				TreeItem[] selection = tree.getSelection();
				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();
				try {
					String itemText = selection[0].getText();

					if (itemText.equals(singleFileCompareText)) {
						
						window.getActivePage().showView(
								App.App_view_singleFileCompareView_ID);
					} else if (itemText.equals(multiFileCompareText)) {
						window.getActivePage().showView(
								App.App_view_multiFileCompareView_ID);
					}
				} catch (PartInitException s) {
					// TODO Auto-generated catch block
					s.printStackTrace();
				}

			}
		});

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
