package com.wangyl.tools;
/**
 * @author WangYunli
 *
 */ 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
/**
 * @author WangYunli
 * IOapi Usage:
 * @IOapi(int maxStreamNum)
 * maxStreamNumָ������Դ������ٸ�Streamͬʱ��д�ļ����ᴴ��maxStreamNum����Stream��maxStreamNum��дStream
 * @startRead(String filepath,String encodingString,int Readernum)
 * ��ʼ��ȡ��ָ���ļ�·���������ʽ��Stream���
 * @endRead(int Readernum)
 * �رն�ȡStream���ͷ���Դ��ReadernumָҪ�رյ�Stream���
 * @String readOneSentence(int Readernum)
 * ��ȡһ��
 * @int readint(int readernum)
 * ��ȡһ��������
 * @startWrite(String filepath,String encodingString,int Writernum)
 * ��ʼ��һ��дStream
 * void endWrite(int Writernum)
 * ����д�룬�ͷ�дStream
 * void writeOneString(String s,int Writernum)
 * дһ��String
 * void writeStringBufferIntoTXT(StringBuffer buf,int Writernum)
 * ��StringBuffer������д��txt
 * void writechars(char []a,int writernum)
 * ��char����д���ļ�
 * static boolean isFileExist(String path)
 * �ж��ļ��Ƿ���ڣ�����boolean����
 * public static String GetCurrentDir()
 * ��ȡ��ǰ·��
 * */
public class IOapi {
	private InputStreamReader read[];
	private BufferedReader bufread[];
	private OutputStreamWriter writer[];
	private BufferedWriter bufwriter[];
	private boolean isAppendMode[];
	public IOapi(int maxStreamNum)
	{
		read=new InputStreamReader[maxStreamNum];
		bufread=new BufferedReader[maxStreamNum];
		writer=new OutputStreamWriter[maxStreamNum];
		bufwriter=new BufferedWriter[maxStreamNum];
		isAppendMode=new boolean[maxStreamNum];
		int i;
		for(i=0;i<maxStreamNum;i++)
		{
			read[i]=null;
			bufread[i]=null;
			writer[i]=null;
			bufwriter[i]=null;
			isAppendMode[i]=false;
		}
	}
	public void startRead(String filepath,String encodingString,int Readernum)
	{
		try {
			read[Readernum] = new InputStreamReader(new FileInputStream(filepath), encodingString);
			bufread[Readernum] = new BufferedReader(read[Readernum]);
		} catch (UnsupportedEncodingException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		
	}
	public void startRead(File file,String encodingString,int Readernum)
	{
		try {
			read[Readernum] = new InputStreamReader(new FileInputStream(file), encodingString);
			bufread[Readernum] = new BufferedReader(read[Readernum]);
		} catch (UnsupportedEncodingException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		
	}
	public void endRead(int Readernum)
	{
		if(bufread!=null)
			try {
				bufread[Readernum].close();
				bufread[Readernum]=null;
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		if(read!=null)
			try {
				read[Readernum].close();
				read[Readernum]=null;
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
	}
	public static void makeSureDirExists(String dir) {
		File file = new File(dir);
		if(!file.exists()){  
		    file.mkdirs();  
		}
	}
	public void startWrite(String filepath,String encodingString,int Writernum)
	{
		try {  
			File file = new File(filepath);  
			File fileParent = file.getParentFile();  
			if(!fileParent.exists()){  
			    fileParent.mkdirs();  
			}  
			writer[Writernum]=new OutputStreamWriter(new FileOutputStream(filepath),encodingString);
			bufwriter[Writernum]=new BufferedWriter(writer[Writernum]);
		} catch (UnsupportedEncodingException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}
	public void startWrite(File file,String encodingString,int Writernum)
	{
		try {
			writer[Writernum]=new OutputStreamWriter(new FileOutputStream(file),encodingString);
			bufwriter[Writernum]=new BufferedWriter(writer[Writernum]);
		} catch (UnsupportedEncodingException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
	}
	public boolean IsAppendMode(int Writernum) {
		return isAppendMode[Writernum];
	}
	public void startWrite(String filepath,String encodingString,int Writernum,boolean isAppend)
	{
		if(isAppend) {
			isAppendMode[Writernum]=true;
			try {  
				File file = new File(filepath);  
				File fileParent = file.getParentFile();  
				if(!fileParent.exists()){  
				    fileParent.mkdirs();  
				}  
				writer[Writernum]=new OutputStreamWriter(new FileOutputStream(filepath,isAppend),encodingString);
				bufwriter[Writernum]=new BufferedWriter(writer[Writernum]);
			} catch (UnsupportedEncodingException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		}
		else {
			try {  
				File file = new File(filepath);  
				File fileParent = file.getParentFile();  
				if(!fileParent.exists()){  
				    fileParent.mkdirs();  
				}  
				writer[Writernum]=new OutputStreamWriter(new FileOutputStream(filepath),encodingString);
				bufwriter[Writernum]=new BufferedWriter(writer[Writernum]);
			} catch (UnsupportedEncodingException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
		}
	}
	public void endWrite(int Writernum)
	{
		try {
			if(bufwriter!=null)
			{
				if(bufwriter[Writernum]!=null) {
					bufwriter[Writernum].close();
					bufwriter[Writernum]=null;
				}
			}
			if(writer!=null)
			{
				if(writer[Writernum]!=null) {
					writer[Writernum].close();
					writer[Writernum]=null;
				}
			}
			
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		isAppendMode[Writernum]=false;
	}
	/**
	 * ��ȡ���ļ���᷵��null������""
	 */
	public String readOneSentence(int Readernum)
	{
		try {
			return bufread[Readernum].readLine();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			System.out.println("�ļ�δ��ȷ�򿪣���ȡ����");
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * ���isAppendMode[writernum]=false�����д��������׷��д
	 * @param s
	 * @param Writernum
	 */
	public void writeOneString(String s,int Writernum)
	{
		
		try {
			bufwriter[Writernum].write(s);
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
			System.out.println("д��string����");
		}
	}
	public void writeStringBufferIntoTXT(StringBuffer buf,int Writernum)//��StringBufferд��txt
	{
		
			try {
				bufwriter[Writernum].write(buf.toString());
			} catch (IOException e) {
				// TODO �Զ����ɵ� catch ��
				e.printStackTrace();
			}
	}
	public static boolean isFileExist(String path)
	{
		File file=new File(path);
		return file.exists();
	}
	public int readint(int readernum)
	{
		int rst=-1;
		try {
			rst=bufread[readernum].read();
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		return rst;
	}
	public void writechars(char []a,int writernum)
 	{
 		try {
			bufwriter[writernum].write(a);
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
			System.out.println("Wrong in IOforHMM.writechars");
		}
 	}
	public static String GetCurrentDir() {
		return System.getProperty("user.dir");
	}
	public static String StringArrayList2String(ArrayList<String> str_array_list){
		String sparator=" ";
		StringBuffer tmpStrBuf=new StringBuffer("");
		for(String word:str_array_list) {
			tmpStrBuf.append(word);
			tmpStrBuf.append(sparator);
		}
		return tmpStrBuf.toString().trim();
	}
	public static String StringArrayList2String(ArrayList<String> str_array_list,String sparator){
		StringBuffer tmpStrBuf=new StringBuffer("");
		for(String word:str_array_list) {
			tmpStrBuf.append(word);
			tmpStrBuf.append(sparator);
		}
		return tmpStrBuf.toString().trim();
	}
}
