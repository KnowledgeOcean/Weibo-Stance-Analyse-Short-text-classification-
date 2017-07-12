package com.wangyl.tools;

import java.util.HashMap;

import com.wangyl.config.Config;

public class StopWordsFilter {
	private static HashMap<String, Integer> stopwordsMap=LoadStopWords();
	private static HashMap<String, Integer> LoadStopWords() {
		HashMap<String, Integer> rstHashMap=new HashMap<String, Integer>();
		IOapi myIO=new IOapi(1);
		myIO.startRead(Config.ChStopwordsSrcString, Config.encodingType, 0);
		String line=myIO.readOneSentence(0);
		int num=0;
		while(line!=null) {
			if(!rstHashMap.containsKey(line.replace("\n", ""))) {
				rstHashMap.put(line.replace("\n", ""), num);
				num++;
			}
			line=myIO.readOneSentence(0);
		}
		myIO.endRead(0);
		myIO.startRead(Config.EnStopwordsSrc, Config.encodingType, 0);
		line=myIO.readOneSentence(0);
		while(line!=null) {
			if(!rstHashMap.containsKey(line.replace("\n", ""))) {
				rstHashMap.put(line.replace("\n", ""), num);
				num++;
			}
			line=myIO.readOneSentence(0);
		}
		myIO.endRead(0);
		myIO.startRead(Config.UserStopwordSrc, Config.encodingType, 0);
		line=myIO.readOneSentence(0);
		while(line!=null) {
			if(!rstHashMap.containsKey(line.replace("\n", ""))) {
				rstHashMap.put(line.replace("\n", ""), num);
				num++;
			}
			line=myIO.readOneSentence(0);
		}
		myIO.endRead(0);
		myIO.startRead(Config.stopDict2Src, Config.encodingType, 0);
		line=myIO.readOneSentence(0);
		while(line!=null) {
			if(!rstHashMap.containsKey(line.replace("\n", ""))) {
				rstHashMap.put(line.replace("\n", ""), num);
				num++;
			}
			line=myIO.readOneSentence(0);
		}
		myIO.endRead(0);
		return rstHashMap;
	}
	/**
	 * @description split��ȥ�����з�����˷���ֵ�����л��з�
	 * @param in
	 * @param separator
	 * @return
	 */
	public static String filterSW(String in,String separator) {
		String []strs=in.split(separator);
		StringBuffer strBuffer=new StringBuffer("");
		for(int i=0;i<strs.length;i++) {
			if(stopwordsMap.containsKey(strs[i])) {
				continue;
			}
			else {
				strBuffer.append(strs[i]+separator);
			}
		}
		return strBuffer.toString().trim();
	}
	/**
	 * @description Ĭ�Ϸָ����ǿո�split��ȥ�����з�����˷���ֵ�����л��з�
	 * @15527429331
	 * @param in
	 * @return
	 */
	public static String filterSW(String in) {
		return filterSW(in, " ");
	}
	public static boolean IsStopword(String s) {
		return stopwordsMap.containsKey(s);
	}
	public static void main(String[] args) {
		String string="iphone se ���� �Ѿ� Ҫ 2 3 �� ���� �� Ҳ �� �� һ �� ���� �� ����\n";
		System.out.print(filterSW(string));
		System.out.print("hahaha");
	}
}
