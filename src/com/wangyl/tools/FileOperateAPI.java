package com.wangyl.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.wangyl.config.Config;
import com.wangyl.log.Log;

public class FileOperateAPI {
	/**
     * �����ļ���Ŀ¼ <������ϸ����>
     * 
     * @param srcPath �ļ�����·��
     * @param destDirPath Ŀ���ļ��о���·��
     * @throws Exception 
     * @see [�ࡢ��#��������#��Ա]
     */
    public static void CopyFile(String srcPath, String destDirPath) throws Exception
    {
        File srcfile = new File(srcPath);
        File destDir = new File(destDirPath);
        InputStream is = null;
        OutputStream os = null;
        int ret = 0;
        if(!destDir.exists()){  
		    destDir.mkdirs();
		}
        // Դ�ļ�����
        if (srcfile.exists() && destDir.exists() && destDir.isDirectory())
        {
            try
            {
                is = new FileInputStream(srcfile);
                String destFile = destDirPath + File.separator + srcfile.getName();
                os = new FileOutputStream(new File(destFile));
                byte[] buffer = new byte[1024];
                while ((ret = is.read(buffer)) != -1) {
                	os.write(buffer, 0, ret); // �˴�������os.write(buffer),����ȡ�����ֽ�С��1024ʱ�����д;
                	// ret�Ƕ�ȡ���ֽ���
                }
                os.flush();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new Exception("");
            }
            catch (IOException e) {
                e.printStackTrace();
                throw new Exception("");
            }
            finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                }
                catch (Exception e) {
                }
            }
        }
        else {
            throw new Exception("Դ�ļ������ڻ�Ŀ��·�������ڣ�Դ�ļ���"+srcPath+"��Ŀ��·����"+destDirPath);
        }
    }
    /**
     * �г��ļ����µ������ļ���ʹ�õݹ顣 <������ϸ����>
     * 
     * @param dirPath �ļ��о���·��
     * @see [�ࡢ��#��������#��Ա]
     */
    public static void GetFileList(String dirPath)
    {
        File rootDir = new File(dirPath);
        if (rootDir.exists())
        {
            File[] files = rootDir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    System.out.println("Ŀ¼" + file.getName());
                    // �ݹ����
                    GetFileList(file.getPath());
                }
                else {
                    System.out.println("�ļ�" + file.getName());
                }
            }
        }
    }
    /**
     * <һ�仰���ܼ���>�����ļ��� <������ϸ����>
     * 
     * @param srcDir �ļ��еľ���·��
     * @param destDir Ŀ�����·��
     * @throws Exception
     * @see [�ࡢ��#��������#��Ա]
     */
    public static void CopyFolder(String srcDir, String destDir) throws Exception
    {
        File srcFile = new File(srcDir);
        // ��Ŀ��·�������ļ���
        String name = srcFile.getName();
        File destFile = new File(destDir + File.separator + name);
        if (!destFile.exists()) {
            destFile.mkdir();
        }
        if (srcFile.exists() && destFile.isDirectory()) {
            File[] files = srcFile.listFiles();
            String src = srcDir;
            String dest = destFile.getAbsolutePath();
            for (File temp : files) {
                // ����Ŀ¼
                if (temp.isDirectory()) {
                    String tempSrc = src + File.separator + temp.getName();
                    String tempDest = dest + File.separator + temp.getName();
                    File tempFile = new File(tempDest);
                    tempFile.mkdir();
                    // ����Ŀ¼��Ϊ��ʱ
                    if (temp.listFiles().length != 0) {
                        // �ݹ����
                        CopyFolder(tempSrc, tempDest);
                    }
                }
                else {
                // �����ļ�
                	String tempPath = src + File.separator + temp.getName();
                    CopyFile(tempPath, dest);
                }
            }
        }
    }
    /**
     * ɾ���ļ��л��ļ� <������ϸ����>
     * Ҫ��ɾ�������ݣ���ɾ��������
     * @param dirPath Ҫɾ�����ļ���
     * @see [�ࡢ��#��������#��Ա]
     */
    public static void DeleteFolder(String dirPath)
    {
        File folder = new File(dirPath);
        File[] files = folder.listFiles();
        if(files!=null) {
        	for (File file : files)
            {
                if (file.isDirectory()) {
                    String tempFilePath = dirPath + File.separator + file.getName();
                    DeleteFolder(tempFilePath);
                }
                else {
                    file.delete();
                }
            }
        }
        folder.delete();
    }
    /**
     * �½�Ŀ¼
     * @param folderPath String �� c:/fqf
     * @return boolean
     */
    public void newFolder(String folderPath) {
      try {
    	File myFilePath = new java.io.File(folderPath);
        if (!myFilePath.exists()) {
          myFilePath.mkdir();
        }
      }
      catch (Exception e) {
        Log.LogInf(Timer.GetNowTimeToMillisecends()+"�½�Ŀ¼��������");
        e.printStackTrace();
      }
    }
    /** ����Ŀ¼
     * 
     * @param dir
     * @param fileList
     * @throws IOException
     */
 	public static void visitDirsAllFiles(File dir, List<File> fileList) throws IOException {
 		if (dir.isDirectory()) {
 			// ���˵�Linux ���ļ�
 			FilenameFilter filter = new FilenameFilter() {
 				public boolean accept(File dir, String name) {
 					return !name.startsWith(".");
 				}
 			};
 			String[] children = dir.list(filter);

 			for (int i = 0; i < children.length; i++) {
 				visitDirsAllFiles(new File(dir, children[i]), fileList);
 			}
 		} else {
 			fileList.add(dir);
 		}
 	}
 	public static int HowManyFileInDir(String dir) throws IOException {
 		List<File> filelist=new ArrayList<File>();
 		visitDirsAllFiles(dir, filelist);
 		return filelist.size();
 	}
 	public static void visitDirsAllFiles(String dir, List<File> fileList) throws IOException {
 		visitDirsAllFiles(new File(dir), fileList);
 	}
    public static void main(String[] args) {
    	try {
			Log.LogInf(HowManyFileInDir(Config.lsaCorpusDir)+"\n");
		} catch (IOException e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
    }
}
