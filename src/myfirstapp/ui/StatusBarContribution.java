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
		Label separator =new Label(parent,SWT.SEPARATOR);//添加一个竖立的分隔线
		//布局数据类
		StatusLineLayoutData statusLineLayoutData=new StatusLineLayoutData();
		statusLineLayoutData.heightHint=20;
		separator.setLayoutData(statusLineLayoutData);
		//显示文字信息的标签
		CLabel statusCLabel=new CLabel(parent,SWT.SHADOW_NONE);
		statusLineLayoutData=new StatusLineLayoutData();
		statusLineLayoutData.widthHint=315;
		statusCLabel.setLayoutData(statusLineLayoutData);
		statusCLabel.setText(message);
		
		separator =new Label(parent,SWT.SEPARATOR);//添加一个竖立的分隔线
		statusLineLayoutData=new StatusLineLayoutData();
		statusLineLayoutData.heightHint=20;
		
		//显示文字信息的标签
				statusCLabel=new CLabel(parent,SWT.SHADOW_NONE);
				statusLineLayoutData=new StatusLineLayoutData();
				statusLineLayoutData.widthHint=315;
				statusCLabel.setLayoutData(statusLineLayoutData);
				//statusCLabel.setText("授权：四川省电力检修公司");
	}
	public StatusBarContribution(String msg){
		message=msg;
	}
}
