package ralfs.de.ipk_gatersleben.bit.bi.edal.examples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;

public class TextDataBase {
	ArrayList<String> wordList;
	File db;
	public TextDataBase() {
		wordList = new ArrayList<>();
		try {
			db = new File(System.getProperty("user.home"), "rest_exampleDB.txt");
			if(db.createNewFile()) {
				PrintWriter out = new PrintWriter(db.getPath());
				out.println("example");
				out.close();
				wordList.add("example");
			}else {
				Scanner sc = new Scanner(db);

		        while (sc.hasNextLine()) {
		            String word = sc.nextLine();
		            wordList.add(word);
		        }
		        sc.close();
			}

		}
			catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> getData() {
		return wordList;
	}

	public void append(String msg) {
		wordList.add(msg);
		PrintWriter out = null;
		try {
			out = new PrintWriter(db.getPath());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		for(String s : wordList) {
			out.println(s);
		}
		out.close();
	}
}
