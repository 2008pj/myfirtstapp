package myfirstapp.sysdeal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import lucene_3_tika.SearchFiles;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.json.JSONObject;

public class CopyOfSingleFileCompareDealMethod {
	SearchFiles searchFile = new SearchFiles();
	Tool tool = new Tool();

	/**
	 * 
	 * ����������ļ������ļ����зֶδ���
	 * 
	 * @param editorPane
	 * @param filePath
	 */
	public JSONObject paseDocument( JTextPane editorPane,
			String filePath) {
		JSONObject json;
		XWPFDocument docx = null;
		
		try {
			InputStream in = new FileInputStream(filePath);
			docx = new XWPFDocument(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String docxTitle=docx.getProperties().getCoreProperties().getTitle();
		List<XWPFParagraph> paragraphs = docx.getParagraphs();
		String paragraphContext = "";
		Document doc = new DefaultStyledDocument();
		editorPane.setDocument(doc);
		StyleContext sc = StyleContext.getDefaultStyleContext();// ���һ������������ʽ����
		java.awt.Color color = new java.awt.Color(237, 82, 16);
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
				StyleConstants.Foreground, color);// Ϊ����ӵ���ʽ�����������ɫ
		int copiedWordNum = 0;// �ظ�����
		int countNum = 0;// ��������=��������+���ķ���+Ӣ�ĵ�����
		int copiedSententsNum=0;//�ظ��ľ�����
		int paragraphCountNum = paragraphs.size();// ��������

		for (XWPFParagraph p : paragraphs) {
			try {
				doc.insertString(doc.getLength(), "\r\n     ", null);
				paragraphContext = p.getText();

				// ����ͳ��,�������ĺ��֡����ķ��š�Ӣ�ĵ���
				countNum += tool.checkWordsNum(paragraphContext);// ���ж��������������

				// ��������Ϊ����
				List<String> sententList = new Tool()
						.parseToSentences(paragraphContext);

				// �������о��ӣ��Ծ���Ϊ��λ���в��أ��������Ϊ�ظ�����ı�������ɫ
				for (String sentent : sententList) {
					/**
					 * ���غ��ķ���������ظ����򷵻�һ�����󣬰���1ԭʼ���ӡ�2�������¡�3�������⡢4Id
					 */
					CopiedSentent copiedSentent = searchFile.isCopied(sentent);
					// �ظ�
					if (copiedSentent != null) {
//						CopiedSententsList.add(copiedSentent);
						copiedSententsNum++;
						copiedWordNum += tool.checkWordsNum(sentent);// �������ĺ��֡����ķ��š�Ӣ�ĵ���
						doc.insertString(doc.getLength(), sentent, aset);
					} else {
						doc.insertString(doc.getLength(), sentent, null);
					}
				}
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		json = new JSONObject(//
				"{'paragraphCount':"+paragraphCountNum+","//��������
						+ "'wordCount':"+countNum+","//����
						+ "'docxTitle':"+docxTitle+","//�ĵ�����
						+ "'copiedSententsNum':"+copiedSententsNum+","//�ظ������
						+ "'copiedWordNum':"+copiedWordNum+"}"//�ظ�����
		);
		return json;
	}
	
	
	public void writeToReport(String highlighterString, String reportFilePath) {
		try {
			File file = new File(reportFilePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter writer = new FileWriter(file);
			writer.write("<html>");
			writer.write("<body style=\"font-size:13px;line-height:20px;\">");
			writer.write(highlighterString);
			writer.write("</body>");
			writer.write("</html>");
			writer.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
