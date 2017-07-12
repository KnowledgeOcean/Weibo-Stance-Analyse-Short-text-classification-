package com.wangyl.tools;

import java.util.ArrayList;

import org.thunlp.thulac.Split;
/**
 * show ���Ա�ǩ˵����
 * n/���� np/���� ns/���� ni/������ nz/����ר��
 * m/���� q/���� mq/������ t/ʱ��� f/��λ�� s/������
 * v/���� a/���ݴ� d/���� h/ǰ�ӳɷ� k/��ӳɷ� i/ϰ�� 
 * j/��� r/���� c/���� p/��� u/���� y/��������
 * e/̾�� o/������ g/���� w/��� x/����
 * �����������API��һ��ʹ�ô����Ա�ע�ķ����ִʣ�һ��ʹ�ò������Ա�ע�ķ����ִʣ�
 * ���ַ������Ի��ã���һ�����õ�һ��api�ִʣ�һ�����õڶ���api�ִʣ�����ÿ���л�api������ģ�Ͳ��������³�ʼ��ģ��;
 * ������ޱ�Ҫ���鲻Ҫ�л�ʹ��api;
 * ����ʹ�ô����Ա�ע��api�����������api������ȡ�ִ����кʹ�������;
 * @author WangYunli
 * 
 */
public class WordSegment {
	private static char separator='/';//Ĭ�ϵĵ��������֮��ķָ���
	private static ArrayList<Term> terms=new ArrayList<Term>();//һ�����ʶ�Ӧһ��term��term�а������ʺʹ��Ա�ǩ
	private static Split sp=new Split();
	private static ArrayList<String> splitStreams=null;
	private static String nowString="";
	private static boolean methodWithTag=false;//�ǲ���ʹ�õĴ����Եķ����ִʵ�
	private static int idCounter=0;//��¼nowString��Ӧ��id��ͬʱҲ��һ�����˶��پ仰
	public static void setSeparator(char separator) {
		WordSegment.separator = separator;
		sp.setSeparator(WordSegment.separator);
	}
	public static char getSeparator() {
		return separator;
	}
	public static ArrayList<Term> getTerms() {
		return terms;
	}
	public static String getNowString() {
		return nowString;
	}
	/**
	 * ��ʹ�ô��Ա�ע��ֻ�ִʣ��ִ�Ч�����ܱȴ��Ա�ע�Բ�
	 */
	public static String splitWordwithOutTag(String str) {
	
		if((methodWithTag==true&&idCounter>0)||idCounter==0) {
			sp.setSeparator(' ');
			sp.setSegOnly(true);
			sp.setUseFilter(false);
		}
		idCounter++;
		methodWithTag=false;
		nowString=str;
		splitStreams=sp.split(str);
		return IOapi.StringArrayList2String(splitStreams, "").trim();
	}
	public static String splitWordwithTag(String str) {
		if((methodWithTag==false&&idCounter>0)||idCounter==0) {
			sp.setSeparator(separator);
			if(idCounter>0) {
				sp.setSegOnly(false);
				sp.setUseFilter(false);
			}
		}
		idCounter++;
		methodWithTag=true;
		terms.clear();
		nowString=str;
		splitStreams=sp.split(str);
		String []tmpStrArray=null;
		for(String ss:splitStreams) {
			tmpStrArray=ss.split(String.valueOf(separator));
			if(tmpStrArray.length!=2) {
				System.out.println("Error 10001:�ִʽ����ʽ����"+ss);
				continue;
			}
			else {
				terms.add(new Term(tmpStrArray[0],tmpStrArray[1]));
			}
		}
		return IOapi.StringArrayList2String(splitStreams, " ").trim();
	}
	public static String TermsNameToString(String separator) {
		StringBuffer strBuf=new StringBuffer("");
		if(idCounter<1||methodWithTag==false) {
			return "";
		}
		else {
			for(Term term:terms) {
				strBuf.append(term.name);
				strBuf.append(separator);
			}
			return strBuf.toString().trim();
		}
	}
	public static String TermsTagToString(String separator) {
		StringBuffer strBuf=new StringBuffer("");
		if(idCounter<1||methodWithTag==false) {
			return "";
		}
		else {
			for(Term term:terms) {
				strBuf.append(term.tag);
				strBuf.append(separator);
			}
			return strBuf.toString().trim();
		}
	}
}
class Term {
	public String name;
	public String tag;
	public Term() {
		this.name="";
		this.tag="";
	}
	public Term(String name,String tag) {
		this.name=name;
		this.tag=tag;
	}
}
