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
              // ---------创建窗口中的其他界面组件-------------
              shell.setLayout(new GridLayout());
              createMainComp(shell);//创建主面板
              createStatusbar(shell);//创建工具栏
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
              b1.setText("隐藏状态栏");
              b1.addSelectionListener(new SelectionAdapter() {
                       private boolean flag = true;
                       public void widgetSelected(SelectionEvent e) {
                                // 用statusbar.setVisible(false)来隐藏状态栏是不够的，还必须把它占用的空间也释放出来，这时应该用GridData.exclude
                                GridData data = (GridData) statusbar.getLayoutData();
                                data.exclude = flag;
                                shell.layout();
                                b1.setText((flag ? "显示" : "隐藏") + "状态栏");
                                flag = !flag;
                       }
              });
              hideProbarButton = new Button(parent, SWT.NONE);
              hideProbarButton.setText("隐藏进度条");
              hideProbarButton.setEnabled(false);
              hideProbarButton.addSelectionListener(new SelectionAdapter() {
                       private boolean flag = false;
                       public void widgetSelected(SelectionEvent e) {
                                progressBar.setVisible(flag);
                                hideProbarButton.setText((flag ? "隐藏" : "显示") + "进度条");
                                flag = !flag;
                       }
              });
              final Button b3 = new Button(parent, SWT.NONE);
              b3.setText(" GO ");
              b3.addSelectionListener(new SelectionAdapter() {
                       private boolean stopFlag = true;
                       public void widgetSelected(SelectionEvent e) {
                                stopFlag = !stopFlag;
                                if (stopFlag) // 根据停止标志stopFlag来判断是停止还是运行
                                          stop();
                                else
                                          go();
                       }
                       private void stop() {
                                b3.setEnabled(false);// 停止需要时间，在完全停止前要防止再次开始。
                                b3.setText("GO");
                       }
                       private void go() {
                                b3.setText("STOP");
                                progressBar = createProgressBar(statusbar);
                                hideProbarButton.setEnabled(true);
                                statusbar.layout();// 重新布局一下工具栏，使进度条显示出来
                                new Thread() {
                                          public void run() {
                                                   for (int i = 1; i < 11; i++) {
                                                            if (display.isDisposed() || stopFlag) {
                                                                      disposeProgressBar();
                                                                      return;
                                                            }
                                                            moveProgressBar(i);
                                                            try {  Thread.sleep(1000);          } catch (Throwable e2) {} //停一秒
                                                   }
                                                   disposeProgressBar();
                                          }
                                          private void moveProgressBar(final int i) {
                                                   display.asyncExec(new Runnable() {
                                                            public void run() {
                                                                      if (!statusbarLabel.isDisposed())
                                                                               statusbarLabel.setText("前进到第" + i + "步");
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
                       // 这一句不能放在线程外执行，否则progressBar被创建后就立即被dispose了
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
              //设置工具栏在Shell中的形状为水平抢占充满，并高19像素
              GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
              gridData.heightHint = 19;
              statusbar.setLayoutData(gridData);
              //设置为用行列式布局管理状态栏里的组件
              RowLayout layout = new RowLayout();
              layout.marginLeft = layout.marginTop = 0; //无边距
              statusbar.setLayout(layout);
              //创建一个用于显示文字的标签
              statusbarLabel = new Label(statusbar, SWT.BORDER);
              statusbarLabel.setLayoutData(new RowData(70, -1));
    }
    //创建进度条
    private ProgressBar createProgressBar(Composite parent) {
              ProgressBar progressBar = new ProgressBar(parent, SWT.SMOOTH);
              progressBar.setMinimum(0); // 最小值
              progressBar.setMaximum(100);// 最大值
              return progressBar;
    }
}
