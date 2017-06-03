package com.zccl.ruiqianqi.tools.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruiqianqi on 2016/8/23 0023.
 */
public class Tree {

    /** 超级根节点 */
    private TreeNode<Element> root;

    public Tree() {
        init();
    }

    private void init(){
        root = new TreeNode();
        root.setElement(new Element("root", "root"));
    }

    /**
     * 增加一个新的节点
     * @param node        父节点
     * @param nodeAdded  子节点
     */
    public void addNode(TreeNode<Element> node, TreeNode<Element> nodeAdded) {
        //增加节点
        if(node==null){
            root.addChildNode(nodeAdded);
        } else {
            node.addChildNode(nodeAdded);
        }
    }

    /**
     * 增加一个新的节点
     * @param node      父节点
     * @param element  子节点
     */
    public void addNode(TreeNode<Element> node, Element element) {
        TreeNode<Element> nodeAdded = new TreeNode();
        nodeAdded.setElement(element);
        //增加节点
        if(node==null){
            root.addChildNode(nodeAdded);
        } else {
            node.addChildNode(nodeAdded);
        }
    }

    /**
     * 通过元素的equals方法进行比较
     * @param input    从哪个节点开始搜索
     * @param newNode  要搜索的元素对象
     * @return
     */
    public TreeNode<Element> search(TreeNode<Element> input, Element newNode) {
        TreeNode<Element> temp = null;
        if (input.getElement().equals(newNode)) {
            return input;
        }
        for (int i = 0; i < input.getChildList().size(); i++){
            temp = search(input.getChildList().get(i), newNode);
            if (null != temp) {
                break;
            }
        }
        return temp;
    }

    /**
     * 通过元素的equals方法进行比较
     * @param input    从哪个节点开始搜索
     * @param newCmd   要搜索的元素对象
     * @return
     */
    public TreeNode<Element> search(TreeNode<Element> input, String newCmd) {
        TreeNode<Element> temp = null;
        if (input.getElement().hashCode()==newCmd.hashCode()) {
            return input;
        }
        for (int i = 0; i < input.getChildList().size(); i++){
            temp = search(input.getChildList().get(i), newCmd);
            if (null != temp) {
                break;
            }
        }
        return temp;
    }

    /**
     * 从节点的子节点开始搜索
     * @param input    从哪个节点开始搜索
     * @param newCmd   要搜索的元素对象
     * @return
     */
    public TreeNode<Element> searchChild(TreeNode<Element> input, String newCmd) {
        for (int i = 0; i < input.getChildList().size(); i++){
            TreeNode<Element> temp = input.getChildList().get(i);
            if (temp.getElement().hashCode()==newCmd.hashCode()) {
                return temp;
            }
        }
        return null;
    }

    /**
     * 遍历表，返回匹配的结果，明确匹配
     * @param cmd
     * @return
     */
    public List<TreeNode<Element>> searchExplicit(String cmd){
        List<TreeNode<Element>> result = new ArrayList<>();
        char[] cmdChars = cmd.toCharArray();
        TreeNode temp = root;
        for (int i = 0; i < cmdChars.length; i++) {
            temp = searchChild(temp, String.valueOf(cmdChars[i]));
            if(temp != null){
                result.add(temp);
            } else {
                break;
            }
        }
        return result;
    }

    /**
     * 遍历表，返回匹配的结果，模糊匹配
     *
     * @param cmd
     * @return
     */
    public List<TreeNode<Element>> searchVague(String cmd){
        List<TreeNode<Element>> result = new ArrayList<>();
        char[] cmdChars = cmd.toCharArray();

        TreeNode temp = search(root, String.valueOf(cmdChars[0]));
        if(temp==null){
            return null;
        }
        result.add(temp);

        for (int i = 1; i < cmdChars.length; i++) {
            temp = searchChild(temp, String.valueOf(cmdChars[i]));
            if(temp != null){
                result.add(temp);
            } else {
                break;
            }
        }
        return result;
    }

}
