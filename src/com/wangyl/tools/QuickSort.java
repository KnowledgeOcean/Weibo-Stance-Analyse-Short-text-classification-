package com.wangyl.tools;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class QuickSort {
    @SuppressWarnings("unchecked")
    //���������ź���ԭ���޸ģ�ʹ����Զ������������������������������Ϊ�ڲ�ʹ�ã��ⲿ�������ӿ�Ϊsort()��sort����Ҫ��������ʵ��Comparable�ӿڣ������ṩ����ʱ���ͼ�⣬�����ġ�
    private static void quickSort(Object[] in,int begin, int end) {
        if( begin == end || begin == (end-1) ) return;
        Object p = in[begin];
        int a = begin +1;
        int b = a;
        for( ; b < end; b++) {
            //�ö��������������ʵ��Comparable�ӿڣ���������ʹ��compareTo�������бȽ�
            if( ((Comparable<Object>)in[b]).compareTo(p) < 0) {
                if(a == b){a++; continue;}
                Object temp = in[a];
                in[a] = in[b];
                in[b] = temp;
                a++;
            }
        }
        in[begin] = in[a-1];
        in[a-1] = p;
        if( a-1 > begin){
            quickSort(in,begin, a);
        } 
        if( end-1 > a ) {
            quickSort(in,a, end);
        } 
        return;
    }
    
    //ʹ�÷��ͣ�����������������򣬸ö��������������ʵ��Comparable�ӿ�
    public static <T extends Comparable<? super T>> void sort(T[] input){
        quickSort(input,0,input.length);
    }
    
    //��Ӷ�List�����������Ĺ��ܣ��ο���Java�е�Java.util.Collections���sort()����
    public static <T extends Comparable<? super T>> void sort(List<T> list){
        Object[] t = list.toArray();//���б�ת��Ϊ����
        quickSort(t,0,t.length); //�������������
        //����������ɺ���д�ص��б���
        ListIterator<T> i = list.listIterator();
        for (int j=0; j<t.length; j++) {
            i.next();
            i.set((T)t[j]);
        }
    }
    
    //����Java��ԭʼ�������ͣ�int��double��byte�ȣ��޷�ʹ�÷��ͣ�����ֻ��ʹ�ú������ػ���ʵ�ֶ���Щԭʼ�������飨int[]��double[]��byte[]�ȣ�����������Ϊ�˹���ͬһ��������������ԭʼ���͵�(AutoBoxing��UnBoxing)���ƽ����װΪ��Ӧ�������ͣ�����µĶ������飬������ٽ��װ��������ȱ������Ҫ�����ת�����衢����Ŀռ䱣���װ������顣��һ�ַ�ʽ�ǽ�������븴�Ƶ��������غ����У��ٷ�API�е�Java.util.Arrays������е�sort()��������ʹ�����ַ��������Դ�Arrays���Դ���뿴����
    public static void sort(int[] input){
        Integer[] t = new Integer[input.length];
        for(int i = 0; i < input.length; i++){
            t[i] = input[i];//��װ
        }
        quickSort(t,0,t.length);//����
        for(int i = 0; i < input.length; i++){
            input[i] = t[i];//���װ
        }
    }
    //double[]��������غ���
    public static void sort(double[] input){
        Double[] t = new Double[input.length];
        for(int i = 0; i < input.length; i++){
            t[i] = input[i];
        }
        quickSort(t,0,t.length);
        for(int i = 0; i < input.length; i++){
            input[i] = t[i];
        }
    }
    //byte[]��������غ���
    public static void sort(byte[] input){
        Byte[] t = new Byte[input.length];
        for(int i = 0; i < input.length; i++){
            t[i] = input[i];
        }
        quickSort(t,0,t.length);
        for(int i = 0; i < input.length; i++){
            input[i] = t[i];
        }
    }
    //short[]��������غ���
    public static void sort(short[] input){
        Short[] t = new Short[input.length];
        for(int i = 0; i < input.length; i++){
            t[i] = input[i];
        }
        quickSort(t,0,t.length);
        for(int i = 0; i < input.length; i++){
            input[i] = t[i];
        }
    }
    //char[]��������غ���
    public static void sort(char[] input){
        Character[] t = new Character[input.length];
        for(int i = 0; i < input.length; i++){
            t[i] = input[i];
        }
        quickSort(t,0,t.length);
        for(int i = 0; i < input.length; i++){
            input[i] = t[i];
        }
    }
    //float[]��������غ���
    public static void sort(float[] input){
        Float[] t = new Float[input.length];
        for(int i = 0; i < input.length; i++){
            t[i] = input[i];
        }
        quickSort(t,0,t.length);
        for(int i = 0; i < input.length; i++){
            input[i] = t[i];
        }
    }
    
    //�����õ�main����
     public static void main(String[] args) {
        //����һ���������ɵ�int[]���飬��������
        int LEN = 1;
        int[] input = {1,2,1,2,1};
        //input[0]=0;
        System.out.print("int[] after sorting: ");
        sort(input);
        for(int i : input) {
          System.out.print(i + " ");
        } 
        System.out.println();
    }
}
