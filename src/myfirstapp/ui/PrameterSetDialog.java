package myfirstapp.ui;

import myfirstapp.sysdeal.PropertiesReader;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class PrameterSetDialog extends Dialog {
	protected Shell shell;

	private int result;

	Label min_wordSize_labe;
	Text min_wordSize_value;
	Label min_wordSize_discription;

	Label passPercent_label;
	Text passPercent_value;
	Label passPercent_discription;

	Label topDocumentNum_label;
	Text topDocumentNum_value;
	Label topDocumentNum_discription;

	Label colore1_label;
	Text colore1_value;
	Label colore1_discription;

	Label colore2_label;
	Text colore2_value;
	Label colore2_discription;

	Button saveButton;

	public PrameterSetDialog(Shell parent) {
		this(parent, SWT.NONE);
	}

	public PrameterSetDialog(Shell parent, int style) {
		super(parent, style);
	}

	protected void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(590, 199);
		shell.setText("�������� ");
		// �����Զ�����
		Rectangle screenSize = Display.getDefault().getClientArea();
		Rectangle frameSize = shell.getBounds();
		shell.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		shell.setLayout(new GridLayout(3, false));

		min_wordSize_labe = new Label(shell, SWT.NONE);
		min_wordSize_labe.setText("��С�ʻ㵥Ԫ������");

		min_wordSize_value = new Text(shell, SWT.BORDER);
		min_wordSize_value.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		min_wordSize_value.setText(PropertiesReader.getInstance().readKey(
				"min_wordSize"));

		min_wordSize_discription = new Label(shell, SWT.NONE);
		min_wordSize_discription.setText("����С�ʻ㵥Ԫ��ֵ����������ʱ����С�ʻ㵥ԪС�ڸ���ֵ���򲻽���������");

		passPercent_label = new Label(shell, SWT.NONE);
		passPercent_label.setText("���ƶ���ֵ��");

		passPercent_value = new Text(shell, SWT.BORDER);
		passPercent_value.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		passPercent_value.setText(PropertiesReader.getInstance().readKey(
				"passPercent"));

		passPercent_discription = new Label(shell, SWT.NONE);
		passPercent_discription.setText("����ֵ%�����ӳ�Ϯ����ͨ�����ٽ�ֵ��");

		topDocumentNum_label = new Label(shell, SWT.NONE);
		topDocumentNum_label.setText("���Ƶ�����������");

		topDocumentNum_value = new Text(shell, SWT.BORDER);
		topDocumentNum_value.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false, 1, 1));
		topDocumentNum_value.setText(PropertiesReader.getInstance().readKey(
				"topDocumentNum"));

		topDocumentNum_discription = new Label(shell, SWT.NONE);
		topDocumentNum_discription.setText("�������ƵĿ��ļ�������");

		colore1_label = new Label(shell, SWT.NONE);
		colore1_label.setText("������ɫ1��");

		colore1_value = new Text(shell, SWT.BORDER);
		colore1_value.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		colore1_value.setText(PropertiesReader.getInstance().readKey(
				"exportColor1"));

		colore1_discription = new Label(shell, SWT.NONE);
		colore1_discription.setText("�����ڿ��ļ�������ʾ�ظ�������ɫ֮һ��");

		colore2_label = new Label(shell, SWT.NONE);
		colore2_label.setText("������ɫ2��");

		colore2_value = new Text(shell, SWT.BORDER);
		colore2_value.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false, 1, 1));
		colore2_value.setText(PropertiesReader.getInstance().readKey(
				"exportColor2"));

		colore2_discription = new Label(shell, SWT.NONE);
		colore2_discription.setText("�����ڿ��ļ�������ʾ�ظ�������ɫ֮һ��");

		saveButton = new Button(shell, SWT.NONE);
		saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		saveButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false,
				false, 3, 1));
		saveButton.setText("   ����   ");
		saveButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				PropertiesReader.getInstance().writeProperties("topDocumentNum", topDocumentNum_value.getText());
				PropertiesReader.getInstance().writeProperties("min_wordSize", min_wordSize_value.getText());
				PropertiesReader.getInstance().writeProperties("passPercent", passPercent_value.getText());
				PropertiesReader.getInstance().writeProperties("exportColor1", colore1_value.getText());
				PropertiesReader.getInstance().writeProperties("exportColor2", colore2_value.getText());
				MessageDialog.openInformation(
						shell, "֪ͨ", "����������ϡ��������֮��������Ч��");
				close();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	public int open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}
	protected void createContents2() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(514, 199);
		shell.setText("�������� ");
		// �����Զ�����
		Rectangle screenSize = Display.getDefault().getClientArea();
		Rectangle frameSize = shell.getBounds();
		shell.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		shell.setLayout(new GridLayout(3, false));

		Label l=new Label(shell,SWT.NONE);
		l.setText(System.getProperty("user.dir")+"\\properties1.properties");
	}
	
	public void close(){
		shell.dispose();
	}
}
