package lucene_3_tika;

import java.io.IOException;

import myfirstapp.sys.App;
import myfirstapp.sysdeal.PropertiesReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

import java.io.StringReader;

import org.apache.lucene.analysis.cn.smart.Utility;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * Tokenizes input text into sentences.
 * <p>
 * The output tokens can then be broken into words with {@link WordTokenFilter}
 * </p>
 * 
 * @lucene.experimental
 * @deprecated Use {@link HMMChineseTokenizer} instead
 */
@Deprecated
public final class MyTokenizer extends Tokenizer {

	/**
	 * End of sentence punctuation: 。，！？；,!?;
	 */
	private final static String PUNCTION = "：。，！？；,!?;……";
	private boolean atBegin = true;
	private final StringBuilder buffer = new StringBuilder();
	int count = 0;

	private int tokenStart = 0, tokenEnd = 0;

	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
	private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);

	public MyTokenizer() {
	}

	// public MyTokenizer(AttributeFactory factory) {
	// super(factory);
	// }

	@Override
	public boolean incrementToken() throws IOException {
		count++;
		clearAttributes();
		buffer.setLength(0);
		int ci;
		char ch;
		char pch = 0;
		// boolean atBegin = true;
		tokenStart = tokenEnd;
		ci = input.read();
		ch = (char) ci;
//		System.out.println(ch);

		return this.handleTermAtrr(ch, pch, ci);

	}

	@Override
	public void reset() throws IOException {
		super.reset();
		tokenStart = tokenEnd = 0;
	}

	@Override
	public void end() throws IOException {
		super.end();
		// set final offset
		final int finalOffset = correctOffset(tokenEnd);
		offsetAtt.setOffset(finalOffset, finalOffset);
	}

	/**
	 * 去掉停用词
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public String stopFileter(String str) throws IOException {
		Analyzer anylzer = new IKAnalyzer(true);
		TokenStream tokenStream = anylzer.tokenStream("dd", new StringReader(
				str));
		tokenStream.reset();
		// 语汇单元对应的文本
		CharTermAttribute charTermAttribute = tokenStream
				.addAttribute(CharTermAttribute.class);
		// 递归处理所有语汇单元
		StringBuffer sb = new StringBuffer();
		while (tokenStream.incrementToken()) {
			sb.append(charTermAttribute.toString());
		}
		return sb.toString();
	}

	public void docheck(char ch, char pch, int ci) throws IOException {
		buffer.setLength(0);
		while (true) {
			// System.out.println("ch="+ch+"  tokenStart="+tokenStart+"  tokenEnd="+tokenEnd);
			if (ci == -1) {
				break;
			} else if (PUNCTION.indexOf(ch) != -1) {
				// End of a sentence
				buffer.append(ch);
				tokenEnd++;
				break;
			} else if (atBegin && Utility.SPACES.indexOf(ch) != -1) {
				tokenStart++;
				tokenEnd++;
				ci = input.read();
				ch = (char) ci;
			} else {
				buffer.append(ch);
				atBegin = false;
				tokenEnd++;
				pch = ch;
				ci = input.read();
				ch = (char) ci;
				// Two spaces, such as CR, LF
				if (Utility.SPACES.indexOf(ch) != -1
						&& Utility.SPACES.indexOf(pch) != -1) {
					// buffer.append(ch);
					tokenEnd++;
					break;
				}
			}
		}

	}

	public boolean handleTermAtrr(char ch, char pch, int ci) throws IOException {
		this.docheck(ch, pch, ci);
		if (buffer.length() == 0)
			return false;// 返回false，表示文章结束
		else {
			String str = stopFileter(buffer.toString());// 去除停用词
			int min_wordSize=Integer.parseInt(PropertiesReader.getInstance().readKey("min_wordSize"));
			if (str.length() < min_wordSize) {
				// System.out.println("******去掉停用词后长度为0,buffer="+buffer.toString());

				ci = input.read();
				if (ci == -1) {
					return false;
				} else {
					ch = (char) ci;
					return this.handleTermAtrr(ch, pch, ci);
				}

			} else {
				// System.out.println("tokenStart=" + tokenStart + " tokenEndt="
				// + tokenEnd + "【" + str + "】");
				termAtt.setEmpty().append(str);
				offsetAtt.setOffset(correctOffset(tokenStart),
						correctOffset(tokenEnd));
				typeAtt.setType("sentence");
				return true;
			}

		}
	}
}
