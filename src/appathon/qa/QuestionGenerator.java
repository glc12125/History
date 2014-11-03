package appathon.qa;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import android.content.Context;


public class QuestionGenerator {
	
	private static String[] QUESTION_KINDS = new String[]{"ChooseCountry", "ChooseCity", "ChooseOption"};

	private Context context;
	
	public QuestionGenerator(Context context){
		this.context = context;
	}
	
	public ArrayList<Question> getQuestions(int count){
		ArrayList<Question> questions = new ArrayList<Question>();
		
		try {
			InputStream is = context.getAssets().open("modern.hst");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
	        
			String line = br.readLine();
	        while (line != null) {
	            String[] fields = line.split("\t");
	            if(fields.length >= 7){
	            	String question = fields[0];
	            	String country = fields[1];
	            	String kind = QUESTION_KINDS[Integer.parseInt(fields[6])];
	            	ArrayList<Answer> options = new ArrayList<Answer>();
	            	for(int i = 2; i <= 5; i++){
	            		options.add(new Answer(fields[i], kind != "ChooseOption"));
	            	}
	            	String correctAnswer = options.get(0).answer;
	            	Collections.shuffle(options);
	            	Question questionObj = new Question(question, null, options, correctAnswer, country, kind);
	            	questions.add(questionObj);
	            }
	            line = br.readLine();
	        }
	        br.close();
	        
	        if (count < questions.size()){
	        	List<Integer> indices = new ArrayList<Integer>(questions.size());
	        	for(int i = 0; i < questions.size(); i++){
	        		indices.add(i);
	        	}
	        	Collections.shuffle(indices);
	        	indices = indices.subList(0, count);
	        	Collections.sort(indices);
	        	ArrayList<Question> oldQuestions = questions;
	        	questions = new ArrayList<Question>(count);
	        	ListIterator<Integer> indexIterator = indices.listIterator();
	        	while(indexIterator.hasNext()){
	        		questions.add(oldQuestions.get(indexIterator.next()));
	        	}
	        }
	        
	    } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    return questions;
	}

}
