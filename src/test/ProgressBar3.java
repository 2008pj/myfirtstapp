package test;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
public class ProgressBar3 {
    private Display display;
    private Shell shell;
    private Composite statusbar;
    private Label statusbarLabel;
    private ProgressBar progressBar;
    private Button hideProbarButton;
    public static void main(String[] args) {      new ProgressBar3().open();     }
    private void open() {
              display = Display.getDefault();
              shell = new Shell();
              shell.setSize(250, 170);
              // ---------���������е������������-------------
              shell.setLayout(new GridLayout());
              createMainComp(shell);//���������
              createStatusbar(shell);//����������
              // -----------------END------------------------
              shell.layout();
              shell.open();
              while (!shell.isDisposed()) {
                       if (!display.readAndDispatch())
                                display.sleep();
              }
              display.dispose();
    }
    private void createMainComp(Composite parent) {
              Composite comp = new Composite(parent, SWT.BORDER);
              comp.setLayoutData(new GridData(GridData.FILL_BOTH));
              comp.setLayout(new RowLayout());
              createButton(comp);
    }
    private void createButton(Composite parent) {
              final Button b1 = new Button(parent, SWT.NONE);
              b1.setText("����״̬��");
              b1.addSelectionListener(new SelectionAdapter() {
                       private boolean flag = true;
                       public void widgetSelected(SelectionEvent e) {
                                // ��statusbar.setVisible(false)������״̬���ǲ����ģ����������ռ�õĿռ�Ҳ�ͷų�������ʱӦ����GridData.exclude
                                GridData data = (GridData) statusbar.getLayoutData();
                                data.exclude = flag;
                                shell.layout();
                                b1.setText((flag ? "��ʾ" : "����") + "״̬��");
                                flag = !flag;
                       }
              });
              hideProbarButton = new Button(parent, SWT.NONE);
              hideProbarButton.setText("���ؽ�����");
              hideProbarButton.setEnabled(false);
              hideProbarButton.addSelectionListener(new SelectionAdapter() {
                       private boolean flag = false;
                       public void widgetSelected(SelectionEvent e) {
                                progressBar.setVisible(flag);
                                hideProbarButton.setText((flag ? "����" : "��ʾ") + "������");
                                flag = !flag;
                       }
              });
              final Button b3 = new Button(parent, SWT.NONE);
              b3.setText(" GO ");
              b3.addSelectionListener(new SelectionAdapter() {
                       private boolean stopFlag = true;
                       public void widgetSelected(SelectionEvent e) {
                                stopFlag = !stopFlag;
                                if (stopFlag) // ����ֹͣ��־stopFlag���ж���ֹͣ��������
                                          stop();
                                else
                                          go();
                       }
                       private void stop() {
                                b3.setEnabled(false);// ֹͣ��Ҫʱ�䣬����ȫֹͣǰҪ��ֹ�ٴο�ʼ��
                                b3.setText("GO");
                       }
                       private void go() {
                                b3.setText("STOP");
                                progressBar = createProgressBar(statusbar);
                                hideProbarButton.setEnabled(true);
                                statusbar.layout();// ���²���һ�¹�������ʹ��������ʾ����
                                new Thread() {
                                          public void run() {
                                                   for (int i = 1; i < 11; i++) {
                                                            if (display.isDisposed() || stopFlag) {
                                                                      disposeProgressBar();
                                                                      return;
                                                            }
                                                            moveProgressBar(i);
                                                            try {  Thread.sleep(1000);          } catch (Throwable e2) {} //ͣһ��
                                                   }
                                                   disposeProgressBar();
                                          }
                                          private void moveProgressBar(final int i) {
                                                   display.asyncExec(new Runnable() {
                                                            public void run() {
                                                                      if (!statusbarLabel.isDisposed())
                                                                               statusbarLabel.setText("ǰ������" + i + "��");
                                                                      if (!progressBar.isDisposed())
                                                                               progressBar.setSelection(i * 10);
                                                            }
                                                   });
                                          }
                                          private void disposeProgressBar() {
                                                   if (display.isDisposed())   return;
                                                   display.asyncExec(new Runnable() {
                                                            public void run() {
                                                                      hideProbarButton.setEnabled(false);
                       // ��һ�䲻�ܷ����߳���ִ�У�����progressBar���������������dispose��
                                                                      progressBar.dispose();
                                                                      b3.setEnabled(true);
                                                            }
                                                   });
                                          }
                                }.start();
                       }
              });
    }
    private void createStatusbar(Composite parent) {
              statusbar = new Composite(parent, SWT.BORDER);
              //���ù�������Shell�е���״Ϊˮƽ��ռ����������19����
              GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
              gridData.heightHint = 19;
              statusbar.setLayoutData(gridData);
              //����Ϊ������ʽ���ֹ���״̬��������
              RowLayout layout = new RowLayout();
              layout.marginLeft = layout.marginTop = 0; //�ޱ߾�
              statusbar.setLayout(layout);
              //����һ��������ʾ���ֵı�ǩ
              statusbarLabel = new Label(statusbar, SWT.BORDER);
              statusbarLabel.setLayoutData(new RowData(70, -1));
    }
    //����������
    private ProgressBar createProgressBar(Composite parent) {
              ProgressBar progressBar = new ProgressBar(parent, SWT.SMOOTH);
              progressBar.setMinimum(0); // ��Сֵ
              progressBar.setMaximum(100);// ���ֵ
              return progressBar;
    }
}
