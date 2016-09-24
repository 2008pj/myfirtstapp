package myfirstapp.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;

import lucene_3_tika.IndexFile;
import myfirstapp.sys.App;
import myfirstapp.sysdeal.Tool;

import org.apache.lucene.analysis.Analyzer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;

public class CreatIndex extends ViewPart {
	Button selectDirectButton;
	Label showDirectLabel;
	Button creatIndexButton;
	Text showResultText;
	Tool tool = new Tool();
	int temp;

	public CreatIndex() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(final Composite parent) {
		GridLayout gl_parent = new GridLayout(3, false);
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		// gl_parent.horizontalSpacing = 3;
		parent.setLayout(gl_parent);
		// 显示库文件目录
		showDirectLabel = new Label(parent, SWT.BORDER);
		showDirectLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		showDirectLabel.setText("C:\\lucene\\docsPath");
		// 选择库文件按钮
		selectDirectButton = new Button(parent, SWT.NONE);
		selectDirectButton.setImage(SWTResourceManager.getImage(
				CreatIndex.class, "/img/folder.png"));
		selectDirectButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		selectDirectButton
				.setText("\u9009\u62E9\u5E93\u6587\u4EF6\u76EE\u5F55...");
		selectDirectButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dd = new DirectoryDialog(parent.getShell());
				showDirectLabel.setText(dd.open());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		// 调用lucene构建索引
		creatIndexButton = new Button(parent, SWT.NONE);
		creatIndexButton.setImage(SWTResourceManager.getImage(CreatIndex.class,
				"/img/outline.png"));
		creatIndexButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
				false, false, 1, 1));
		creatIndexButton.setText("构建索引");
		creatIndexButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final String docsPath = showDirectLabel.getText();
				final Analyzer analyzer = App.App_lucene_analyzer;
				showResultText.setText("");

				final Path path = Paths.get(docsPath);
				/*
				 * 遍历文件总数
				 */
				final List<File> list = new LinkedList<>();
				tool.filesToList(path, list);
				Job job = new Job("构建索引") {
					@Override
					protected IStatus run(final IProgressMonitor monitor) {
						// Set total number of work units
						monitor.beginTask("正在索引创建:", list.size());
						for (int i = 0; i < list.size(); i++) {
							temp=i;
							if (monitor.isCanceled())
								return Status.CANCEL_STATUS;
							final File f = list.get(i);
							monitor.subTask("(" + i + "/" + list.size() + ")  "
									+ f.toString());

							parent.getDisplay().syncExec(new Runnable() {
								@Override
								public void run() {
									// 这段代码实际上会被放在UI线程中执行
									IndexFile index = new IndexFile(f
											.toString(), showResultText);
									if(temp==0){
										index.index(App.INDEX_MODE_CREAT);
									}else{
										index.index(App.INDEX_MODE_APPEND);
									}
								}
							});
							monitor.worked(1);

						}
						parent.getDisplay().asyncExec(new Runnable() {
							@Override
							public void run() {
								MessageDialog.openInformation(
										parent.getShell(), "通知", "索引构建完毕");
							}
						});

						return Status.OK_STATUS;
					}
				};

				job.setUser(true);
				job.schedule();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		new Label(parent, SWT.NONE);
		// creatIndexButton.setEnabled(false);
		// 显示索引构建信息
		showResultText = new Text(parent, SWT.BORDER | SWT.READ_ONLY
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		showResultText.setBackground(SWTResourceManager
				.getColor(SWT.COLOR_WHITE));
		showResultText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				true, 3, 1));

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
