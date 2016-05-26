package com.xxx.algorithm;

import java.util.Stack;

//二叉树三种遍历递归及非递归实现（Java）
public class Traverse {

	/******************定义二叉树**************************/
	private final int MAX_SIZE = 10; 
	//链式存储
	public static class BinaryTreeNode
	{
		int mValue;
		BinaryTreeNode mLeft;
		BinaryTreeNode mRight;
		
		public BinaryTreeNode(int mValue) {
			this.mValue = mValue;
		}
	}
	
	//顺序存储
	class BinaryTreeNode2
	{
		int[] data = new int[MAX_SIZE];
		int length;
	}
	
	//用以实现后续遍历的辅助结构
	private class HelpNode
	{
		BinaryTreeNode treeNode;
		boolean isFirst;
	}

	/******************递归实现***************************/
	//先序遍历
	public int PreOrderTreeWalk(BinaryTreeNode pNode)
	{
		if(pNode == null)
			return 0;
		visitNode(pNode);
		PreOrderTreeWalk(pNode.mLeft);
		PreOrderTreeWalk(pNode.mRight);
		return 1;
	}

	//中序遍历
	public int InOrderTreeWalk(BinaryTreeNode pNode)
	{
		if(pNode == null)
			return 0;
		InOrderTreeWalk(pNode.mLeft);
		visitNode(pNode);
		InOrderTreeWalk(pNode.mRight);
		return 1;
	}

	//后序遍历
	public int PostOrderTreeWalk(BinaryTreeNode pNode)
	{
		if(pNode == null)
			return 0;
		PostOrderTreeWalk(pNode.mLeft);
		PostOrderTreeWalk(pNode.mRight);
		visitNode(pNode);
		return 1;
	}

	/*****************非递归实现***********************/
	//先序遍历
	public int PreOrderTraverse(BinaryTreeNode pNode)
	{
		Stack<BinaryTreeNode> stack  = new Stack<>();
		if(pNode == null)
			return 0;

		while(!stack.isEmpty()||pNode != null)
		{
			while(pNode != null)
			{
				//先访问
				visitNode(pNode);
				stack.push(pNode);
				//遍历左节点
				pNode = pNode.mLeft;
			}
			//返回顶层元素
			pNode = stack.peek();
			stack.pop();
			//遍历右节点
			pNode = pNode.mRight;	
		}
		return 1;
	}
	
	//先序遍历实现方法二
	public int PreOrderTraverse2(BinaryTreeNode pNode)
	{
		if(pNode == null)
			return 0;
		Stack<BinaryTreeNode> stack = new Stack<>();
		stack.push(pNode);
		
		while(!stack.isEmpty())
		{
			pNode = stack.pop();
			visitNode(pNode);
			
			if(pNode.mRight != null)
				stack.push(pNode.mRight);
			if(pNode.mLeft != null)
				stack.push(pNode.mLeft);
		}
		return 1;
	}

	//中序遍历
	public int InOrderTraverse(BinaryTreeNode pNode)
	{
		Stack<BinaryTreeNode> stack = new Stack<>();
		if(pNode == null)
			return 0;
		
		while(!stack.isEmpty()||pNode != null)
		{
			while(pNode!=null)
			{
				stack.push(pNode);
				pNode = pNode.mLeft;
			}
			pNode = stack.pop();
			visitNode(pNode);
			pNode = pNode.mRight;	
		}
		return 1;
	}

	//后序遍历，用一个标记标记右子树是否访问过 
	/*
	 *    第一种思路：对于任一结点P，将其入栈，然后沿其左子树一直往下搜索，直到搜索到没有左孩子的结点，
	 *    此时该结点出现在栈顶，但是此时不能将其出栈并访问，因此其右孩子还为被访问。所以接下来按照相同
	 *    的规则对其右子树进行相同的处理，当访问完其右孩子时，该结点又出现在栈顶，此时可以将其出栈并访
	 *    问。这样就保证了正确的访问顺序。可以看出，在这个过程中，每个结点都两次出现在栈顶，只有在第二
	 *    次出现在栈顶时，才能访问它。因此需要多设置一个变量标识该结点是否是第一次出现在栈顶。
	 * */
	public int PostOrderTraverse(BinaryTreeNode pNode)
	{
		if(pNode == null)
			return 0;
		Stack<HelpNode> stack = new Stack<>();
		HelpNode helpNode;
		while(!stack.isEmpty() || pNode != null)
		{
			//一直循环至最左节点
			while(pNode != null)
			{
				HelpNode temp = new HelpNode();
				temp.treeNode = pNode;
				temp.isFirst = true;
				stack.push(temp);
				pNode = pNode.mLeft;
			}
			
			if(!stack.isEmpty())
			{
				helpNode = stack.pop();
				
				if(helpNode.isFirst)//表示第一次,即每一个要被访问的根节点要被push两次
				{
					helpNode.isFirst = false;
					stack.push(helpNode);
					pNode = helpNode.treeNode.mRight;//右节点的是否有效则移至循环的开始出进行判断
				}
				else 
				{
					visitNode(helpNode.treeNode);
					pNode = null;
				}
			}
		}
		return 1;	
	}
	
	//后序遍历实现方法二:双栈法
	public int PostOrderTraverse2(BinaryTreeNode pNode)
	{
		if(pNode == null)
			return 0;
		Stack<BinaryTreeNode> stack1 = new Stack<>();
		Stack<BinaryTreeNode> stack2 = new Stack<>();//辅助栈
		//存入根节点，初始化
		stack1.push(pNode);
		//stack1弹出的元素，压入stack2，在将该元素的左右节点压入stack1
		while(!stack1.isEmpty())
		{
			pNode = stack1.pop();
			stack2.push(pNode);
			if(pNode.mLeft != null)
			{
				stack1.push(pNode.mLeft);
			}
			if(pNode.mRight != null)
			{
				stack1.push(pNode.mRight);
			}
		}
		
		//stack弹出的即是后序遍历的顺序
		while(!stack2.isEmpty())
		{
			visitNode(stack2.pop());
		}
		return 1;	
	}
	
	//后序遍历实现方法三
	/*
	 * 第二种思路：要保证根结点在左孩子和右孩子访问之后才能访问，因此对于任一结点P，先将其入栈。
	 * 如果P不存在左孩子和右孩子，则可以直接访问它；或者P存在左孩子或者右孩子，但是其左孩子和右
	 * 孩子都已被访问过了，则同样可以直接访问该结点。若非上述两种情况，则将P的右孩子和左孩子依次
	 * 入栈，这样就保证了每次取栈顶元素的时候，左孩子在右孩子前面被访问，左孩子和右孩子都在根结点
	 * 前面被访问。
	 * */
	public int PostOrderTraverse3(BinaryTreeNode pNode)
	{
		if(pNode == null)
			return 0;
		
		BinaryTreeNode preVisitedNode = null;
		Stack<BinaryTreeNode> stack = new Stack<>();
		stack.push(pNode);
		
		while(!stack.isEmpty())
		{
			pNode = stack.peek();
			if((pNode.mLeft == null && pNode.mRight == null)//左右子树均为空的情况，即叶子节点
				||(preVisitedNode != null && 
				(preVisitedNode == pNode.mLeft || preVisitedNode == pNode.mRight)))//左右子树已经被访问的情况,如果有右子树，则下一次栈顶一                                                                                                   //定为右子树；若无右子树，则栈顶为根节点；故保证了左子/                                                                                                   //树-右子树-根节点的访问顺序
			{
				visitNode(pNode);
				preVisitedNode = stack.pop();
			}
			else 
			{
				if(pNode.mRight != null)
					stack.push(pNode.mRight);//注意push的顺序，先访问右子树
				if(pNode.mLeft != null)
					stack.push(pNode.mLeft);
			}
		}
		
		return 1;
	}


	/*********辅助函数**********/
	//访问节点数据
	private void visitNode(BinaryTreeNode treeNode)
	{
		System.out.print(treeNode.mValue);
		System.out.print("、");
	}



	public static void main(String[] args)
	{
		/************************构造测试二叉树链表组***********************/
		int[] data = {1,2,3,4,5,6,7,8,9,10};
		BinaryTreeNode[] treeNodes = new BinaryTreeNode[data.length];
		
		for(int i = 0; i < data.length; i++)
		{
			treeNodes[i] = new BinaryTreeNode(data[i]);
		}
		
		for(int i = 0; i < (data.length/2); i ++)
		{
			treeNodes[i].mLeft = treeNodes[(i + 1)*2 - 1];
			
			if(((i + 1) *2) < data.length)
			{
				treeNodes[i].mRight = treeNodes[(i + 1)*2];
			}
		}
		Traverse traverse = new Traverse();
		BinaryTreeNode root = treeNodes[0];
		System.out.print("先序遍历递归：    ");
		traverse.PreOrderTreeWalk(root);
		System.out.println();
		
		System.out.print("先序遍历非递归一：");
		traverse.PreOrderTraverse(root);
		System.out.println();
		
		System.out.print("先序遍历非递归二：");
		traverse.PreOrderTraverse2(root);
		System.out.println();
		
		System.out.print("中序遍历递归：  ");
		traverse.InOrderTreeWalk(root);
		System.out.println();
		
		System.out.print("中序遍历递归：  ");
		traverse.InOrderTraverse(root);
		System.out.println();
		
		System.out.print("后序遍历递归：  ");
		traverse.PostOrderTreeWalk(root);
		System.out.println();
		
		System.out.print("后序遍历非递归一：");
		traverse.PostOrderTraverse(root);
		System.out.println();
		
		System.out.print("后序遍历非递归二：");
		traverse.PostOrderTraverse2(root);
		System.out.println();
		
		System.out.print("后序遍历非递归三：");
		traverse.PostOrderTraverse3(root);
		System.out.println();
		/******************验证输出***********************/
//		for(BinaryTreeNode treeNode : treeNodes)
//		{
//			System.out.print("根节点");
//			System.out.println(treeNode.mValue);
//			System.out.print("左节点");
//			if(treeNode.mLeft != null)
//			{
//				System.out.println(treeNode.mLeft.mValue);
//			}
//			else 
//			{
//				System.out.println("null");
//			}
//			
//			System.out.print("右节点");
//			if(treeNode.mRight != null)
//			{
//				System.out.println(treeNode.mRight.mValue);
//			}
//			else 
//			{
//				System.out.println("null");
//			}
//			System.out.println();
//		}
	}










}
