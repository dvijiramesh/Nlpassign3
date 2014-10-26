package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;


public class Trivia {

	ArrayList<String> listwords = new ArrayList<String>();
	ArrayList<String> listwords_withstop = new ArrayList<String>();
	ArrayList<String> testlistwords_withstop = new ArrayList<String>();
	ArrayList<String> lowercasewords = new ArrayList<String>();
	ArrayList<String> total_countlowercasewords = new ArrayList<String>();
	ArrayList<String> testlistwords = new ArrayList<String>();
	float vocabsize;
	int testvocabsize;
	int unigram_vocabsize;
	double totprob ;
	double totprob1;
	float tot_loglikely;
	
	float accuracy;

	float likelihood;
	float loglikelihood;
	String Start = "<S>";
	String Stop = "</S>";
	String Unknown = "<UNK>";
	float testprobability;
	int count1;
	int counttest;
	float totalprob = 0;
	float prob_smooth;
	double log_prob; 
	HashMap<String,Bigram> words_counts_map = new HashMap<String,Bigram>();
	HashMap<String,Bigram> testwords_counts_map = new HashMap<String,Bigram>();
	HashMap<String,Integer> unigram_counts_map = new HashMap<String,Integer>();

	HashMap<String,Bigram> wordprob_map = new HashMap<String,Bigram>();
	HashMap<String,Bigram> testwordprob_map = new HashMap<String,Bigram>();


	int count;
	public void ReadFile(String trainfilename) throws IOException {
		Properties p = new Properties();
		File file = new File(trainfilename);
				
	    String wholetextdata = FileUtils.readFileToString(file);
	    String[] sentences = wholetextdata.split("\\.");

    	System.out.println("TRAIN FILE UNDER PROGRESS");
	    System.out.println("Sentenses size " + sentences.length);
	    
	    for (int i = 0;i<sentences.length;i++){
	    	
	    	String[] words = sentences[i].trim().split("\\s+");
            listwords.clear();
	    	listwords.add(Start);

	    		for (int j=0; j<words.length;j++){
	    			listwords.add(words[j]);
	    			listwords_withstop.add(words[j]);
	    	//		System.out.println(words[j]) ;
		    		}
	    		listwords.add(Stop);
	    		for (int k=0;k<listwords.size();k++){
					listwords.set(k, listwords.get(k).toLowerCase());
	    			}
	    		
	    for (int m=1; m<listwords.size();m++){
	    	
	    		String currentword = listwords.get(m);
	    		String prevword = listwords.get(m-1);
	    		String key = currentword.concat(prevword);

	    		if(!unigram_counts_map.containsKey(listwords.get(m-1)))	{
	    		
	    		unigram_counts_map.put(listwords.get(m-1), 1);
	    		unigram_vocabsize = unigram_vocabsize+1;
	    		} 

	    		else {
	    		count1 = unigram_counts_map.get(listwords.get(m-1)) + 1;
	    		unigram_counts_map.remove(listwords.get(m-1));
	    		unigram_counts_map.put(listwords.get(m-1), count1);
    	
	    		}
 
	    	if(!words_counts_map.containsKey(key)){	    		
	    		Bigram b = new Bigram(currentword,prevword);
	    		b.setcount();
	    		words_counts_map.put(key, b);
	    		vocabsize = vocabsize+1;
	    		} 

	    	else {
	    		
	    		Bigram b1 = words_counts_map.get(key);
	    		b1.setcount();
	    		words_counts_map.put(key,b1);
	    	
	    	}
	    }
	    }

	    System.out.println("Bigram vocab size......................." + vocabsize);	
	  	System.out.println( "NUMBER OF SENTENCES                  "   + sentences.length) ;
    	System.out.println("***********      TRAIN FILE PROGRESS DONE *********** ") ;
	}
	
	
	
	public void test(String testfilename) throws IOException{
		Properties p = new Properties();
		File file = new File(testfilename);
		
	    String wholetextdata = FileUtils.readFileToString(file);
	    String[] testsentences = wholetextdata.split("\\.");

	    System.out.println("TEST FILE UNDER PROGRESS");
	    System.out.println("TEST FILE SENTENCES LENGTH - " + testsentences.length);
	    
	    System.out.println("alpha -   tot_loglikely" ); 
	    
	   // float alpha = 50;
		  // while (alpha <100){
			 //  tot_loglikely = (float) 0.0;
			   //alpha = (float) (alpha +0.00001);
	    for (int i = 0;i<testsentences.length;i++){
	    	String[] words = testsentences[i].trim().split("\\s+");
            testlistwords.clear();
	    	testlistwords.add(Start);

	    		for (int j=0; j<words.length;j++){
	    			testlistwords.add(words[j]);
	    			testlistwords_withstop.add(words[j]);
	    	//		System.out.println(words[j]) ;
		    		}
	    		testlistwords.add(Stop);
	    		for (int k=0;k<testlistwords.size();k++){
					testlistwords.set(k, testlistwords.get(k).toLowerCase());
	    			}

	    
	    for (int m=1; m<testlistwords.size();m++){
	    		String currentword = testlistwords.get(m);
	    		String prevword = testlistwords.get(m-1);
	    		String key = currentword.concat(prevword);
	    	
		    if(words_counts_map.containsKey(key) ){
		    	float count;
		    	int freq = words_counts_map.get(key).getcount();
		    	String pre_word = words_counts_map.get(key).word2; 
		    //	if(unigram_counts_map.containsKey(pre_word))	{
	    		 count =  unigram_counts_map.get(pre_word);
	    		 if (count >= 2){
	    		prob_smooth = (freq +1) /(count + (1 * vocabsize));
	    		log_prob = Math.log(prob_smooth)/Math.log(2);
	    		//}
	    		 }
	    		 
	    		 else{
			    		prob_smooth = 1/(1 * vocabsize);
			    		log_prob = Math.log(prob_smooth)/Math.log(2);}
	
		    	}
		    	else{
		    		prob_smooth = 1/(1 * vocabsize);
		    		log_prob = Math.log(prob_smooth)/Math.log(2);
		    	}
		    
		    
	    tot_loglikely += log_prob ;
	    
	    }
	 }
	    System.out.println("total loglikelyhood"  +  "  -   " + tot_loglikely );   
		   }
	
	
	public void goodbad(String transfilename) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(transfilename));
		//File file = new File("C:\\Users\\dhamov1\\Desktop\\nlp\\Assign1\\good-bad.txt");

		String line = null;
		float correct = 0;
		int linenum = 0;
		double prob_words = 0;
		int senten_size;
		int senten_size1;
		int size = 0;
		float log_prob_sen = (float) 0.0;
		ArrayList<String> goodwords = new ArrayList<String>();
		ArrayList<String> badwords = new ArrayList<String>();
		ArrayList<Float> goodwords_prob = new ArrayList<Float>();
		ArrayList<Float> badwords_prob = new ArrayList<Float>();
		ArrayList<Integer> good_sen_size = new ArrayList<Integer>();
		ArrayList<Integer> bad_sen_size = new ArrayList<Integer>();


		while ((line = br.readLine()) != null && line.trim() != ""){
			linenum++;
		
		//String wholetextdata = FileUtils.readFileToString(file);
	   // String[] sentences = wholetextdata.split("\\.");
	    //for (int k = 0;k<sentences.length;k++){
			String[] wordarrays = line.trim().split("\\s+");
	    	//String[] wordarrays = sentences[k].trim().split("\\s+");

			int len = wordarrays.length;
			
			if (linenum % 2 != 0){
				//System.out.println("Line number    " +linenum);
				goodwords.clear();
				log_prob_sen = 0;
				goodwords.add(Start);
			for (int i = 0; i< len; i++){
					goodwords.add(wordarrays[i]);
					
			}
			goodwords.add(Stop);
			
			senten_size = goodwords.size();
			good_sen_size.add(senten_size);
			
			for (int i = 1;i<goodwords.size();i++){
				goodwords.set(i, goodwords.get(i).toLowerCase());
				String currentword = goodwords.get(i);
	    		String prevword = goodwords.get(i-1);
	    		String key = currentword.concat(prevword);
	    		if(words_counts_map.containsKey(key)){
			    	float count;
			    	int freq = words_counts_map.get(key).getcount();
			    	String pre_word = words_counts_map.get(key).word2; 
			    	if(unigram_counts_map.containsKey(pre_word))	{
			    		
			    		 count =  unigram_counts_map.get(pre_word);}
			    	else{count = 0;}
			    		
			    		prob_words = (float) (freq +1) / (float) (count + vocabsize);
			    		log_prob_sen = (float) (Math.log(prob_words)/ (float) Math.log(2));

			    	}
			    	else{
			    		prob_words = (float) ((1.0) /(float )(count + vocabsize));
			    		log_prob_sen = (float) (Math.log(prob_words)/(float) Math.log(2));

			    	}
			    	
	    		log_prob_sen += log_prob_sen;
	    		//prob_words *= prob_words;
	    		}
			goodwords_prob.add(log_prob_sen);
			//prob_words =prob_words *100000000000.0;
			//goodwords_prob.add((float) prob_words);
			}
		
			else {
				
			//	System.out.println("Line number    " +linenum);
				badwords.clear();
				log_prob_sen = 0;
				badwords.add(Start);
			for (int i = 0; i< len; i++){
					badwords.add(wordarrays[i]);
					
			}
			badwords.add(Stop);
			senten_size1 = badwords.size();
			bad_sen_size.add(senten_size1);
			
			for (int i = 1;i<badwords.size();i++){
				badwords.set(i, badwords.get(i).toLowerCase());
				String currentword = badwords.get(i);
	    		String prevword = badwords.get(i-1);
	    		String key = currentword.concat(prevword);
	    		if(words_counts_map.containsKey(key)){
			    	float count;
			    	int freq = words_counts_map.get(key).getcount();
			    	String pre_word = words_counts_map.get(key).word2; 
			    	if(unigram_counts_map.containsKey(pre_word))	{
			    		
			    		 count =  unigram_counts_map.get(pre_word);}
			    	else{count = 0;}
			    		//System.out.println("FREQUENCY>>>>>>" + freq);
			    		prob_words = (float) (freq +1) /(float)(count + vocabsize);
			    		//log_prob_sen = (float) (Math.log(prob_words)/ (float)Math.log(2));
			    		log_prob_sen = (float) (Math.log(prob_words));

			    	}
			    	else{
			    		
			    		prob_words = (float)(1) /(float)( vocabsize);
			    		log_prob_sen = (float) (Math.log(prob_words)/(float)Math.log(2));
			    		//log_prob_sen = (float) (Math.log(prob_words));

			    	}
			    	
	    log_prob_sen += log_prob_sen;
	    	//	prob_words *= prob_words;
	    		}
			badwords_prob.add(log_prob_sen);
			//prob_words =prob_words *100000000000.0;
		//	badwords_prob.add((float) prob_words);
			}
		}
		
		for ( int i = 0; i<bad_sen_size.size();i++){
			//System.out.println("sentence size of bad words" + bad_sen_size.get(i) + "Sentence size of good" + good_sen_size.get(i));
		}
		for ( int i = 0; i<goodwords_prob.size();i++){
			//System.out.println("good words prob file" + goodwords_prob.get(i));
		}
		
		for ( int i = 0; i<badwords_prob.size();i++){
			//System.out.println("bad words prob file" + badwords_prob.get(i));
		}
		
		for ( int i = 0; i<goodwords_prob.size();i++){
			if (good_sen_size.get(i) <= bad_sen_size.get(i)){
				size++;
			}
				if ((goodwords_prob.get(i)) >= (badwords_prob.get(i))){
					
					
					correct++;
				}
				else {
					
					System.out.println ("Few lines of wrong classification  " + i);
				}
				System.out.println( "Good sentence size smaller than or equal    " +size);

		}
		
	accuracy = (correct/ goodwords_prob.size()) ;
	
		//System.out.println(" CORRECT "  + correct + "        linenum" + sentences.length);
		System.out.println("Total accuracy of Bigram model is     " + accuracy + "%");
		System.out.println (" Size of 2 arrays   " +goodwords_prob.size() + " - " + badwords_prob.size());
	}

	public static void main(String[] args) throws IOException {
		String trainfilename = args[0];
		String testfilename = args[1];
		String transfilename = args[2];
		Trivia main = new Trivia();
		main.ReadFile(trainfilename);
		main.test(testfilename);
	  main.goodbad(transfilename);
		
	}

}
