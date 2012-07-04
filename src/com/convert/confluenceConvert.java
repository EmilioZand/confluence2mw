package com.convert;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.*;

public class confluenceConvert {

    private static String convert(String input){
	String text = input;
	String REGEX = null;
	String REPLACE = null;
	Pattern p = null;
	Matcher m = null; 

	text = text.replaceAll("(\n*)\\{code([\\s\\S]+?)\\{code\\}", "$1{startcode$2{endcode}");
	text = text.replaceAll("(\n*)\\{noformat([\\s\\S]+?)\\{noformat\\}", "$1{startnoformat$2{endnoformat}");
	text = text.replaceAll("[\\[](?=(?:(?!\\{startcode)[\\s\\S])*?\\{endcode\\})", "~~STARTBRACKET~~");
	text = text.replaceAll("[\\]](?=(?:(?!\\{startcode)[\\s\\S])*?\\{endcode})", "~~ENDBRACKET~~");
	text = text.replaceAll("[\\\\](?=(?:(?!\\{startcode)[\\s\\S])*?\\{endcode\\})", "~~BACKSLASH~~");
	text = text.replaceAll("[\\*](?=(?:(?!\\{startcode)[\\s\\S])*?\\{endcode\\})", "~~STAR~~");

	text = text.replaceAll("[\\[](?=(?:(?!\\{startnoformat)[\\s\\S])*?\\{endnoformat\\})", "~~STARTBRACKET~~");
	text = text.replaceAll("[\\]](?=(?:(?!\\{startnoformat)[\\s\\S])*?\\{endnoformat\\})", "~~ENDBRACKET~~");
	text = text.replaceAll("[\\\\](?=(?:(?!\\{startnoformat)[\\s\\S])*?\\{endnoformat\\})", "~~BACKSLASH~~");
	text = text.replaceAll("[\\*](?=(?:(?!\\{startnoformat)[\\s\\S])*?\\{endnoformat\\})", "~~STAR~~");
	/* convert confluence newlines */
	text = text.replaceAll("\134\134", "<br/>");

	/* remove anchors - mediawiki handles automatically */
	text = text.replaceAll("\\{anchor.*}", "");

	String codereplace2 = "<!-- code start-->\n<h5 style=\"text-align:center\">$1</h5><hr>\n<pre style=\"margin-left:20px; font-size:1.4em; background-color:#fdfdfd\">$2</pre><!-- code end-->\n";
	String codereplace1 = "\n<!-- code start-->\n<pre style=\"margin-left:20px; font-size:1.4em; background-color:#fdfdfd\">$2</pre><!-- code end-->\n";

	text = text.replaceAll("\\{startcode\\s*:\\s*title=([^|}]*)[|][^\\}]*}([\\s\\S]+?)\\{endcode\\}", codereplace2 );   
	text = text.replaceAll("\\{startcode\\s*:\\s*title=([^}]*)\\}([\\s\\S]+?)\\{endcode\\}", codereplace2 );
	text = text.replaceAll("\\{startcode\\s*:\\s*(.*)?\\}([\\s\\S]+?)\\{endcode\\}", codereplace1 );       
	text = text.replaceAll("\\{startcode(\\s*)\\}([\\s\\S]+?)\\{endcode\\}", codereplace1 );

	String noformatreplace2 = "<h5 style=\"text-align:center\">$1</h5><hr>\n<pre style=\"margin-left:20px; border:solid; border-color:#3C78B5; border-width:1px; font-size:1.4em; background-color:#fff\">$2</pre>";
	String noformatreplace1 = "\n<pre style=\"margin-left:20px; border:solid; border-color:#3C78B5; border-width:1px; font-size:1.4em; background-color:#fff\">$2</pre>";
	text = text.replaceAll("\\{startnoformat\\s*:\\s*title=([^|}]*)[|][^\\}]*}([\\s\\S]+?)\\{endnoformat\\}", noformatreplace2 );   
	text = text.replaceAll("\\{startnoformat\\s*:\\s*title=([^\\}]*)}([\\s\\S]+?)\\{endnoformat\\}", noformatreplace2 );
	text = text.replaceAll("\\{startnoformat\\s*:\\s*(.*)?\\}([\\s\\S]+?)\\{endnoformat\\}", noformatreplace1 );       
	text = text.replaceAll("\\{startnoformat(\\s*)\\}([\\s\\S]+?)\\{endnoformat\\}", noformatreplace1 );

	String panelreplace2 = "~~TABLESTART~~ cellpadding=\"10\" width=\"100%\" style=\"margin-left:20px; border:solid; border-color:#55a; border-width:1px; text-align:left; background-color:#f0f0f0;\"\n~~ROWSTART~~\n| *$1*<hr>\n<div style=\"white-space: pre\">\n$2\n</div>\n~~TABLEEND~~\n<br/>";
	String panelreplace1 = "~~TABLESTART~~ cellpadding=\"10\" width=\"100%\" style=\"margin-left:20px; border:solid; border-color:#55a; border-width:1px; text-align:left; background-color:#f0f0f0;\"\n~~ROWSTART~~\n|\n<div style=\"white-space: pre\">\n$2\n</div>\n~~TABLEEND~~\n<br/>";

	text = text.replaceAll("\\{panel\\s*:\\s*title=([^|\\}]*)[|][^\\}]*}([\\s\\S]+?)\\{panel\\}", panelreplace2 );   
	text = text.replaceAll("\\{panel\\s*:\\s*title=([^\\}]*)\\\\}([\\s\\S]+?)\\{panel\\}", panelreplace2 );
	text = text.replaceAll("\\{panel\\s*:\\s*(.*)?\\}([\\s\\S]+?)\\{panel\\}", panelreplace1 );       
	text = text.replaceAll("\\{panel(\\s*)\\}([\\s\\S]+?)\\{panel\\}", panelreplace1 );

	String tipreplace2 = "~~TABLESTART~~ width=\"100%\" style=\"padding: 20px; margin-left:20px; border:solid; border-color:#aaa; border-width:0px; text-align:left; background-color:#ddffdd;\"\n~~ROWSTART~~\n| <span style=\"color:#00AA00\">*TIP*:</span> *$1*<hr>\n~~ROWSTART~~\n|\n<div style=\"white-space: pre\">\n$2\n</div>\n~~TABLEEND~~\n<br/>";
	String tipreplace1 = "~~TABLESTART~~ width=\"100%\" style=\"padding: 20px; margin-left:20px; border:solid; border-color:#aaa; border-width:0px; text-align:left; background-color:#ddffdd;\"\n~~ROWSTART~~\n| <span style=\"color:#00AA00\">*TIP*</span><hr>\n~~ROWSTART~~\n|\n<div style=\"white-space: pre\">\n$2\n</div>\n~~TABLEEND~~\n<br/>";

	text = text.replaceAll("\\{tip\\s*:\\s*title=([^|\\}]*)[|][^\\}]*}([\\s\\S]+?)\\{tip\\}", tipreplace2 );   
	text = text.replaceAll("\\{tip\\s*:\\s*title=([^\\}]*)\\}([\\s\\S]+?)\\{tip\\}", tipreplace2 );
	text = text.replaceAll("\\{tip\\s*:\\s*(.*)?\\\\}([\\s\\S]+?)\\{tip\\}", tipreplace1 );       
	text = text.replaceAll("\\{tip(\\s*)\\}([\\s\\S]+?)\\{tip\\\\}", tipreplace1 );

	String inforeplace2 = "~~TABLESTART~~ width=\"100%\" style=\"padding: 20px; margin-left:20px; border:solid; border-color:#aaa; border-width:0px; text-align:left; background-color:#D8E4F1;\"\n~~ROWSTART~~\n| <span style=\"color:#0000AA\">*INFO*:</span> *$1*<hr>\n~~ROWSTART~~\n|\n<div style=\"white-space: pre\">$2</div>\n~~TABLEEND~~\n<br/>";
	String inforeplace1 = "~~TABLESTART~~ width=\"100%\" style=\"padding: 20px; margin-left:20px; border:solid; border-color:#aaa; border-width:0px; text-align:left; background-color:#D8E4F1;\"\n~~ROWSTART~~\n| <span style=\"color:#0000AA\">*INFO*</span><hr>\n~~ROWSTART~~\n|\n<div style=\"white-space: pre\">\n$2\n</div>\n~~TABLEEND~~\n<br/>";

	text = text.replaceAll("\\{info\\s*:\\s*title=([^|\\}]*)[|][^\\}]*\\\\}([\\s\\S]+?)\\{info\\}", inforeplace2 );   
	text = text.replaceAll("\\{info\\s*:\\s*title=([^\\}]*)\\}([\\s\\S]+?)\\{info\\}", inforeplace2 );
	text = text.replaceAll("\\{info\\s*:\\s*(.*)?\\}([\\s\\S]+?)\\{info\\}", inforeplace1 );       
	text = text.replaceAll("\\{info(\\s*)\\}([\\s\\S]+?)\\{info\\}", inforeplace1 );

	String notereplace2 = "~~TABLESTART~~ width=\"100%\" style=\"padding: 20px; margin-left:20px; border:solid; border-color:#aaa; border-width:0px; text-align:left; background-color:#FFFFCE;\"\n~~ROWSTART~~\n| <span style=\"color:#AAAA00\">*NOTE*:</span> *$1*<hr>\n~~ROWSTART~~\n|\n<div style=\"white-space: pre\">$2</div>\n~~TABLEEND~~\n<br/>";
	String notereplace1 = "~~TABLESTART~~   width=\"100%\" style=\"padding: 20px; margin-left:20px; border:solid; border-color:#aaa; border-width:0px; text-align:left; background-color:#FFFFCE;\"\n~~ROWSTART~~\n| <span style=\"color:#AAAA00\">*NOTE*</span><hr>\n~~ROWSTART~~\n|\n<div style=\"white-space: pre\">\n$2\n</div>\n~~TABLEEND~~\n<br/>";

	text = text.replaceAll("\\{note\\s*:\\s*title=([^|\\}]*)[|][^\\}]*\\}([\\s\\S]+?)\\{note\\}", notereplace2 );   
	text = text.replaceAll("\\{note\\s*:\\s*title=([^\\}]*)\\}([\\s\\S]+?)\\{note\\}", notereplace2 );
	text = text.replaceAll("\\{note\\s*:\\s*(.*)?\\}([\\s\\S]+?)\\{note\\}", notereplace1 );       
	text = text.replaceAll("\\{note(\\s*)\\}([\\s\\S]+?)\\{note\\}", notereplace1 );

	String warningreplace2 = "<!-- warning start -->\n~~TABLESTART~~ width=\"100%\" style=\"padding: 20px; margin-left:20px; border:solid; border-color:#aaa; border-width:0px; text-align:left; background-color:#FFCCCC;\"\n~~ROWSTART~~\n| <span style=\"color:#AA0000\">*WARNING*:</span> *$1*<hr>\n~~ROWSTART~~\n|\n<div style=\"white-space: pre\">\n$2\n</div>\n~~TABLEEND~~\n<br/>";
	String warningreplace1 = "<!-- warning start -->\n~~TABLESTART~~ width=\"100%\" style=\"padding: 20px; margin-left:20px; border:solid; border-color:#aaa; border-width:0px; text-align:left; background-color:#FFCCCC;\"\n~~ROWSTART~~\n| <span style=\"color:#AA0000\">*WARNING*</span><hr>\n~~ROWSTART~~\n|\n<div style=\"white-space: pre\">\n$2\n</div>\n~~TABLEEND~~\n<br/>";

	text = text.replaceAll("\\{warning\\s*:\\s*title=([^|\\}]*)[|][^\\}]*\\}([\\s\\S]+?)\\{warning\\}", warningreplace2 );   
	text = text.replaceAll("\\{warning\\s*:\\s*title=([^\\}]*)\\}([\\s\\S]+?)\\{warning\\}", warningreplace2 );
	text = text.replaceAll("\\{warning\\s*:\\s*(.*)?\\}([\\s\\S]+?)\\{warning\\}", warningreplace1 );       
	text = text.replaceAll("\\{warning(\\s*)\\}([\\s\\S]+?)\\{warning\\}", warningreplace1 );

	// section   
	String sectionreplace = "<!-- section start -->\n~~TABLESTART~~ border=0 width=\"100%\" cellpadding=10 align=top\n~~ROWSTART~~$1\n~~TABLEEND~~<!-- section end-->\n";
	text = text.replaceAll("(\\{section\\}([\\s\\S]+?)(\\{column\\}([\\s\\S]+?)\\{column\\})*?(.|\n)*?\\{section\\})", sectionreplace);
	text = text.replaceAll("\\{section\\}([\\s\\S]+?)\\{section\\}", "$1");


	// column
	String columnreplace1 = "\n<!-- column start -->\n~~CELLSTART~~$2\n<!-- column end -->\n";
	String columnreplace2 = "\n<!-- column start -->\n~~CELLSTART~~$2\n<!-- column end -->\n";
	text = text.replaceAll("\\{column\\s*:\\s*title=([^|\\}]*)[|][^\\}]*\\}([\\s\\S]+?)\\{column\\}", columnreplace2 );   
	text = text.replaceAll("\\{column\\s*:\\s*title=([^\\}]*)\\}([\\s\\S]+?)\\{column\\}", columnreplace2 );
	text = text.replaceAll("\\{column\\s*:\\s*(.*)?\\\\}([\\s\\S]+?)\\{column\\}", columnreplace1 );       
	text = text.replaceAll("\\{column(\\s*)\\}([\\s\\S]+?)\\{column\\}", columnreplace1 );

	/* Add newline to EOF to fix issues */
	text = text + "\n";

	/* clean up confluence garbage chars - \\ */
	text = text.replaceAll("\\\\\\s*\\\\", "<br/>");
	text = text.replaceAll("\\\\\\\\", "");
	text = text.replaceAll("\\\\\\\\", "");

	/* Replace escaped brackets - \[ */
	text = text.replaceAll("\\\\\\[", "<nowiki>[</nowiki>");

	/* Replace escape sequences - \ */
	text = text.replaceAll("\\\\(\\S)", "<nowiki>$1</nowiki>");

	/* Replace thumbnail images */
	text = text.replaceAll("\n\\!([^|]+)[|]\\s*thumbnail\\s*\\!\\s*\n", "\n~~ATTACHED_IMAGE_THUMBNAIL~~$1\n");
	text = text.replaceAll("\n\\!\\s*(http[^|]+)?\\!\\s*\n", "\n~~REMOTE_IMAGE~~$1\n");
	text = text.replaceAll("\n\\!([^|]+)?\\!\\s*\n", "\n~~ATTACHED_IMAGE~~$1\n");

	// detect a table-plus
	text = text.replaceAll("\\{table-plus(.*)\\}\n*((.|\n)*?)\n*\\{table-plus\\}", "\n~~TABLEPLUS~~\n$2\n");

	/* detect a table-plus with headers */
	text = text.replaceAll("~~TABLEPLUS~~\n*([|][|].*[|][|])\\s*\n((\\|.*\n)+)\n", "\n<!-- table start -->\n~~TABLESTART~~ border=1  width=\"100%\" cellspacing=\"0\" cellpadding=\"4\" style=\"border-color:#eee\" class=\"wikitable sortable\" \n<!-- header row start -->\n~~HEADERROW~~$1~~HEADEREND~~\n<!-- header row end -->\n$2\n~~TABLEEND~~<!-- table end -->\n\n<br/>\n");

	/* detect a table with headers with split lines within same cell */
	//  text = text.replaceAll("([|][|].*[|][|])\\s*\n(([|][^|}-][^|]+\n[^|]+?[|]\\s*\n)+)", "\n<!-- table start -->\n~~TABLESTART~~ border=1  width=\"100%\" cellspacing=\"0\" cellpadding=\"4\" style=\"border-color:#eee\" class=\"wikitable sortable\"\n<!-- header row start -->\n~~HEADERROW~~$1~~HEADEREND~~\n<!-- header row end -->\n$2\n~~TABLEEND~~<!-- table end -->\n\n<br/>\n");
	REGEX = "([|][|].*[|][|])\\s*\n(([|][^|\\}-][^|]+\n[^|]+?[|]\\s*\n)+)";
	REPLACE =  "\n<!-- table start -->\n~~TABLESTART~~ border=1  width=\"100%\" cellspacing=\"0\" cellpadding=\"4\" style=\"border-color:#eee\" class=\"wikitable sortable\"\n<!-- header row start -->\n~~HEADERROW~~$1~~HEADEREND~~\n<!-- header row end -->\n$2\n~~TABLEEND~~<!-- table end -->\n\n<br/>\n";
	p = Pattern.compile(REGEX, Pattern.UNIX_LINES);
	m = p.matcher(text); 
	text = m.replaceAll(REPLACE);
	/* detect a table with headers */
	REGEX = "\n([|][|].*[|][|])\\s*\n((\\|.*\n)+)\n*";
	REPLACE = "\n<!-- table start -->\n~~TABLESTART~~ border=1  width=\"100%\" cellspacing=\"0\" cellpadding=\"4\" style=\"border-color:#eee\" class=\"wikitable sortable\" \n<!-- header row start -->\n~~HEADERROW~~$1~~HEADEREND~~\n<!-- header row end -->\n$2\n~~TABLEEND~~<!-- table end -->\n\n<br/>\n";
	p = Pattern.compile(REGEX, Pattern.UNIX_LINES);
	m = p.matcher(text); 
	text = m.replaceAll(REPLACE);
	//	  text = text.replaceAll("\n([|][|].*[|][|])\\s*\n(\\|.*\n)+)\n*", "\n<!-- table start -->\n~~TABLESTART~~ border=1  width=\"100%\" cellspacing=\"0\" cellpadding=\"4\" style=\"border-color:#eee\" class=\"wikitable sortable\" \n<!-- header row start -->\n~~HEADERROW~~$1~~HEADEREND~~\n<!-- header row end -->\n$2\n~~TABLEEND~~<!-- table end -->\n\n<br/>\n");

	/* Create table elements in header row*/
	text = text.replaceAll("[|][|]", "!!");

	/* Clean up beginning of header row */
	text = text.replaceAll("\n~~HEADERROW~~\\!\\!", "\n~~HEADERROW~~");

	/* Clean up end of header row */
	text = text.replaceAll("\\!\\!~~HEADEREND~~\n", "~~HEADEREND~~\n");


	text = text.replaceAll("[|] '''\\s*\\n", "|\n");

	// external links
	text = text.replaceAll("\\[(http:\\/\\/[^\\]|]+)\\]", "[$1 $1]");
	text = text.replaceAll("\\[(https:\\/\\/[^\\]|]+)\\]", "[$1 $1]");
	text = text.replaceAll("\\[\\[(http:\\/\\/[^\\]|]+)\\|([^\\]|]+)\\]\\]", "[$1 $2]");
	text = text.replaceAll("\\[\\[(https:\\/\\/[^\\]|]+)\\|([^\\]|]+)\\]\\]", "[$1 $2]");
	text = text.replaceAll("\\[([^\n|]+?)[|]\\s*(https*)([^\n]+?)\\]", "~~LINKSTART~~$2$3 $1~~LINKEND~~");

	// internal links
	text = text.replaceAll("\\[([^\n|]+?)[|]([^\n]+?)\\]", "~~LINKSTART~~$2~~LINKSEPARATOR~~$1~~LINKEND~~");
	text = text.replaceAll("\\[(BeanDev|MODDOCS):(.+?)\\]", "~~LINKSTART~~$2~~LINKEND~~");
	text = text.replaceAll("\\[([^\\]]*)+\\]", "~~LINKSTART~~$1~~LINKEND~~");

	/* detect regular rows that haven"t been detected yet*/
	// ISSUE WITH THIS LINE
	text = text.replaceAll("\n(([|][^|\n}-][^|}]*)+)[|]", "\n~~ROWSTART~~$1");


	/* Internal links with line break*/
	text = text.replaceAll("\\[([^\\]|])?\n([^\\]|])?]\n", "~~LINKSTART~~$1$2~~LINKEND~~<br>");

	/* Internal links without break */
	text = text.replaceAll("\\[([^\\]|]*)?\n([^\\]|])?]", "~~LINKSTART~~$1$2~~LINKEND~~");

	/* detect cells */
	//ISSUE WITH THIS LINE
	text = text.replaceAll("[|]([^|\n}-][^|\n}]*)", "\n~~CELLSTART~~$1");
	// Fix except for files with blank cells
	text = text.replaceAll("~~CELLSTART~~\\W\n", "~~ROWSTART~~");
	text = text.replaceAll("~~CELLSTART~~([^|]*?)[|]", "\n~~CELLSTART~~$1");
	text = text.replaceAll("^[|]-\n[|] '''", "|- style=\"background-color:#f0f0f0;\"\n| \'\'\'"	);

	// formatting
	/*text = text.replace("*(?!*)(.+?)*(?!*)", "'''$1'''");*/
	text = text.replaceAll("(\\W)\\*([^\n*]+?)\\*(\\W)", "$1'''$2'''$3");
	text = text.replaceAll("(\\W)_([\\w][^\n]*?[\\w])_(\\W)", "$1''$2''$3");

	// headings
	REGEX = "^h1. (.+)$";
	REPLACE = "= $1 =";
	p = Pattern.compile(REGEX, Pattern.MULTILINE);
	m = p.matcher(text); 
	text = m.replaceAll(REPLACE);
	REGEX = "^h2. (.+)$";
	REPLACE = "== $1 ==";
	p = Pattern.compile(REGEX, Pattern.MULTILINE);
	// get a matcher object
	m = p.matcher(text); 
	text = m.replaceAll(REPLACE);
	REGEX = "^h3. (.+)$";
	REPLACE = "=== $1 ===";
	p = Pattern.compile(REGEX, Pattern.MULTILINE);
	// get a matcher object
	m = p.matcher(text); 
	text = m.replaceAll(REPLACE);
	REGEX = "^h4. (.+)$";
	REPLACE = "==== $1 ====";
	p = Pattern.compile(REGEX, Pattern.MULTILINE);
	// get a matcher object
	m = p.matcher(text); 
	text = m.replaceAll(REPLACE);
	REGEX = "^h5. (.+)$";
	REPLACE = "===== $1 =====";
	p = Pattern.compile(REGEX, Pattern.MULTILINE);
	// get a matcher object
	m = p.matcher(text); 
	text = m.replaceAll(REPLACE);
	REGEX = "^h6. (.+)$";
	REPLACE = "====== $1 ======";
	p = Pattern.compile(REGEX, Pattern.MULTILINE);
	// get a matcher object
	m = p.matcher(text); 
	text = m.replaceAll(REPLACE);

	/* cleanup */
	text = text.replaceAll("~~LINKSEPARATOR~~", "|");
	text = text.replaceAll("~~STARTBRACKET~~", "[");
	text = text.replaceAll("~~LINKSTART~~", "[[");
	text = text.replaceAll("~~ENDBRACKET~~", "]");
	text = text.replaceAll("~~LINKEND~~", "]]");
	text = text.replaceAll("~~BACKSLASH~~", "\\");
	text = text.replaceAll("~~TABLESTART~~", "{|");
	text = text.replaceAll("~~TABLEEND~~", "|}");
	text = text.replaceAll("~~TABLEPLUS~~", "");
	text = text.replaceAll("~~HEADERROW~~", "!");
	text = text.replaceAll("~~HEADEREND~~", "");
	text = text.replaceAll("~~ROWSTART~~", "|-");
	text = text.replaceAll("\n+~~CELLSTART~~", "\n| ");
	text = text.replaceAll("~~STAR~~", "*");

	/* clean up multiple newlines */
	text = text.replaceAll("\n+[|][\\}]", "\n|\\}");
	text = text.replaceAll("\n+[|][-]\n+[|]", "\n|-\n|");
	text = text.replaceAll("\n[|][-]\n[|][-]\n", "\n|-\n");
	text = text.replaceAll("[\n\\s]+[{][|]", "\n{|");
	text = text.replaceAll("[\n\\s]+[!]([^!])", "\n!$1");
	/*text = text.replaceAll("\n[|]\n+[|]([^\\}-])", "\n|$1");*/    

	return text;
    }

    private static List<String> getFileList(String URL) throws IllegalStateException, IOException{
	List <String> matches = new ArrayList <String> ();
	List <Pattern> patterns = new ArrayList <Pattern> ();
	BufferedReader buf = null;
	String match = null;
	patterns.add (Pattern.compile ("class=\"p\">(.*?)</a>"));
	HttpClient client = new DefaultHttpClient();
	HttpGet httpget = new HttpGet(URL);
	HttpResponse response = client.execute(httpget);
	HttpEntity entity = response.getEntity();
	if (entity != null) {
	    try {
		InputStream inputStream = (InputStream) entity.getContent ();
		InputStreamReader isr = new InputStreamReader (inputStream);
		buf = new BufferedReader (isr);
		String str = null;
		while ((str = buf.readLine ()) != null){
		    for (Pattern p : patterns){
			Matcher m = p.matcher (str);
			while (m.find ()) {
			    match = m.group();
			    match = match.replace("class=\"p\">","");
			    match = match.replace("</a>","");
			    matches.add (match);
			}
		    }
		} 
	    }
	    finally{
		buf.close();
	    }
	}
	return matches;
    }

    private static String getConfluence(String URL)throws IllegalStateException, IOException{
	HttpClient client = new DefaultHttpClient();
	HttpGet httpget = new HttpGet(URL);
	HttpResponse response = client.execute(httpget);
	HttpEntity entity = response.getEntity();
	InputStream inputStream = (InputStream) entity.getContent ();
	String text = IOUtils.toString(inputStream);
	return text;
    }

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
	// TODO Auto-generated method stub
	if (args.length != 1) {
	    System.out.println ("Please enter correct URL!");
	    System.exit(0);
	}
	//	File file_in = new File(args[0]);
	List<String> conFiles = getFileList(args[0]);
	int pathStart = args[0].indexOf("source/xref/")+12;
	int pathEnd = args[0].indexOf("src/site/confluence/");
	String folderPath = args[0].substring(pathStart, pathEnd);
	String url = args[0].replace("xref","raw");
	for (int i = 0; i < conFiles.size();i++){
	    File file_out = new File("C:/Confluence/"+folderPath+conFiles.get(i).replace(".confluence", "")+".mw");
	    FileUtils.writeStringToFile(file_out, convert(getConfluence(url+conFiles.get(i))));
	}
	//String input = FileUtils.readFileToString(file_in);

    }
}

