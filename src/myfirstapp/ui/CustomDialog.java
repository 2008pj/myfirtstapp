package myfirstapp.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class CustomDialog extends Dialog {
	protected Object result;  
    protected Shell shell;
    String temp="";
    Browser browser;
	public CustomDialog(Shell parent,String s) {
		super(parent);
		temp=s;
	}
	 public Object open() {  
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
	  
	    protected void createContents() {  
	        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);  
	        shell.setSize(312, 212);  
	        shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true)); 
	        shell.setText("SWT Dialog");  
	  
//	        final Button button = new Button(shell, SWT.NONE);  
//	        button.setText(temp);  
//	        button.setBounds(127, 74, 44, 23); 
//	        browser = new Browser(shell, SWT.NONE);
//	        browser.setBounds(0, 0, 312, 212); 
//			browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
//			browser.setText(temp);
	        
	        
	    }  
}
