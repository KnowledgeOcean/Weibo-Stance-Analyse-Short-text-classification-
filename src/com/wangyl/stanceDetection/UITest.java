package com.wangyl.stanceDetection;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * GridLayout ���ֲ���ʾ��
 * 
 * @author cn.haibin
 * 
 */
public class UITest {
	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("���ֲ���");
		shell.setSize(600, 400);
		GridLayout layer = new GridLayout();
		layer.numColumns = 2;
		layer.makeColumnsEqualWidth = false;
		layer.marginWidth = 5;
		layer.marginHeight = 5;
		layer.verticalSpacing = 0;
		layer.horizontalSpacing = 1;
		shell.setLayout(layer);
		GridData treeGridData = new GridData(GridData.FILL_VERTICAL);
		treeGridData.widthHint = 200;
		Tree tree = new Tree(shell, SWT.SINGLE);
		tree.setLayoutData(treeGridData);
		TreeItem depart1 = new TreeItem(tree, SWT.NONE);
		depart1.setText("��������");
		{
			TreeItem info1 = new TreeItem(depart1, SWT.NONE);
			info1.setText("�������ʦ");
			TreeItem info2 = new TreeItem(depart1, SWT.NONE);
			info2.setText("���Թ���ʦ");
		}
		TreeItem depart2 = new TreeItem(tree, SWT.NONE);
		depart2.setText("�г���");
		{
			TreeItem info3 = new TreeItem(depart2, SWT.NONE);
			info3.setText("��Ʒ");
			TreeItem info4 = new TreeItem(depart2, SWT.NONE);
			info4.setText("��Ӫ");
		}
		GridData expandBarGridData = new GridData(GridData.FILL_BOTH);
		expandBarGridData.widthHint = 300;
		ExpandBar expandBar = new ExpandBar(shell, SWT.V_SCROLL);
		expandBar.setLayoutData(expandBarGridData);
		{
			Composite comp1 = new Composite(expandBar, SWT.NONE);
			comp1.setLayout(new GridLayout(2, false));
			Group group = new Group(comp1, SWT.NONE);
			group.setText("���Ӳ���");
			Label lb_departCode = new Label(group, SWT.NONE);
			lb_departCode.setBounds(10, 30, 100, 25);
			lb_departCode.setText("���Ŵ���");
			Text txt_departCode = new Text(group, SWT.BORDER);
			txt_departCode.setBounds(110, 30, 100, 25);
			Label lb_departName = new Label(group, SWT.NONE);
			lb_departName.setBounds(10, 60, 100, 25);
			lb_departName.setText("��������");
			Text txt_departName = new Text(group, SWT.BORDER);
			txt_departName.setBounds(110, 60, 100, 25);
			Button btn_ok = new Button(group, SWT.NONE);
			btn_ok.setBounds(10, 90, 100, 25);
			btn_ok.setText("OK");
			Button btn_cancel = new Button(group, SWT.NONE);
			btn_cancel.setBounds(110, 90, 100, 25);
			btn_cancel.setText("Cancel");
			new Label(comp1, SWT.NONE);
			Group group1 = new Group(comp1, SWT.NONE);
			group1.setText("������Ϣ");
			final TableViewer tableViewer = new TableViewer(group1, SWT.MULTI
					| SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL
					| SWT.H_SCROLL);
			Table table = tableViewer.getTable();
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			table.setBounds(10, 40, 400, 100);
			final TableColumn newColumnTableColumn = new TableColumn(table,
					SWT.NONE);
			newColumnTableColumn.setWidth(120);
			newColumnTableColumn.setText("���Ŵ���");
			final TableColumn newColumnTableColumn_1 = new TableColumn(table,
					SWT.NONE);
			newColumnTableColumn_1.setWidth(120);
			newColumnTableColumn_1.setText("��������");
			Label lb_departCode1 = new Label(group1, SWT.NONE);
			lb_departCode1.setBounds(10, 140, 100, 25);
			lb_departCode1.setText("���Ŵ���");
			Button btn_departCode = new Button(group1, SWT.BORDER);
			btn_departCode.setBounds(110, 140, 100, 25);
			btn_departCode.setText("��ѯ");
			ExpandItem item1 = new ExpandItem(expandBar, SWT.NONE);
			item1.setText("���Ź���");
			item1.setHeight(400);// ����Item�ĸ߶�
			item1.setControl(comp1);// setControl��������comp1������
		}
		{
			Composite comp2 = new Composite(expandBar, SWT.NONE);
			ExpandItem item1 = new ExpandItem(expandBar, SWT.NONE);
			item1.setText("��λ����");
			item1.setHeight(95);// ����Item�ĸ߶�
			item1.setControl(comp2);// setControl��������comp1������
		}
		{
			Composite comp3 = new Composite(expandBar, SWT.NONE);
			comp3.setLayout(new GridLayout());
			ExpandItem item1 = new ExpandItem(expandBar, SWT.NONE);
			item1.setText("��Ա����");
			item1.setHeight(50);// ����Item�ĸ߶�
			item1.setControl(comp3);// setControl��������comp1������
		}
		shell.open();
		while (!shell.isDisposed()) {
			if (display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
}