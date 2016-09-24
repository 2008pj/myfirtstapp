package myfirstapp.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.Document;

import lucene_3_tika.IndexFile;
import lucene_3_tika.SearchFiles;
import myfirstapp.eventListener.Plugin;
import myfirstapp.sys.App;
import myfirstapp.sysdeal.CopiedFileSentents;
import myfirstapp.sysdeal.CopiedFilesTool;
import myfirstapp.sysdeal.CopiedSentent;
import myfirstapp.sysdeal.FromInfo;
import myfirstapp.sysdeal.PropertiesReader;
import myfirstapp.sysdeal.Tool;

import org.apache.lucene.analysis.Analyzer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.eclipse.wb.swt.SWTResourceManager;

public class MultiFileCompare extends ViewPart {
	Text destDirectoryPathText;
	Button openDirectoryButton;
	Button btnCheckButton;
	Button startButton;
	String destDirectoryStr;
	Table resultTable;// 检查结果Table
	Table fromTable;// 出处Table
	TableItem item;
	int tableIndex = 0;
	String resultStr;
	Path directoryPath;
	Composite composite;
	DecimalFormat df = new DecimalFormat("0.0");// 格式化小数，不足的补0
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdfFileNameDate = new SimpleDateFormat("MM-dd HH-mm");
	SimpleDateFormat sdf2 = new SimpleDateFormat("YYYY/MM/dd");

	JTextPane editorPane;// 实例化一个文本编辑的控件
	Button addToLibraryButton;// 添加入库按钮
	Button exportReportButton;// 导出报告按钮
	ArrayList myListeners = new ArrayList();
	Tool tool = new Tool();
	List<Map> resultMapList = new LinkedList<>();
	List sumInfonList = new LinkedList<>();
	List<FromInfo> fromInfoList = new LinkedList<>();
	int srcDocNum = 0;

	public MultiFileCompare() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		composite = parent;
		parent.setLayout(new GridLayout(1, false));
		creatSelectDirectoryGroup();
		creatMiddleGroup(parent);
		creatOptionButtons();

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * 创建文件夹选择组件
	 * 
	 * @param parent
	 */
	public void creatSelectDirectoryGroup() {
		Group outerGroup = new Group(composite, SWT.NONE);
		outerGroup.setLayout(new GridLayout(4, true));// 内层布局分为3列，宽度等分
		outerGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false)); // 本组件水平对齐，上下对齐，高度自适应，宽度填满父容器
		outerGroup.setText("多文件检测");

		destDirectoryPathText = new Text(outerGroup, SWT.BORDER | SWT.READ_ONLY);
		destDirectoryPathText.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, true));// 本组件水平对齐，上下对齐，高度自适应，宽度填满父容器

		openDirectoryButton = new Button(outerGroup, SWT.NONE);
		openDirectoryButton.setImage(SWTResourceManager.getImage(
				MultiFileCompare.class, "/img/folder.png"));
		openDirectoryButton.setText(" 选择目录...");
		openDirectoryButton.setLayoutData(new GridData(SWT.CENTER, SWT.FILL,
				true, // 本组件靠左对齐，上下对齐，高度填满父容器，宽度填满父容器
				true));
		openDirectoryButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dd = new DirectoryDialog(composite.getShell());
				dd.setText("Open");
				dd.setFilterPath("C:/");
				destDirectoryStr = dd.open();
				// 显示文件名
				if (destDirectoryStr != null) {
					destDirectoryPathText.setText(destDirectoryStr);
					startButton.setEnabled(true);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnCheckButton = new Button(outerGroup, SWT.CHECK);
		btnCheckButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				false, false, 1, 1));
		btnCheckButton.setText("自动入库");
		btnCheckButton.setSelection(true);
		btnCheckButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (btnCheckButton.getSelection()) {
					addToLibraryButton.setVisible(false);
				} else {
					addToLibraryButton.setVisible(true);
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		/***************************************************
		 *                     开始按钮功能
		 ***************************************************/
		
		startButton = new Button(outerGroup, SWT.NONE);
		startButton.setImage(SWTResourceManager.getImage(
				MultiFileCompare.class, "/img/play_blue.png"));
		startButton
				.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true, true)); // 本组件靠右对齐，上下对齐，高度填满父容器，宽度填满父容器
		startButton.setText("   开始    ");
		startButton.setEnabled(false);
		startButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				directoryPath = Paths.get(destDirectoryStr);

				/*
				 * 遍历文件总数
				 */
				srcDocNum = tool.getDirectFileNum(directoryPath);
				try {
					tableIndex = 0; // 表格索引清0
					resultTable.removeAll(); // 清除表格内容
					Job job = new Job("多文件对比") {
						@Override
						protected IStatus run(IProgressMonitor monitor) {
							// Set total number of work units
							monitor.beginTask("正在进行多文件对:", srcDocNum);
							List<File> list = new LinkedList<>();
							tool.filesToList(directoryPath, list);
							resultMapList.clear();
							for (int i = 0; i < list.size(); i++) {
								if (monitor.isCanceled())
									return Status.CANCEL_STATUS;
								File f = (File) list.get(i);
								monitor.subTask("(" + i + "/" + srcDocNum
										+ ")  " + f.toString());
								/*
								 * 调用核心比对方法
								 */
								beginCompare(f.toString());

								monitor.worked(1);
							}

							return Status.OK_STATUS;
						}
					};

					job.setUser(true);
					job.schedule();

					addToLibraryButton.setEnabled(true); // 添加入库按钮可用
					// srcDocNum=0;
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	};

	private void creatMiddleGroup(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(2, true));
		creatShowResultGroup(composite);
		creatShowOriginalTextGroup(composite);

	}
	/**
	 * 构建查询结果表格内容
	 */
	private void creatShowResultGroup(final Composite composite) {
		// composite.setLayout(new GridLayout(1, true));
		Composite composite2 = new Composite(composite, SWT.NONE);
		composite2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite2.setLayout(new GridLayout(1, true));
		
		resultTable = new Table(composite2, SWT.BORDER | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		resultTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TableColumn tc1 = new TableColumn(resultTable, SWT.CENTER);
		TableColumn tc2 = new TableColumn(resultTable, SWT.LEFT);
		TableColumn tc3 = new TableColumn(resultTable, SWT.LEFT);
		TableColumn tc4 = new TableColumn(resultTable, SWT.CENTER);
		tc2.setText("路径");
		tc3.setText("重复率");
		tc3.addSelectionListener(new SelectionListener() {
			 boolean isAscend = true; // 按照升序排序
			@Override
			public void widgetSelected(SelectionEvent e) {
					    
					     TableItem[] items=resultTable.getItems();
					     Collator comparator =Collator.getInstance(Locale.getDefault());
					     for (int i = 1; i < items.length; i++) {
					         String str2value = items[i].getText(2);
					         for (int j = 0; j < i; j++) {
						         String str1value = items[j].getText(2);
						         boolean isLessThan = comparator.compare(str2value, str1value)<0;     
						         if ((isAscend && isLessThan)
						           || (!isAscend && !isLessThan)){
						          String[] values = getTableItemText(resultTable, items[i]);
						          Object obj = items[i].getData();
						          items[i].dispose();
						          TableItem item = new TableItem(resultTable, SWT.NONE, j);
						          item.setText(values);
						          item.setData(obj);
						          items = resultTable.getItems();
						          break;
						         }
						        }
					     }
					     resultTable.setSortColumn(resultTable.getColumn(2));
					     resultTable.setSortDirection((isAscend ? SWT.UP : SWT.DOWN));
					     isAscend = !isAscend;
					     
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			});
		tc4.setText("结果");
		tc1.setWidth(30);
		tc2.setWidth(350);
		tc3.setWidth(60);
		tc4.setWidth(100);
		resultTable.setHeaderVisible(true);

		/*
		 * 构建表格右键菜单
		 */
		// final Menu menuTable = new Menu(resultTable);
		// resultTable.setMenu(menuTable);
		// MenuItem miTest = new MenuItem(menuTable, SWT.NONE);
		// miTest.setText("详细信息");
		// miTest.addSelectionListener(al);
		// miTest = new MenuItem(menuTable, SWT.NONE);
		// miTest.setText("原文相似查看");
		// miTest.addSelectionListener(ShowOriginalSameSentents);
		// resultTable.addListener(SWT.MenuDetect, new Listener() {
		// @Override
		// public void handleEvent(Event event) {
		// if (resultTable.getSelectionCount() <= 0) {
		// event.doit = false;
		// } else {
		// String string = "";
		// TableItem[] selection = resultTable.getSelection();
		// for (int i = 0; i < selection.length; i++)
		// string += selection[i].getText(2) + " ";
		// System.out.println("Selection={" + string + "}");
		// }
		// }
		// });

		resultTable.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				/*
				 * 原文相似查看窗口高亮显示重复语句
				 */
				Map map = (Map) resultMapList.get(resultTable
						.getSelectionIndex());
				editorPane.setDocument((Document) map.get("swingDoc"));

				/*
				 * 出处窗口显示所有库文件来源
				 */
				fromInfoList = (List) sumInfonList.get(resultTable
						.getSelectionIndex());
				FromInfo fromInfo;
				fromTable.removeAll();
				for (int i = 0; i < fromInfoList.size(); i++) {
					fromInfo = fromInfoList.get(i);
					TableItem item = new TableItem(fromTable, SWT.NONE);
					item.setText(new String[] {
							i + "",
							fromInfo.getScore() + "",
							fromInfo.getFrom(),
							sdf2.format(new Date(Long.parseLong(fromInfo
									.getCreatDate()))) });
					fromTable.update();
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		/**
		 * 构建出处表格
		 */
		fromTable = new Table(composite2, SWT.BORDER | SWT.V_SCROLL
				| SWT.FULL_SELECTION);
		fromTable.setHeaderVisible(true);
		fromTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TableColumn tc11 = new TableColumn(fromTable, SWT.CENTER);
		TableColumn tc12 = new TableColumn(fromTable, SWT.LEFT);
		TableColumn tc13 = new TableColumn(fromTable, SWT.LEFT);
		TableColumn tc14 = new TableColumn(fromTable, SWT.LEFT);
		tc12.setText("相似权重");
		tc13.setText("出处");
		tc14.setText("入库时间");
		tc11.setWidth(30);
		tc12.setWidth(100);
		tc13.setWidth(350);
		tc14.setWidth(100);
		fromTable.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// new PopWindow().Pop(composite, fromInfoList, fromTable);

				String srcFileName = tool.getTitleName(resultTable.getItem(
						resultTable.getSelectionIndex()).getText(1));
				String fromFileName = tool.getTitleName(fromTable.getItem(
						fromTable.getSelectionIndex()).getText(2));
				FromInfo fromInfo = (FromInfo) fromInfoList.get(fromTable
						.getSelectionIndex());
				String creatDate = fromTable.getItem(
						fromTable.getSelectionIndex()).getText(3);
				String highlighterStr = fromInfo.getHighlighters();
				new PopWindow().Pop(composite, srcFileName, fromFileName,
						highlighterStr, creatDate);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void creatShowOriginalTextGroup(Composite composite) {
		Group outerGroup3 = new Group(composite, SWT.NONE);
		outerGroup3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		outerGroup3.setLayout(new GridLayout(1, true));
		outerGroup3.setText("原文相似查看");
		Composite comp = new Composite(outerGroup3, SWT.EMBEDDED);// SWT.EMBEDDED必须
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final java.awt.Frame frame = SWT_AWT.new_Frame(comp);// 只能放Frame不能放JFrame
		editorPane = new JTextPane();// 实例化一个文本编辑的控件
		JScrollPane scl = new JScrollPane(editorPane);
		// editorPane.setDocument(doc);
		frame.add(scl);

	}

	/**
	 * 其他操作按钮区（导出报告，添加入库）
	 * 
	 * @param parent
	 */
	private void creatOptionButtons() {
		Composite buttonsComposite = new Composite(composite, SWT.NONE);
		buttonsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));
		buttonsComposite.setLayout(new GridLayout(2, true));

		addToLibraryButton = new Button(buttonsComposite, SWT.NONE);
		addToLibraryButton.setText("添加入库");
		addToLibraryButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				true, // 本组件靠左对齐，上下对齐，高度填满父容器，宽度填满父容器
				true));
		addToLibraryButton.setEnabled(false);
		addToLibraryButton.setVisible(false);// 默认不可见
		addToLibraryButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				TableItem[] items = resultTable.getItems();
				final List<String> pathStrs = new ArrayList<>();
				for (TableItem item : items) {
					if (item.getText(3).equals(App.RESULTSTRING_PASS)) {
						pathStrs.add(item.getText(1));
					}
				}
				final Analyzer analyzer = App.App_lucene_analyzer;

				ProgressMonitorDialog pmd = new ProgressMonitorDialog(composite
						.getShell());
				IRunnableWithProgress rwp = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						// totalWork为IProgressMonitor.UNKNOWN时表示不知道任务的总量
						// 将在进度条上显示一个来回移动的进度条
						monitor.beginTask("添加入库", IProgressMonitor.UNKNOWN);
						for (String temp : pathStrs) {
							System.out.println(temp);
							monitor.subTask(temp);
							// 这段代码实际上会被放在UI线程中执行
							IndexFile index = new IndexFile(temp, null);
							index.index(App.INDEX_MODE_APPEND);
							Thread.sleep(5000);

						}
						monitor.done();
					}
				};
				try {
					pmd.run(true, false, rwp);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				MessageBox messageBox = new MessageBox(composite.getShell(),
						SWT.ICON_INFORMATION | SWT.OK);
				messageBox.setText("提示");
				messageBox.setMessage("加入索引库成功！");
				messageBox.open();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		exportReportButton = new Button(buttonsComposite, SWT.NONE);
		exportReportButton.setImage(SWTResourceManager.getImage(
				MultiFileCompare.class, "/img/page_white_go.png"));
		exportReportButton.setText("导出报告");
		exportReportButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				true, // 本组件靠左对齐，上下对齐，高度填满父容器，宽度填满父容器
				true));
		exportReportButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				final DirectoryDialog dd = new DirectoryDialog(composite
						.getShell());
				final String basePath = dd.open();
				ProgressMonitorDialog pmd = new ProgressMonitorDialog(composite
						.getShell());
				IRunnableWithProgress rwp = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						// totalWork为IProgressMonitor.UNKNOWN时表示不知道任务的总量
						// 将在进度条上显示一个来回移动的进度条
						monitor.beginTask("正在导出" + "", IProgressMonitor.UNKNOWN);
						composite.getDisplay().syncExec(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									saveReport(basePath);
								} catch (DocumentException | IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});

						monitor.done();

					}
				};
				try {
					pmd.run(true, false, rwp);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	public void compareByDirectory(final IProgressMonitor monitor)
			throws Exception {
		if (Files.isDirectory(directoryPath)) {
			Files.walkFileTree(directoryPath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					beginCompare(file.toString());
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}

	/**
	 * 执行单文件比对
	 * 
	 * @param parent
	 */
	public void beginCompare(final String filePathStr) {
		// monitor.subTask("正在处理:"+filePathStr);
		// new Thread() {
		// public void run() {
		composite.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				tableIndex++; // 索引号+1
				item = new TableItem(resultTable, SWT.NONE);
				item.setText(0, tableIndex + "");
				item.setText(1, filePathStr);

				Map map = tool.paseDocument(filePathStr, new JTextPane());
				resultMapList.add(map);
				int wordCount = (int) map.get("countNum");
				int copiedWordNum = (int) map.get("copiedWordNum");
				float percent = (float) copiedWordNum / wordCount * 100;
				item.setText(2, df.format(percent));
				/*
				 * 检测通过（重复百分比小于设定值）
				 */
				if (percent < Float.parseFloat(PropertiesReader.getInstance()
						.readKey("passPercent"))) { // 通过（重复百分比小于设定值）
					item.setText(3, App.RESULTSTRING_PASS);
					/* 
					 * 判断自动入库按钮是否被选中，如果选中，则进行入库操作
					 */
					if (btnCheckButton.getSelection()) {
						IndexFile index = new IndexFile(filePathStr, null);
						index.index(App.INDEX_MODE_APPEND);
					}
				} else { 
				/*
				 * 涉嫌抄袭（重复百分比超过设定值）
				 */
					item.setText(3, App.RESULTSTRING_FAIL);
					item.setForeground(3,
							Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				}
				CopiedFileSentents copiedFileSentents = (CopiedFileSentents) map
						.get("copiedFileSentents");
				CopiedFilesTool.getInstance().addFile(copiedFileSentents);

				/*
				 * 执行第二次全文对比,返回相似文章List
				 */
				List fromInfoList = new SearchFiles().search(filePathStr);
				sumInfonList.add(fromInfoList);

			}
		});
		// }
		// }.start();
		// monitor.worked(1);
	}

	// 菜单事件
	SelectionListener al = new SelectionListener() {
		@Override
		public void widgetSelected(SelectionEvent e) {

			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			try {
				window.getActivePage().showView(App.App_view_detail_ID);
			} catch (PartInitException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// MenuItem item = (MenuItem) e.widget;
			TableItem item = resultTable.getItem(resultTable
					.getSelectionIndex());
			// 传递table索引值
			Plugin.getInstance().initAndInvoke(myListeners, item.getText(0));
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub

		}
	};
	/**
	 * 显示原文相似监听事件
	 */
	SelectionListener ShowOriginalSameSentents = new SelectionListener() {

		@Override
		public void widgetSelected(SelectionEvent e) {

			Map map = tool.paseDocument(
					resultTable.getItem(resultTable.getSelectionIndex())
							.getText(1), editorPane);

		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub

		}

	};

	/**
	 * 构建详细信息窗口GUI
	 * 
	 * @param title
	 */
	void ShowDetailWindow(String title) {
		// 创建新的Shell
		final Shell detailShell = new Shell(composite.getShell(), SWT.RESIZE
				| SWT.CLOSE | SWT.MAX | SWT.MIN | SWT.BORDER_SOLID);

		detailShell.setSize(1000, 700);
		detailShell.setText(title);
		detailShell.setLayout(new GridLayout(1, false));
		// 居中
		Rectangle screenSize = Display.getDefault().getClientArea();
		Rectangle frameSize = detailShell.getBounds();
		detailShell.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);

		new Thread() {
			public void run() {
				detailShell.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						Label label = new Label(detailShell, SWT.None);
						label.setText(resultTable.getSelection()[0].getText(1));

					}
				});

			}
		}.start();
		detailShell.open();
	}

	/**
	 * 导出报告
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	@SuppressWarnings("unchecked")
	void saveReport(String basePath) throws DocumentException, IOException {

		com.itextpdf.text.Document document = new com.itextpdf.text.Document(
				PageSize.A4, 50, 50, 50, 50);
		PdfWriter writer;

		try {
			writer = PdfWriter.getInstance(document,
					new FileOutputStream(basePath + "\\多文件检测报告_"
							+ sdfFileNameDate.format(new Date()) + ".pdf"));
		} catch (FileNotFoundException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		document.open();
		// 标题字体
		Font titleFont = new Font(BaseFont.createFont(
				"C:/Windows/Fonts/simkai.ttf", BaseFont.IDENTITY_H,
				BaseFont.NOT_EMBEDDED), 20);
		// 基本字体
		Font baseFont = new Font(BaseFont.createFont(
				"C:/Windows/Fonts/simfang.ttf", BaseFont.IDENTITY_H,
				BaseFont.NOT_EMBEDDED), 12);
		// 红色字体
		Font redFont = new Font();
		redFont = new Font(BaseFont.createFont("C:/Windows/Fonts/simfang.ttf",
				BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), 12);
		redFont.setColor(BaseColor.RED);

		// 报告标题
		creatPdfTile(document, titleFont);
		// 报告生成时间
		creatPDFexportDate(document, baseFont);
		// 对比汇总信息
		creatSummeryTable(document, baseFont, redFont);
		// 详细信息
		creatFromInfo(document, baseFont);

		document.close();

	}

	/**
	 * 报告标题
	 * 
	 * @param document
	 * @param titleFont
	 */
	public void creatPdfTile(com.itextpdf.text.Document document, Font titleFont) {
		Paragraph pdfTitle = new Paragraph(App.PDFREPORT_TITLE, titleFont);
		pdfTitle.setAlignment(1);
		pdfTitle.setSpacingAfter(30);
		try {
			document.add(pdfTitle);
		} catch (Exception e) {
		}
	}

	/**
	 * 报告生成时间
	 * 
	 * @param document
	 * @param baseFont
	 */
	public void creatPDFexportDate(com.itextpdf.text.Document document,
			Font baseFont) {
		Paragraph creatDate = new Paragraph("报告生成时间：" + sdf.format(new Date()),
				baseFont);
		creatDate.setSpacingAfter(20);
		try {
			document.add(creatDate);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 汇总信息
	 * 
	 * @param document
	 * @param baseFont
	 * @param redFont
	 */
	public void creatSummeryTable(com.itextpdf.text.Document document,
			Font baseFont, Font redFont) {
		TableItem[] items = resultTable.getItems();
		float[] widths1 = { 0.1f, 0.6f, 0.1f, 0.2f };
		PdfPTable table = new PdfPTable(widths1);
		table.getDefaultCell().setMinimumHeight(40);
		table.setWidthPercentage(100);
		table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

		table.addCell(new Phrase("序号", baseFont));
		table.addCell(new Phrase("名称", baseFont));
		table.addCell(new Phrase("重复率", baseFont));
		table.addCell(new Phrase("结果", baseFont));
		for (TableItem item : items) {
			table.addCell(new Phrase(item.getText(0), baseFont));
			table.addCell(new Phrase(item.getText(1), baseFont));
			table.addCell(new Phrase(item.getText(2), baseFont));
			if (item.getText(3).equals(App.RESULTSTRING_PASS)) {
				table.addCell(new Phrase(item.getText(3), baseFont));
			} else {
				table.addCell(new Phrase(item.getText(3), redFont));
			}
		}
		try {
			document.add(table);

		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * 单文件出处列表
	 */
	@SuppressWarnings("unchecked")
	public void creatFromInfo(com.itextpdf.text.Document document, Font baseFont) {
		TableItem[] items = resultTable.getItems();
		Paragraph fileName;
		FromInfo fromInfo;
		for (int i = 0; i < items.length; i++) {
			fileName = new Paragraph((i + 1) + "、" + items[i].getText(1),
					baseFont);
			fileName.setAlignment(Element.ALIGN_LEFT);
			fileName.setSpacingBefore(30);
			fileName.setSpacingAfter(10);
			fromInfoList = (List<FromInfo>) sumInfonList.get(i);
			float[] widths1 = { 0.1f, 0.2f, 0.6f, 0.2f };
			PdfPTable table = new PdfPTable(widths1);
			// table.getDefaultCell().setMinimumHeight(40);
			table.setWidthPercentage(100);
			table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

			table.addCell(new Phrase("序号", baseFont));
			table.addCell(new Phrase("相似权重", baseFont));
			table.addCell(new Phrase("库文件", baseFont));
			table.addCell(new Phrase("入库时间", baseFont));
			for (int ii = 0; ii < fromInfoList.size(); ii++) {
				fromInfo = fromInfoList.get(ii);

				table.addCell(new Phrase((ii + 1) + "", baseFont));
				table.addCell(new Phrase(fromInfo.getScore() + "", baseFont));
				table.addCell(new Phrase(tool.getTitleName(fromInfo.getFrom()),
						baseFont));
				table.addCell(new Phrase(sdf2.format(new Date(Long
						.parseLong(fromInfo.getCreatDate()))), baseFont));
			}

			try {
				document.add(fileName);
				document.add(table);
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
	}
	public static String[] getTableItemText(Table table, TableItem item) {
	    int count =table.getColumnCount();
	    String[]strs = new String[count];
	    for (int i =0; i < count; i++) {
	    strs[i] = item.getText(i);
	    }
	    return strs;
	 }
}
