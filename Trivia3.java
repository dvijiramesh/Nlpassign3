package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class Trivia3 {

	ArrayList<String> list_words = new ArrayList<String>();
	
	ArrayList<Features> word_list = new ArrayList<Features>();
	ArrayList<Features> test_word_list = new ArrayList<Features>();

	
	ArrayList<String> test_list_words = new ArrayList<String>();
	ArrayList<String> list_uniq_words = new ArrayList<String>();
	HashMap<String,Features> map_words_features = new HashMap<String,Features>();
	
    HashMap<String,Float> map_tagfeature = new HashMap<String,Float>();
    HashMap<String,Float> map_tagcount = new HashMap<String,Float>();
    HashMap<String,Integer> map_wordtagcount = new HashMap<String,Integer>();
    HashMap<String,Integer> map_uniqueword = new HashMap<String,Integer>();
    
	ArrayList<String> list_pos = new ArrayList<String>();
	ArrayList<String> test_list_pos = new ArrayList<String>();
	ArrayList<String> calc_list_pos = new ArrayList<String>();
	Pattern p = Pattern.compile("[^a-z0-9 ]",Pattern.CASE_INSENSITIVE);
	String Start = "STAR";
	String Stop = "STOP";
	int uniq;
	int totwords;
	int ct1;
	String pos_for_unq = "";


	HashMap<String,HashMap<String,Integer>> map_words_pos = new HashMap<String,HashMap<String,Integer>>();
	HashMap<String,Integer> map_pos = new HashMap<String,Integer>();
	//String[] pos = new String[] { "CC", "CD", "DT", "EX","FW","IN","JJ","JJR","JJS","LS","MD","NN","NNS","NNP","NNPS","PDT","POS","PRP","PRP$","RB","RBR","RBS","RP","SYM","TO","UH","VB","VBD","VBG","VBN","VBP","VBZ","WDT","WP","WP$","WRB"};
    HashMap<String,Integer> innermap = new HashMap<String,Integer>();

	public void readFile() throws IOException{
		File file1 = new File("C:\\Users\\Vijayalakshmi\\Desktop\\nlp\\Assign2\\test_tri3.txt");
		FileWriter fw = new FileWriter(file1.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		

		if (!file1.exists()) {
			file1.createNewFile();
		}
		File file = new File("C:\\Users\\Vijayalakshmi\\Desktop\\nlp\\Assign2\\wsj-test.txt");

	    System.out.println("****************TRAIN FILE PRCESSING STARTS***********");
	    String mapkey;
	    float feacount=0;
	    
		String wholetextdata = FileUtils.readFileToString(file);
		String[] sentences = wholetextdata.split("\\.\\s\\.");
		
		//System.out.println("Printing Sentences");
		
	    for (int i = 0;i<sentences.length;i++){
	        String[] words = sentences[i].trim().split("\\s+");
	    
	    
	        list_words.clear();
	    		for (int j=0; j<words.length;j++){
	    			list_words.add(words[j]);
		    		}

		        //System.out.println(sentences[i].trim() + " - " + words.length + " " + list_words.size());
	    		
	    		for (int k=1;k<list_words.size();k += 2){
	    			
	    			
	            	Features wordfeature =createfeaturevector(words[k-1],words[k]); 
	            	if ((k-1)==0){wordfeature.prevtag = Start;}
	            	else {
	            		wordfeature.prevtag = words[k-2];
	            	}
	            	
	            	
	            	if (k==list_words.size()-1){
	            		wordfeature.nexttag = Stop;
	            	}
	            	
	            	else if ((k!= (list_words.size()-1)) || ((k!= (list_words.size())))){
	            		wordfeature.nexttag = words[k+2];
	            	}
	            	
	            	word_list.add(wordfeature);
	            	
	             	//System.out.println(wordfeature.iswordtag + " " + wordfeature.word + " " + wordfeature.iswordpunct +" " +wordfeature.iswordupper + " " +wordfeature.iswordnumeric + " " + wordfeature.iswordtitle + " " +wordfeature.prevtag + " " +wordfeature.nexttag);
	            	//System.out.println("\r\n");

	            //	System.out.println(" word-is " + wordfeature.word + " Prev-tag  " + wordfeature.prevtag + " Next-tag  " + wordfeature.nexttag + " cur-tag " + wordfeature.iswordtag +  " title- " +wordfeature.iswordtitle );
	             	bw.write(wordfeature.iswordtag + " " + wordfeature.word + " " + wordfeature.iswordpunct + " " +wordfeature.prevtag);

	            	bw.write("\r\n");

	    			}}
	bw.close();    
	}
	    /*	
	    	for (int j = 0; j<pos.length;j++){ 
	            if( words[i].equals(pos[j])){
	            	Features wordfeature =createfeaturevector(words[i-1],words[i]); 
	            	word_list.add(wordfeature);
	            	//System.out.println(wordfeature.iswordtag + " word-is-" + wordfeature.word + " title-" +wordfeature.iswordtitle );
	            	//bw.write(wordfeature.iswordtag +  " " + wordfeature.word + " " + wordfeature.iswordnumeric+" "+wordfeature.iswordpunct+" "+wordfeature.iswordtitle+ " " +wordfeature.iswordupper);
	            	
	            	bw.write(wordfeature.iswordtag +   wordfeature.iswordpunct +" " +wordfeature.iswordupper);

	            	bw.write("\r\n");
	            	
	            	mapkey = words[i-1].concat(words[i]);
	            	if(!map_wordtagcount.containsKey(mapkey)){
	            		map_wordtagcount.put(mapkey, 1);
	            	}else{
	            		Integer cnt = map_wordtagcount.get(mapkey) + 1;
	            		map_wordtagcount.put(mapkey, cnt);
	            	}
	           }
	    	}
	    }
	    bw.close();

	    System.out.println("Total Word count   " + word_list.size());
	    System.out.println("Total Unique Word count   " + map_uniqueword.size());
	    System.out.println("Total Unique Word-Tag count   " + map_wordtagcount.size());
	    
	    System.out.println( "Calculating Feature count");
	    
    	for (int j = 0; j<pos.length;j++){ 
    		map_tagcount.put(pos[j], gettagcount(pos[j]));
	    
	    	mapkey = pos[j].concat("NUMERIC0");
	    	feacount = getfeaturecount("0",pos[j],"NUNERIC");
	    	map_tagfeature.put(mapkey, feacount);
	
	    	mapkey = pos[j].concat("NUMERIC1");
	    	feacount = getfeaturecount("1",pos[j],"NUNERIC");
	    	map_tagfeature.put(mapkey, feacount);
	    	
	    	mapkey = pos[j].concat("PUNCT0");
	    	feacount = getfeaturecount("0" ,pos[j],"PUNCT");
	    	map_tagfeature.put(mapkey, feacount);
	
	    	mapkey = pos[j].concat("PUNCT1");
	    	feacount = getfeaturecount("1",pos[j],"PUNCT");
	    	map_tagfeature.put(mapkey, feacount);
	
	    	mapkey = pos[j].concat("TITLE0");
	    	feacount = getfeaturecount("0",pos[j],"TITLE");
	    	map_tagfeature.put(mapkey, feacount);
	    	
	    	mapkey = pos[j].concat("TITLE1");
	    	feacount = getfeaturecount("1",pos[j],"TITLE");
	    	map_tagfeature.put(mapkey, feacount);
	    	
	
	    	mapkey = pos[j].concat("UPPER0");
	    	feacount = getfeaturecount("0",pos[j],"UPPER");
	    	map_tagfeature.put(mapkey, feacount);
	    	
	    	mapkey = pos[j].concat("UPPER1");
	    	feacount = getfeaturecount("1",pos[j],"UPPER");
	    	map_tagfeature.put(mapkey, feacount);

    	}
    
	    System.out.println("****************TRAIN FILE PROCESSINF DONE ***********");
}
	
	
	public void writefile(){
		File file = new File("C:\\Users\\dhamov1\\Desktop\\School_stuff\\nlp\\Assign2\\maxent_train.txt");
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		

		if (!file.exists()) {
			file.createNewFile();
		}

		for( int i =0 ;i<)
		
		bw.write(content);
		bw.close();

		System.out.println("Done");

		
	}*/

	public void test() throws IOException{
		File file1 = new File("C:\\Users\\Vijayalakshmi\\Desktop\\nlp\\Assign2\\maxent_test01.txt");
		FileWriter fw = new FileWriter(file1.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		

		if (!file1.exists()) {
			file1.createNewFile();
		}
		File file = new File("C:\\Users\\Vijayalakshmi\\Desktop\\nlp\\Assign2\\wsj-test.txt");
	    String test_wholetextdata = FileUtils.readFileToString(file);
	    String[] test_words = test_wholetextdata.split("\\s+");
	    
	    double prob;
	    float correct = 0;
	    float accuracy;
	    float f1, f2,f3,f4,f5;
	    float f1_num,f2_num,f3_num,f4_num,f5_num;
	    double logf1, logf2,logf3,logf4,logf5;
	    double maxprob;
	    float tagcount_den=0;
	    
	    String mapkey;
	    
	    System.out.println("**************** TEST FILE PRCESSING STARTS ***********");
	    
	    for (int i = 1;i<test_words.length;i++){
	    	for (int j = 0; j<pos.length;j++){ 
	            if( test_words[i].equals(pos[j])){
//	            	String word = words[i-1];
	            	Features wordfeature =createfeaturevector(test_words[i-1],test_words[i]); 
	            	test_word_list.add(wordfeature);
	            	//bw.write(wordfeature.iswordtag +  " " + wordfeature.word + " " + wordfeature.iswordnumeric+" "+wordfeature.iswordpunct+" "+wordfeature.iswordtitle+ " " +wordfeature.iswordupper);
	            	bw.write(wordfeature.iswordtag +  " " + wordfeature.iswordpunct+" " +wordfeature.iswordupper);

	            	bw.write("\r\n");

	           }
	    	}
	    }
	    bw.close();
	    System.out.println("TEST FILE SIZE - " + test_words.length + " - " + test_word_list.size());
	    
	    for (int i=0;i<test_word_list.size();i++) {

	    	Features feature = 	test_word_list.get(i);

	    //	System.out.println("Word : " + feature.word );
	    	
	    	maxprob = -100;
	    	
	    	for( int j = 0; j<pos.length;j++){
	    		//System.out.println ("Word   "+ test_word_list.get(i).word + "   " + pos[j]);
		
	    		f1_num=0;f2_num=0;f3_num=0;f4_num=0;f5_num=0;
	    		
			    	f1_num = getfeaturecount_word(feature.word,pos[j]);
			    	tagcount_den = map_tagcount.get(pos[j]);
			    	
			    	
			    	mapkey = pos[j].concat("NUMERIC").concat(feature.iswordnumeric);
			    	
			    	if(map_tagfeature.containsKey(mapkey)){
			    		f2_num = map_tagfeature.get(mapkey);
			    	}else{
			    		//f2_num = getfeaturecount(feature.iswordnumeric ,pos[j],"NUNERIC");
			    		//map_tagfeature.put(mapkey, f2_num);
			    		System.out.println("ERROR NUMERIC " + feature.iswordnumeric + " (" + feature.word + ")");
			    	}
			    	
			    	mapkey = pos[j].concat("PUNCT").concat(feature.iswordpunct);
			    	
			    	if(map_tagfeature.containsKey(mapkey)){
			    		f3_num = map_tagfeature.get(mapkey);
			    	}else{

			    		//f3_num = getfeaturecount(feature.iswordpunct ,pos[j],"PUNCT");
			    		//map_tagfeature.put(mapkey, f3_num);
			    		System.out.println("ERROR PUNCT " + feature.iswordpunct  + " (" + feature.word + ")");
			    	}
			    	
			    	mapkey = pos[j].concat("TITLE").concat(feature.iswordtitle);

			    	if(map_tagfeature.containsKey(mapkey)){
			    		f4_num = map_tagfeature.get(mapkey);
			    	}else{

			    		//f3_num = getfeaturecount(feature.iswordpunct ,pos[j],"PUNCT");
			    		//map_tagfeature.put(mapkey, f3_num);
			    		System.out.println("ERROR TITLE " + feature.iswordtitle  + " (" + feature.word + ")");
			    	}
			    			
			    	mapkey = pos[j].concat("UPPER").concat(feature.iswordupper);
			    	
			    	if(map_tagfeature.containsKey(mapkey)){
			    		f5_num = map_tagfeature.get(mapkey);
			    	}else{

			    		//f3_num = getfeaturecount(feature.iswordpunct ,pos[j],"PUNCT");
			    		//map_tagfeature.put(mapkey, f3_num);
			    		System.out.println("ERROR UPPER " + feature.iswordupper  + " (" + feature.word + ")");
			    	}
		    	
			    	//System.out.println(" Tag " + pos[j] + "  " + f1_num + "  " + f2_num + " " +f3_num +"  "+f4_num + "  " +f5_num);
			    	
			    	logf1=0; logf2=0;logf3=0;logf4=0;logf5=0;
			    	
			   // 	if(wordexists(test_word_list.get(i).word)){
			    	if(map_uniqueword.containsKey(test_word_list.get(i).word)){
				    	f1 = (f1_num +1)/(tagcount_den + (2 * 1));
				    	logf1 = Math.log(f1);
			
				    	f2 = (f2_num +1)/(tagcount_den + (2 * 1));
				    	logf2 = Math.log(f2);	    	
				    	
				    	f3 = (f3_num +1)/(tagcount_den + (2 * 1));
				    	logf3 = Math.log(f3);	    	
				    	
				    	f4 = (f4_num +1)/(tagcount_den + (2 * 1));
				    	logf4 = Math.log(f4);	    	
			
				    	f5 = (f5_num +1)/(tagcount_den + (2 * 1));
				    	logf5 = Math.log(f5);	    	
			    	}else
			    	{
			    		//System.out.println("ELSE PART ***** ");
			    		
					    	f1 = (1)/(tagcount_den + (2 * 1));
					    	logf1 = Math.log(f1);
				
					    	f2 = (1)/(tagcount_den + (2 * 1));
					    	logf2 = Math.log(f2);	    	
					    	
					    	f3 = (1)/(tagcount_den + (2 * 1));
					    	logf3 = Math.log(f3);	    	
					    	
					    	f4 = (1)/(tagcount_den + (2 * 1));
					    	logf4 = Math.log(f4);	    	
				
					    	f5 = (1)/(tagcount_den + (2 * 1));
					    	logf5 = Math.log(f5);	    	
				    	}
			    	
			    	//System.out.println("Feature cts "+ feature.word + " Tag " + pos[j] + "  " + f1_num + "  " + f2_num + " " +f3_num +"  "+f4_num + "  " +f5_num);
			    	//prob = logf1 + logf2 + logf3 + logf4 + logf5;
			    	prob = f1*f2*f3*f4*f5;
			    	prob = prob * (tagcount_den/test_word_list.size());
			    	
			    	feature.logprob = prob;
			    	if (prob > maxprob) {
			    		maxprob = prob;
			    		feature.calctag = pos[j];
			    	}
			    	
	    	}	

	    	if (feature.calctag.equals(feature.iswordtag)){
	    		correct++;
	    	}

	   // System.out.println("Tag : " + feature.iswordtag + " - " +feature.calctag);
	    
	}
System.out.println("Correct word  " + correct);
	    accuracy = correct/test_word_list.size();
	    
	    System.out.println("Accuracy  " + accuracy);
	    
	    System.out.println("****************TEST FILE PRCESSING DONE***********");
	    
	}

	private float getfeaturecount_word(String word, String tag){
		float wordcount=0;

//	    for (int i=0;i<word_list.size();i++) {
	    	
//		    	if(word_list.get(i).word.equals(word) && word_list.get(i).iswordtag.equals(tag)){
//		    		wordcount +=1;
//		    	}
			
//			}
		String mapkey = word.concat(tag);
		if(map_wordtagcount.containsKey(mapkey)){
				wordcount = map_wordtagcount.get(mapkey);
		}else{
			wordcount=0;
		}
		
	    return wordcount;
	}
	
	private float getfeaturecount(String word, String tag, String feature){
		float wordcount=0;

	    for (int i=0;i<word_list.size();i++) {
	    	
			if(feature.equals("WORD")){
		    	if(word_list.get(i).word.equals(word) && word_list.get(i).iswordtag.equals(tag)){
		    		wordcount +=1;
		    	}
			}
			
			if(feature.equals("NUNERIC")){
			    	if(word_list.get(i).iswordnumeric.equals(word) && word_list.get(i).iswordtag.equals(tag)){
			    		wordcount +=1;
			    	}
			}
			
		    if(feature.equals("PUNCT")){
			    	if(word_list.get(i).iswordpunct.equals(word) && word_list.get(i).iswordtag.equals(tag)){
			    		wordcount +=1;
			    	}
			}
		    
		    if(feature.equals("TITLE")){
		    	if(word_list.get(i).iswordtitle.equals(word) && word_list.get(i).iswordtag.equals(tag)){
			    		wordcount +=1;
		    	}				    	
			}
		    
		    if(feature.equals("UPPER")){
		    	if(word_list.get(i).iswordupper.equals(word) && word_list.get(i).iswordtag.equals(tag)){
		    		wordcount +=1;
		    	}
			}
			}
	    return wordcount;
	}
	
	private float gettagcount(String tag){
		float tagcount=0;
	    for (int i=0;i<word_list.size();i++) {
	    	if(word_list.get(i).iswordtag.equals(tag)){
	    		tagcount +=1;
	    	}
	    }
	    return tagcount;
	}
		

	private Features createfeaturevector(String word,String tag){
		Matcher m = p.matcher(word);
		boolean b = m.find();
    	Features feature = new Features();
    	feature.word=word;
    	feature.iswordtag=tag;
    	
 //   	if(word.matches("((-|\\+)?[0-9]+(\\.[0-9]+)?)+")) {
    	if(word.matches("[0-9]+")){
  //  		System.out.println("NUmeric");
    		feature.iswordnumeric="1";
    	}
    	
    	if (Character.isUpperCase(word.charAt(0))==true){
    		feature.iswordtitle="1";
    	}
    	
    	if (StringUtils.isAllUpperCase(word)){
    		feature.iswordupper="1";
    	}
    	
    	if(b){
    		feature.iswordpunct="1";
    	}
    	
    	
		return feature;
	}
	
	
	boolean wordexists(String word) {
		
		for (int i=0;i<word_list.size();i++){
			if (word.equals(word_list.get(i).word)){
				return true;
			}
			
		}
		return false;
		
		
	}
	

	public static void main(String[] args) throws IOException {
		Trivia3 n = new Trivia3();
        n.readFile();
     //  n.test();
       
		
	}

}
