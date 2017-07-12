package com.wangyl.laplacianEigenmaps;
import java.util.ArrayList;

import com.wangyl.config.Config;
import com.wangyl.lsa.ExtractFeatureBaseLsa;
import com.wangyl.lsa.LsaSvmClassifier;
import com.wangyl.tools.IOapi;
import com.wangyl.tools.QuickSort;
import Jama.EigenvalueDecomposition;
import Jama.Matrix;
/**
 * 
 * @author WangYunli
 * @target ������ά
 */
public class LaplacianEigenmaps {
	private static MatrixR[] GetDistanceList(double[][]feaVec) throws Exception{
		int feaNum=feaVec.length;
		MatrixR[] r=new MatrixR[feaNum];
		for(int i=0;i<feaNum;i++) {
			MatrixR disList=new MatrixR(feaNum,2);
			for(int j=0;j<feaNum;j++) {
				disList.mat[j]=new VectorR(2);
				disList.mat[j].vec[0]=Distance(feaVec[i], feaVec[j]);
				disList.mat[j].vec[1]=j;
			}
			QuickSort.sort(disList.mat);//�����룬��С��������
			r[i]=disList;
		}
		return r;
	}
	private static double Distance(double[] a ,double[] b) throws Exception {
		if(a.length!=b.length) {
			throw new VectorDimNotAlignInMatrixException(a.length, b.length);
		}
		//ʹ��ŷ�Ͼ��룺
		double dis=0;
		int n=a.length;
		for(int i=0;i<n;i++) {
			dis=dis+(a[i]-b[i])*(a[i]-b[i]);
		}
		dis=Math.sqrt(dis);
		return dis;
	}
	
	/*
	 * @description �����õ�main����
	 * @param args
	 
	public static void main(String[] args) {
		double [][]a={{3,2,4},{2,0,2},{4,2,3}};
		Matrix A=new Matrix(a);
		EigenvalueDecomposition eig=A.eig();
		Matrix eigD=eig.getD();
		Matrix eigV=eig.getV();
		eigD.print(eigD.getRowDimension(), eigD.getColumnDimension());
		eigV.print(eigV.getRowDimension(), eigV.getColumnDimension());
		EigValAndVec eigvalvec;
		try {
			eigvalvec = new EigValAndVec(eig);
			eigvalvec.SortEigVal();
			eigvalvec.print();
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		System.out.println(eigD.toString());
	}*/
	/**
	 * @description ʹ��ԭʼ���ݹ���һ��ͼ��ÿһ������������ʾ�����ռ��е�һ���㣬
	 * m�����������������m*m���ڽӾ���Ȩֵ����/���ƶȾ���
	 * @param k KNN�е�K
	 * @return
	 * @throws Exception 
	 */
	private static MatrixR ConstructW(int k,double [][]featureVectors) throws Exception {
		//Matrix matrix=new Matrix(MatrixW);
		MatrixR W=null;
		int feaNum=featureVectors.length;
		if(k>feaNum-1) {
			k=feaNum-1;
		}
		MatrixR[] disM=GetDistanceList(featureVectors);
		double maxdis=disM[0].mat[feaNum-1].vec[0];//ѡ�����о����е����ֵ
		for(int i=0;i<feaNum;i++) {
			if(maxdis<disM[i].mat[feaNum-1].vec[0]) {
				maxdis=disM[i].mat[feaNum-1].vec[0];
			}
		}
		double tao=0.25*maxdis;
		W=new MatrixR(feaNum,feaNum);
		int y=0;
		for(int i=0;i<feaNum;i++) {//��ÿһ��feature/��
			W.SetPointValue(i, i, 1);
			for(int j=1;j<=k;j++) {//������������k���㣬����Wijֵ��д��Ȩֵ����Ȩֵ�����ǶԳƵ�
				
				y=(new Double(disM[i].mat[j].vec[1])).intValue();
				W.SetPointValue(i, y, Math.pow(Math.E, -(disM[i].mat[j].vec[0])/tao));
				W.SetPointValue(y, i, W.GetPointValue(i, y));//�Գƻ�
			}
		}
		System.out.println(tao);
		return W;
	}
	public static void SaveFeaturesForSVM(String outSrc,double [][]fea,ArrayList<Integer> stanceId) {
		IOapi tmpIO=new IOapi(1);
		tmpIO.startWrite(outSrc, Config.encodingType, 0);
		for(int i=0;i<fea.length;i++) {
			tmpIO.writeOneString(FeatureToStringForSVM(fea[i], stanceId.get(i))+"\n", 0);
		}
		tmpIO.endWrite(0);
	}
	private static String FeatureToStringForSVM(double []fea,int stance) {
		StringBuffer strBuf=new StringBuffer("");
		strBuf.append(stance);
		for(int i=0;i<fea.length;i++) {
			//lra[i].ConfirmStanceAndTopic();
			strBuf.append(" "+(i+1)+":"+fea[i]);
		}
		return strBuf.toString();
	}
	/**
	 * @description W��Ӧ��ͼ��n����ͨ�ɷ֣���LE�㷨������Ĺ�������ֵ����n������0������ֵ
	 * @param k KNN�е�K
	 * @param m ȡm����С������ֵ
	 * @throws Exception
	 * @return ����double[m][featureNum]
	 */
	public static double[][] LapEig(int k,int m,double [][]featureVectors) throws Exception{
		MatrixR W_MR=ConstructW(k,featureVectors);
		MatrixR D_MR=MatrixR.Diag(W_MR.Sum(true));
		D_MR.PreprocessForLE();
		Matrix D=new Matrix(D_MR.Transfer2Array());
		Matrix W=new Matrix(W_MR.Transfer2Array());
		Matrix L=new Matrix(D_MR.Transfer2Array());
		L.minusEquals(W);//L=D;L=L-W;
		Matrix D_1=D.inverse();//D����
		Matrix D_1L=D_1.times(L);//D����*L
		EigenvalueDecomposition eig=D_1L.eig();
		EigValAndVec eigvalvec=new EigValAndVec(eig);
		eigvalvec.SortEigVal();
		//eigvalvec.print();
		MatrixR eigMR=eigvalvec.eigValVec;
		int i=0;
		while(Math.abs(eigMR.mat[i].vec[0])<0.00001) {//�ҵ���һ������0������ֵ
			i++;
		}
		return eigMR.Transfer2ArrayAnd_T(i,1,i+m,eigMR.mat[0].dim);
	}
}


class Graph {
	//double []
}

class EigValAndVec{
	MatrixR eigValVec;
	public EigValAndVec(EigenvalueDecomposition eig) throws Exception {
		double[][] eigD=eig.getD().getArray();//����ֵ�����ڶԽ���
		double[][] eigV=eig.getV().getArray();//����������ÿһ����һ��������������ӦeigD�ж�Ӧ�е�����ֵ��
		int m=eigD.length;
		eigValVec=new MatrixR(m,m+1);
		for(int i=0;i<m;i++) {
			eigValVec.SetPointValue(i, 0, eigD[i][i]);
			for(int j=1;j<=m;j++) {
				eigValVec.SetPointValue(i, j, eigV[j-1][i]);
			}
		}
	}
	/**
	 * @description ����ֵ��С��������
	 */
	public void SortEigVal() {
		QuickSort.sort(eigValVec.mat);
	}
	public void print() {
		eigValVec.print();
	}
	/**
	 * 
	 */
	public void TransferVecToMatrixByTopKVals() {
		
	}
}
/**
 * 
 * @author WangYunli
 * @description ʵ��һЩJama��û��ʵ�ֵľ������ܣ���LaplacianEigenmaps������jama����Matrix����ʹ��
 * @functions ʵ��matlab�е�sum������diag����
 */
class MatrixR {
	VectorR []mat;//m��nά������
	public MatrixR(VectorR[] vrs) throws Exception {
		int dim=vrs[0].dim;
		mat=new VectorR[vrs.length];
		for(int i=0;i<mat.length;i++) {
			if(dim!=vrs[i].dim) {
				throw new VectorDimNotAlignInMatrixException(dim, vrs[i].dim);
			}
			mat[i]=new VectorR(vrs[i]);
		}
	}
	/**
	 * @description ����һ��Ԫ��ȫΪ0�ľ���
	 * @param m
	 * @param n
	 */
	public MatrixR(int m,int n) {
		mat=new VectorR[m];
		for(int i=0;i<m;i++) {
			mat[i]=new VectorR(n);
		}
	}
	public MatrixR(int m,int n,double defaultVal) {
		mat=new VectorR[m];
		for(int i=0;i<m;i++) {
			mat[i]=new VectorR(n,defaultVal);
		}
	}
	public MatrixR(MatrixR r) {
		mat=new VectorR[r.mat.length];
		for(int i=0;i<mat.length;i++) {
			mat[i]=new VectorR(r.mat[i]);
		}
	}
	public MatrixR(double [][]a) throws Exception{
		if(a==null) {
			throw new NullPointerException();
		}
		else {
			mat=new VectorR[a.length];
			for(int i=0;i<a.length;i++) {
				mat[i]=new VectorR(a[i]);
			}
		}
	}
	/**
	 * @description ���к��л�ȡԪ�أ�ͬa[i][j]
	 * @param i
	 * @param j
	 * @return
	 * @throws Exception
	 */
	public double GetPointValue(int i,int j) throws Exception{
		if(mat==null) {
			throw new NullPointerException();
		}
		else if(i>=mat.length||j>=mat[0].dim) {
			throw new OutOfIndexInMatrixException(mat.length,mat[0].dim,i,j);
		}
		else {
			return mat[i].vec[j];
		}
	}
	public void SetPointValue(int i,int j,double val) throws Exception {
		if(mat==null) {
			throw new NullPointerException();
		}
		else if(i>=mat.length||j>=mat[0].dim) {
			throw new OutOfIndexInMatrixException(mat.length,mat[0].dim,i,j);
		}
		else {
			mat[i].vec[j]=val;
		}
	}
	public MatrixR MultiMats(MatrixR m) throws Exception{
		if(mat[0].dim!=m.mat.length) {
			throw new DimNotMatchInMultipleMatrixsException(mat[0].dim, m.mat.length);
		}
		else {
			MatrixR r=new MatrixR(mat.length, m.mat[0].dim);
			for(int i=0;i<r.mat.length;i++) {
				for(int j=0;j<r.mat[0].dim;j++) {
					double tmp=0;
					for(int k=0;k<mat[0].dim;k++) {
						tmp=tmp+this.GetPointValue(i, k)*m.GetPointValue(k, j);
					}
				}
			}
			return r;
		}
	}
	/**
	 * 
	 * @param sumRow if sumRow=true,���з�����ͣ�m*n�ľ���õ�nά����
	 * @return
	 */
	public VectorR Sum(boolean sumRow) {
		if(sumRow==true) {
			VectorR tmp=new VectorR(mat[0].dim);
			for(int i=0;i<tmp.dim;i++) {
				double tmpd=0;
				for(int j=0;j<mat.length;j++) {
					tmpd=tmpd+mat[j].vec[i];
				}
				tmp.vec[i]=tmpd;
			}
			return tmp;
		}
		else {
			VectorR tmp=new VectorR(mat.length);
			for(int i=0;i<tmp.dim;i++) {
				double tmpd=0;
				for(int j=0;j<mat[0].dim;j++) {
					tmpd=tmpd+mat[i].vec[j];
				}
				tmp.vec[i]=tmpd;
			}
			return tmp;
		}
	}
	/**
	 * @description ���ɷ���
	 * @param v
	 * @param k
	 * @return
	 */
	public static MatrixR Diag(VectorR v,int k) {
		int m=v.dim+Math.abs(k);
		MatrixR rst=new MatrixR(m, m);
		if(k>=0) {
			int x=0;
			int y=k;
			while(y<m) {
				rst.mat[x].vec[y]=v.vec[x];
				x++;
				y++;
			}
		}
		else {//k<0
			int x=-k;
			int y=0;
			while(x<m) {
				rst.mat[x].vec[y]=v.vec[y];
				x++;
				y++;
			}
		}
		return rst;
	}
	public static MatrixR Diag(VectorR v) {
		return Diag(v, 0);
	}
	public VectorR Diag(int k) throws Exception{
		
		if(mat.length!=mat[0].dim) {
			throw new MatrixMustBeSquareException(mat.length, mat[0].dim);
		}
		int m=mat.length-Math.abs(k);
		if(m<=0) {
			throw new OutOfIndexInMatrixException(-1, -1, -1, -1);
		}
		VectorR vec=new VectorR(m);
		if(k>=0) {
			int x=0;
			int y=k;
			while(x<mat.length&&y<mat.length) {
				vec.vec[x]=mat[x].vec[y];
				x++;
				y++;
			}
		}
		else {
			int x=-k;
			int y=0;
			while(x<mat.length&&y<mat.length) {
				vec.vec[y]=mat[x].vec[y];
				x++;
				y++;
			}
			
		}
		return vec;
	}
	public VectorR Diag() throws Exception {
		return this.Diag(0);
	}
	public double[][] Transfer2Array() {
		int m=mat.length;
		int n=mat[0].dim;
		double [][]rst=new double[m][n];
		for(int i=0;i<m;i++) {
			for(int j=0;j<n;j++) {
				rst[i][j]=mat[i].vec[j];
			}
		}
		return rst;
	}
	/**
	 * @description ��ȡ�����һ���֣���ö�ά���飬��Χ��[si][sj]->[ei-1][ej-1]����ת�ã�����ת�ú������
	 * @param si ��ʼi����
	 * @param sj ��ʼj����
	 * @param ei ��ֹi���꣬������ei
	 * @param ej ��ֹj���꣬������ej
	 * @return rst Ҫ���صĶ�ά����
	 * @throws Exception
	 */
	public double[][] Transfer2ArrayAnd_T(int si,int sj,int ei,int ej) throws Exception {
		if(si<0||sj<0||ei>mat.length||ej>mat[0].dim||si>=ei||sj>=ej) {
			throw new OutOfIndexInMatrixException(-1, -1, -1, -1);
		}
		double [][]rst=new double[ej-sj][ei-si];
		for(int i=si;i<ei;i++) {
			for(int j=sj;j<ej;j++) {
				rst[j-sj][i-si]=mat[i].vec[j];
			}
		}
		return rst;
	}
	/**
	 * @description Ϊ��LE�������⣬����������İѾ���΢�����������
	 */
 	public void PreprocessForLE() throws Exception{
		if(mat.length!=mat[0].dim) {
			throw new MatrixMustBeSquareException(mat.length, mat[0].dim);
		}
		for(int i=0;i<mat.length;i++) {
			if(Math.abs(mat[i].vec[i])<0.00001) {
				mat[i].vec[i]=0.01;
			}
		}
	}
	public void print() {
		int m=mat.length;
		int n=mat[0].dim;
		for(int i=0;i<m;i++) {
			for(int j=0;j<n;j++) {
				System.out.print(mat[i].vec[j]+" ");
			}
			System.out.println();
		}
	}
}
/**
 * 
 * @author WangYunli
 * @description ʵ���������
 */
class VectorR implements Comparable<VectorR>{
	double []vec;
	int dim;
	boolean readOnly;
	public VectorR(int dim) {
		this.dim=dim;
		vec=new double [this.dim];
		for(int i=0;i<this.dim;i++) {
			vec[i]=0;
		}
		readOnly=false;
	}
	public VectorR(int dim,double defaultNum) {
		this.dim=dim;
		vec=new double [this.dim];
		for(int i=0;i<this.dim;i++) {
			vec[i]=defaultNum;
		}
		readOnly=false;
	}
	public VectorR(double []v) {
		dim=v.length;
		vec=new double [this.dim];
		for(int i=0;i<this.dim;i++) {
			vec[i]=v[i];
		}
		readOnly=false;
	}
	/**
	 * @description ��ֵ���ݣ�������������ͬ���������
	 * @param r
	 */
	public VectorR(VectorR r) {
		this.dim=r.dim;
		this.vec=new double [this.dim];
		for(int i=0;i<this.dim;i++) {
			vec[i]=r.vec[i];
		}
		readOnly=false;
	}
	public void VecPlus(VectorR r) throws Exception {
		if(this.dim!=r.dim) {
			throw new VectorException(this.dim,r.dim);
		}
		else {
			for(int i=0;i<this.dim;i++) {
				this.vec[i]=this.vec[i]+r.vec[i];
			}
		}
	}
	public void VecMinus(VectorR r) throws Exception {
		if(this.dim!=r.dim) {
			throw new VectorException(this.dim,r.dim);
		}
		else {
			for(int i=0;i<this.dim;i++) {
				this.vec[i]=this.vec[i]-r.vec[i];
			}
		}
	}
	public double GetInnerProduct(VectorR r) throws Exception {
		double rst=0;
		if(this.dim!=r.dim) {
			throw new VectorException(this.dim, r.dim);
		}
		else {
			for(int i=0;i<this.dim;i++) {
				rst=rst+this.vec[i]*r.vec[i];
			}
			return rst;
		}
	}
	public static VectorR StatVecPlus(VectorR a,VectorR b) {
		VectorR tmp=new VectorR(a);
		try {
			tmp.VecPlus(b);
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		return tmp;
	}
	public static VectorR StatVecMinus(VectorR a,VectorR b) {
		VectorR tmp=new VectorR(a);
		try {
			tmp.VecMinus(b);
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
		}
		return tmp;
	}
	public static double StatGetInnerProduct(VectorR a,VectorR b) throws Exception {
		double rst=0;
		if(a.dim!=b.dim) {
			throw new VectorException(a.dim, b.dim);
		}
		else {
			for(int i=0;i<a.dim;i++) {
				rst=rst+a.vec[i]*b.vec[i];
			}
			return rst;
		}
	}
	/**
	 * @description ר��Ϊ������ֵ���������������ʵ��
	 */
	public int compareTo(VectorR p) {
		// TODO �Զ����ɵķ������
		
		int n=0;
		if(this.vec[0]>p.vec[0]){
			n=1;
		}
		if(this.vec[0]<p.vec[0]) {
			n=-1;
		}
		if(Math.abs(this.vec[0]-p.vec[0])<0.00001) {
			n=0;
		}
		return n;
	}
}
class VectorException extends Exception {  
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String message;
    public VectorException(int m,int n) {  
        message = "����ά����ƥ�䣺"+m+","+n;
    }  
    public String getMessage() {  
        return message;  
    }  
    public void setMessage(String message) {  
        this.message = message;  
    }  
}
class OutOfIndexInMatrixException extends Exception {  
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String message;
    public OutOfIndexInMatrixException(int m,int n,int x,int y) {  
        message = "������������Χ��"+m+","+n+":"+x+","+y;
    }  
    public String getMessage() {  
        return message;  
    }  
    public void setMessage(String message) {  
        this.message = message;  
    }  
}
class VectorDimNotAlignInMatrixException extends Exception {  
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String message;
    public VectorDimNotAlignInMatrixException(int m,int n) {  
        message = "Ҫ����ľ���������ά����һ�£�"+m+","+n;
    }  
    public String getMessage() {  
        return message;  
    }  
    public void setMessage(String message) {  
        this.message = message;  
    }  
}
class DimNotMatchInMultipleMatrixsException extends Exception {  
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String message;
    public DimNotMatchInMultipleMatrixsException(int m,int n) {  
        message = "�����޷���ˣ������ά����"+m+","+n;
    }  
    public String getMessage() {  
        return message;  
    }  
    public void setMessage(String message) {  
        this.message = message;  
    }  
}
class MatrixMustBeSquareException extends Exception {  
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String message;
    public MatrixMustBeSquareException(int m,int n) {  
        message = "��������ľ�������Ƿ��󣡾����С��"+m+","+n;
    }  
    public String getMessage() {  
        return message;  
    }  
    public void setMessage(String message) {  
        this.message = message;  
    }  
}