package lucene_3_tika;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

@SuppressWarnings("deprecation")
public class MyAnalyzer extends Analyzer {

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		final Tokenizer tokenizer = new MyTokenizer();
		return new TokenStreamComponents(tokenizer);
	}

}
