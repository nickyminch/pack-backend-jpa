$output.skipIf($project.search.isEmpty())##
$output.java($Search, "EnglishAnalyzer")##

$output.require("org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext")##
$output.require("org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer")##

public class EnglishAnalyzer implements LuceneAnalysisConfigurer {

	@Override
	public void configure(LuceneAnalysisConfigurationContext context) {
		context.analyzer( "english" ).custom() 
        .tokenizer( "standard" ) 
        .charFilter( "htmlStrip" ) 
        .tokenFilter( "lowercase" ) 
        .tokenFilter( "snowballPorter" ) 
                .param( "language", "English" ) 
        .tokenFilter( "asciiFolding" );

		context.normalizer( "lowercase" ).custom() 
		        .tokenFilter( "lowercase" )
		        .tokenFilter( "asciiFolding" );

		context.analyzer( "french" ).custom() 
		        .tokenizer( "standard" )
		        .charFilter( "htmlStrip" )
		        .tokenFilter( "lowercase" )
		        .tokenFilter( "snowballPorter" )
		                .param( "language", "French" )
		        .tokenFilter( "asciiFolding" );
	}

}
