package myfirstapp.ui;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class StatusBarContribution extends ContributionItem {
	
	
	private static String message;
	
	public StatusBarContribution(){
		super();
	}
	public void fill(Composite parent){
		Label separator =new Label(parent,SWT.SEPARATOR);//���һ�������ķָ���
		//����������
		StatusLineLayoutData statusLineLayoutData=new StatusLineLayoutData();
		statusLineLayoutData.heightHint=20;
		separator.setLayoutData(statusLineLayoutData);
		//��ʾ������Ϣ�ı�ǩ
		CLabel statusCLabel=new CLabel(parent,SWT.SHADOW_NONE);
		statusLineLayoutData=new StatusLineLayoutData();
		statusLineLayoutData.widthHint=315;
		statusCLabel.setLayoutData(statusLineLayoutData);
		statusCLabel.setText(message);
		
		separator =new Label(parent,SWT.SEPARATOR);//���һ�������ķָ���
		statusLineLayoutData=new StatusLineLayoutData();
		statusLineLayoutData.heightHint=20;
		
		//��ʾ������Ϣ�ı�ǩ
				statusCLabel=new CLabel(parent,SWT.SHADOW_NONE);
				statusLineLayoutData=new StatusLineLayoutData();
				statusLineLayoutData.widthHint=315;
				statusCLabel.setLayoutData(statusLineLayoutData);
				//statusCLabel.setText("��Ȩ���Ĵ�ʡ�������޹�˾");
	}
	public StatusBarContribution(String msg){
		message=msg;
	}
}
