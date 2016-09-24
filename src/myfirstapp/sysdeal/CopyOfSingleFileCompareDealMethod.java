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
	 * 分析输入的文件。将文件进行分段处理。
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
		StyleContext sc = StyleContext.getDefaultStyleContext();// 添加一个可以设置样式的类
		java.awt.Color color = new java.awt.Color(237, 82, 16);
		AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
				StyleConstants.Foreground, color);// 为所添加的样式类添加字体颜色
		int copiedWordNum = 0;// 重复字数
		int countNum = 0;// 文章字数=中文字数+中文符号+英文单词数
		int copiedSententsNum=0;//重复的句子数
		int paragraphCountNum = paragraphs.size();// 段落总数

		for (XWPFParagraph p : paragraphs) {
			try {
				doc.insertString(doc.getLength(), "\r\n     ", null);
				paragraphContext = p.getText();

				// 字数统计,包含中文汉字、中文符号、英文单词
				countNum += tool.checkWordsNum(paragraphContext);// 所有段落中文字数相加

				// 将段落拆分为句子
				List<String> sententList = new Tool()
						.parseToSentences(paragraphContext);

				// 遍历所有句子，以句子为单位进行查重，如果发现为重复，则改变文字颜色
				for (String sentent : sententList) {
					/**
					 * 查重核心方法，如果重复，则返回一个对象，包含1原始句子、2高亮文章、3出处标题、4Id
					 */
					CopiedSentent copiedSentent = searchFile.isCopied(sentent);
					// 重复
					if (copiedSentent != null) {
//						CopiedSententsList.add(copiedSentent);
						copiedSententsNum++;
						copiedWordNum += tool.checkWordsNum(sentent);// 包含中文汉字、中文符号、英文单词
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
				"{'paragraphCount':"+paragraphCountNum+","//段落总数
						+ "'wordCount':"+countNum+","//字数
						+ "'docxTitle':"+docxTitle+","//文档标题
						+ "'copiedSententsNum':"+copiedSententsNum+","//重复语句数
						+ "'copiedWordNum':"+copiedWordNum+"}"//重复字数
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
