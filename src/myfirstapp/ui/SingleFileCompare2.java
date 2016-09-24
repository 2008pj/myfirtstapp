package myfirstapp.ui;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import lucene_3_tika.IndexFile;
import myfirstapp.eventListener.Plugin;
import myfirstapp.sys.App;
import myfirstapp.sysdeal.CopiedFileSentents;
import myfirstapp.sysdeal.CopiedFilesTool;
import myfirstapp.sysdeal.CopiedSentent;
import myfirstapp.sysdeal.Tool;

import org.apache.lucene.analysis.Analyzer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.json.JSONObject;

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

public class SingleFileCompare2 extends ViewPart {
	Button openFileButton;
	Button startButton;
	Text destFilePath;
	Label showFilePathLabel;
	Label showResultLabel;
	Table table;
	Browser browser;
	JTextPane editorPane;// ʵ����һ���ı��༭�Ŀؼ�
	DefaultPieDataset dataset = new DefaultPieDataset();
	Label resultImglabel;// ��ʾ"���ӳ�Ϯ"��"ͨ��"��ͼƬlabel
	String resultString;// ������ʽ�����

	Button addToLibrary;// �����ⰴť
	Button showDetailButton;// �鿴��ϸ��ť
	Button exportReportButton;// �������水ť

	Tool tool = new Tool();

	DecimalFormat df = new DecimalFormat("0.0");// ��ʽ��С��������Ĳ�0
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat sdfFileNameDate = new SimpleDateFormat("MM-dd HH-mm");
	/*
	 * ���������õ��ı���
	 */
	String docFileName;// �ͼ��ļ�·���������ļ���
	JSONObject json;
	Map map;
	int paragraphCount; // ��������
	int wordCount; // ������
	int copiedSententsNum; // �ظ��ľ�������
	int copiedCount; // �ظ�������
	float percent; // ��Ϯ��
	ArrayList myListeners = new ArrayList();

	public SingleFileCompare2() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		creatSelectFileGroup(parent);
		creatMiddleGroup(parent);
		creatOptionButtons(parent);

	}

	private void creatMiddleGroup(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(2, true));

		creatShowResultGroup(composite);
		creatShowOriginalTextGroup(composite);
	}

	/**
	 * �ļ�ѡ������
	 * 
	 * @param parent
	 */
	public void creatSelectFileGroup(final Composite parent) {

		Group outerGroup = new Group(parent, SWT.NONE);
		outerGroup.setLayout(new GridLayout(3, true));// �ڲ㲼�ַ�Ϊ3�У���ȵȷ�
		outerGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false)); // �����ˮƽ���룬���¶��룬�߶�����Ӧ���������������
		outerGroup.setText("���ļ����");

		destFilePath = new Text(outerGroup, SWT.BORDER | SWT.READ_ONLY);
		destFilePath
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));// �����ˮƽ���룬���¶��룬�߶�����Ӧ���������������

		openFileButton = new Button(outerGroup, SWT.NONE);
		openFileButton.setText(" ѡ���ļ�...");
		openFileButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, // �����������룬���¶��룬�߶��������������������������
				true));
		openFileButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				FileDialog fd = new FileDialog(parent.getShell(), SWT.OPEN
						| SWT.MULTI);
				fd.setText("Open");
				fd.setFilterPath("C:/");
				fd.setFilterExtensions(App.vaildFileType);
				String path = fd.open();
				// ��ʾ�ļ���
				if (path != null) {
					docFileName = path;
					destFilePath.setText(path);
					startButton.setEnabled(true);
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		startButton = new Button(outerGroup, SWT.NONE);
		startButton
				.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true)); // ��������Ҷ��룬���¶��룬�߶��������������������������
		startButton.setText("   ��ʼ    ");
		startButton.setEnabled(false);
		startButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Image image = new Image(null, this.getClass()
						.getResourceAsStream("/img/comparing.png"));
				resultImglabel.setImage(image);
				beginCompare(parent);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * ��ѯ�����ʾ����
	 * 
	 * @param parent
	 */
	private void creatShowResultGroup(Composite parent) {
		Group outerGroup2 = new Group(parent, SWT.NONE);
		outerGroup2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		outerGroup2.setLayout(new GridLayout(2, true));// �ڲ㲼�ַ�Ϊ2�У���ȵȷ�
		outerGroup2.setText("�����");

		// �����ͼ����ʾ
		JFreeChart chart = ChartFactory.createPieChart(null, // chart_title
				dataset, // data
				true, // include legend
				true,//
				false//
				);
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setSectionPaint("��д��", Color.green);
		plot.setSectionPaint("�ظ���", Color.RED);
		plot.setSectionOutlinesVisible(false);
		plot.setNoDataMessage("No data available");
		plot.setCircular(false);
		plot.setLabelGap(0.02);

		ChartComposite frame1 = new ChartComposite(outerGroup2, SWT.RIGHT,
				chart, true);
		frame1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		frame1.setBounds(0, 0, 600, 400);

		// ��ʾ�Ƿ�ͨ��ͼƬ
		resultImglabel = new Label(outerGroup2, SWT.NONE);
		// resultImglabel.setText("�������ʾ��");
		Image image = new Image(null, this.getClass().getResourceAsStream(
				"/img/default.png"));

		resultImglabel.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, true,
				false));
		resultImglabel.setImage(image);

		// ������ļ�
		showFilePathLabel = new Label(outerGroup2, SWT.LEFT);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		showFilePathLabel.setLayoutData(gridData);
		// �����������ʾ
		showResultLabel = new Label(outerGroup2, SWT.LEFT);
		showResultLabel.setLayoutData(gridData);

	}

	/**
	 * ԭ�����ƾ��Ӹ�����ʾ��
	 * 
	 * @param parent
	 */
	private void creatShowOriginalTextGroup(Composite parent) {
		Group outerGroup3 = new Group(parent, SWT.NONE);
		outerGroup3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		outerGroup3.setLayout(new GridLayout(1, true));
		outerGroup3.setText("ԭ�Ĳ鿴");
		Composite comp = new Composite(outerGroup3, SWT.EMBEDDED);// SWT.EMBEDDED����
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final java.awt.Frame frame = SWT_AWT.new_Frame(comp);// ֻ�ܷ�Frame���ܷ�JFrame
		editorPane = new JTextPane();// ʵ����һ���ı��༭�Ŀؼ�
		JScrollPane scl = new JScrollPane(editorPane);
		// editorPane.setDocument(doc);
		frame.add(scl);

	}

	/**
	 * ����������ť�����������棬�����⣩
	 * 
	 * @param parent
	 */
	private void creatOptionButtons(final Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		composite.setLayout(new GridLayout(3, true));

		addToLibrary = new Button(composite, SWT.NONE);
		addToLibrary.setText("������");
		addToLibrary.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, // �����������룬���¶��룬�߶��������������������������
				true));
		addToLibrary.setEnabled(false);
		addToLibrary.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				final String docsPath = destFilePath.getText();

				ProgressMonitorDialog pmd = new ProgressMonitorDialog(parent
						.getShell());
				IRunnableWithProgress rwp = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						// totalWorkΪIProgressMonitor.UNKNOWNʱ��ʾ��֪�����������
						// ���ڽ���������ʾһ�������ƶ��Ľ�����
						monitor.beginTask("������", IProgressMonitor.UNKNOWN);
						monitor.subTask("���������ӣ���ȴ�����");

						final Analyzer analyzer = App.App_lucene_analyzer;
						// ��δ���ʵ���ϻᱻ����UI�߳���ִ��
						IndexFile index = new IndexFile(
								docsPath, null);
						index.index(App.INDEX_MODE_APPEND);
						monitor.done();

					}
				};
				try {
					pmd.run(true, false, rwp);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				MessageBox messageBox = new MessageBox(parent.getShell(),
						SWT.ICON_INFORMATION | SWT.OK);
				messageBox.setText("��ʾ");
				messageBox.setMessage("����������ɹ���");
				messageBox.open();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		showDetailButton = new Button(composite, SWT.NONE);
		showDetailButton.setText("�鿴��ϸ��Ϣ");
		showDetailButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				true, // �����������룬���¶��룬�߶��������������������������
				true));
		showDetailButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Plugin.getInstance().initAndInvoke(myListeners, 1 + "");

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		exportReportButton = new Button(composite, SWT.NONE);
		exportReportButton.setText("��������");
		exportReportButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,
				true, // �����������룬���¶��룬�߶��������������������������
				true));
		exportReportButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ProgressMonitorDialog pmd = new ProgressMonitorDialog(parent
						.getShell());
				IRunnableWithProgress rwp = new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor)
							throws InvocationTargetException,
							InterruptedException {
						// totalWorkΪIProgressMonitor.UNKNOWNʱ��ʾ��֪�����������
						// ���ڽ���������ʾһ�������ƶ��Ľ�����
						monitor.beginTask("����1" + "", IProgressMonitor.UNKNOWN);
						try {
							saveReport(json);
							System.out.println("���ļ��������");
						} catch (DocumentException | IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * ִ�е��ļ��ȶ�
	 * 
	 * @param parent
	 */
	public void beginCompare(final Composite parent) {
		new Thread() {
			public void run() {
				parent.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						showFilePathLabel.setText("������ļ���"
								+ destFilePath.getText());
						// table.removeAll();
						// table.update();
						// lucene���в��ң���δ���ʵ���ϻᱻ����UI�߳���ִ��
						// new SearchFiles(t).search(destFilePath
						// .getText());
						// �����ļ�

						// json = method.paseDocument(editorPane,
						// destFilePath.getText());
						map = tool.paseDocument(destFilePath.getText(),
								editorPane);
						paragraphCount = (int) map.get("paragraphCount");
						wordCount = (int) map.get("countNum");
						copiedSententsNum = (int) map.get("copiedSententsNum");
						copiedCount = (int) map.get("copiedWordNum");
						percent = (float) copiedCount / wordCount * 100;

						showResultLabel.setText("����������" + paragraphCount
								+ "  ��������" + wordCount + "  �ظ�������"
								+ copiedCount + "  �ظ��ʣ�" + df.format(percent)
								+ "%");

						CopiedFilesTool.getInstance().clearAllFile();
						CopiedFileSentents copiedFileSentents = (CopiedFileSentents) map
								.get("copiedFileSentents");
						CopiedFilesTool.getInstance().addFile(
								copiedFileSentents);

						// ��ͼ����
						dataset.setValue("�ظ���", copiedCount);
						dataset.setValue("��д��", wordCount - copiedCount);
						// ��ѯ ���ͼƬ
						Image image;
						if (percent < 40) {
							image = new Image(null, this.getClass()
									.getResourceAsStream("/img/pass.png"));
							addToLibrary.setEnabled(true);
							resultString = App.RESULTSTRING_PASS;
						} else {
							image = new Image(null, this.getClass()
									.getResourceAsStream("/img/copied.png"));
							addToLibrary.setEnabled(false);
							resultString = App.RESULTSTRING_FAIL;
						}

						resultImglabel.setImage(image);

					}

				});
			}
		}.start();
	}

	@SuppressWarnings("unchecked")
	public void saveReport(JSONObject json) throws DocumentException,
			IOException {
		com.itextpdf.text.Document document = new com.itextpdf.text.Document(
				PageSize.A4, 50, 50, 50, 50);
		PdfWriter writer;

		try {
			writer = PdfWriter.getInstance(document, new FileOutputStream(
					"D:\\���ļ���ⱨ��_" + sdfFileNameDate.format(new Date())
							+ ".pdf"));
		} catch (FileNotFoundException | DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		document.open();
		// ��������
		Font titleFont = new Font(BaseFont.createFont(
				"C:/Windows/Fonts/simkai.ttf", BaseFont.IDENTITY_H,
				BaseFont.NOT_EMBEDDED), 20);
		// ��������
		Font baseFont = new Font(BaseFont.createFont(
				"C:/Windows/Fonts/simfang.ttf", BaseFont.IDENTITY_H,
				BaseFont.NOT_EMBEDDED), 13);
		// ��ɫ����
		Font redFont = new Font();
		redFont = new Font(BaseFont.createFont("C:/Windows/Fonts/simfang.ttf",
				BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED), 12);
		redFont.setColor(BaseColor.RED);

		// �������
		System.out.println(Platform.getInstanceLocation().getURL().getPath());
		// com.itextpdf.text.Image image = com.itextpdf.text.Image.getInstance
		// (System.getProperty("user.dir")+"\\icons\\electricLOGO.png");
		Paragraph pdfTitle = new Paragraph(App.PDFREPORT_TITLE, titleFont);
		// pdfTitle.add(image);
		pdfTitle.setAlignment(1);
		pdfTitle.setSpacingAfter(30);
		// ��������ʱ��

		Paragraph creatDate = new Paragraph("��������ʱ�䣺" + sdf.format(new Date()),
				baseFont);
		creatDate.setSpacingAfter(20);
		PdfPTable table = new PdfPTable(4);
		table.getDefaultCell().setMinimumHeight(40);
		table.setWidthPercentage(100);
		table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);

		table.addCell(new Phrase("�ļ���", baseFont));
		PdfPCell cell;
		cell = new PdfPCell(new Phrase(this.docFileName, baseFont));
		cell.setColspan(3);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
		table.addCell(new Phrase("����", baseFont));
		table.addCell(paragraphCount + "");
		table.addCell(new Phrase("������", baseFont));
		table.addCell(wordCount + "");
		table.addCell(new Phrase("�ظ����", baseFont));
		table.addCell(copiedSententsNum + "");
		table.addCell(new Phrase("�ظ��������", baseFont));
		table.addCell(copiedCount + "");
		table.addCell(new Phrase("�ظ���", baseFont));
		table.addCell(df.format(percent) + "%");
		table.addCell(new Phrase("�����", baseFont));
		if (resultString.equals(App.RESULTSTRING_PASS)) {
			table.addCell(new Phrase(resultString, baseFont));
		} else {
			table.addCell(new Phrase(resultString, redFont));
		}

		try {
			document.add(pdfTitle);
			document.add(creatDate);

			document.add(table);

		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Paragraph paragraph1 = new Paragraph(
				new Paragraph("����Ϊ��ϸ��Ϣ��", baseFont));
		paragraph1.setSpacingBefore(40);
		paragraph1.setSpacingAfter(20);
		document.add(paragraph1);

		List<CopiedFileSentents> list = CopiedFilesTool.getInstance()
				.getFilesList();
		CopiedFileSentents c = list.get(0);
		List<CopiedSentent> l2 = c.getCopiedStentsList();
		float[] widths2 = { 0.1f, 0.5f, 0.4f };
		PdfPTable singleTable = new PdfPTable(widths2);
		singleTable.setWidthPercentage(100);
		singleTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
		singleTable.getDefaultCell().setHorizontalAlignment(
				Element.ALIGN_CENTER);

		singleTable.addCell(new Phrase("���", baseFont));
		singleTable.addCell(new Phrase("���ӳ�Ϯ����", baseFont));
		singleTable.addCell(new Phrase("����", baseFont));
		int i = 1;
		for (final CopiedSentent sentent : l2) {

			singleTable.addCell(new Phrase(i + "", baseFont));
			singleTable.addCell(new Phrase(sentent.getOriginalSentents(),
					baseFont));
			singleTable.addCell(new Phrase(sentent.getFileName(), baseFont));
			i++;
		}
		try {
			document.add(singleTable);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		document.close();

	}
}