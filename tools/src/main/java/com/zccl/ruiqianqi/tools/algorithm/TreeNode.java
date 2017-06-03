package com.zccl.ruiqianqi.tools.algorithm;

import com.zccl.ruiqianqi.tools.LogUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ruiqianqi on 2016/8/23 0023.
 * 我们常用的5种算法有顺序查找，二分法查找，二叉排序树查找，哈希表法查找，分块查找。Java的Hashtable即是用了哈希表法查找。
 */
public class TreeNode<T> {
    /** 父节点ID */
    private int parentId = -1;
    /** 本节点ID */
    private int selfId = -1;
    /** 上一层节点 */
    private TreeNode<T> parentNode;
    /** 本节点元素 */
    private T element;
    /** 下一层子节点集合 */
    private List<TreeNode<T>> childList;

    public TreeNode() {
        init(null);
    }

    public TreeNode(TreeNode<T> parentNode) {
        init(parentNode);
    }

    /**
     * 初始化动作
     */
    private void init(TreeNode<T> parentNode){
        if (childList == null) {
            childList = new ArrayList<>();
        }
        setParentNode(parentNode);
    }

    /**
     * 插入一个child节点到当前节点中
     * @param treeNode
     */
    public void addChildNode(TreeNode<T> treeNode) {
        if(treeNode!=null) {
            treeNode.setParentNode(this);
            childList.add(treeNode);
        }
    }

    /**
     * 返回当前节点的父辈节点集合【从根节点到这里】
     * @return
     */
    public List<TreeNode> getElders() {
        List<TreeNode> elderList = new ArrayList<>();
        TreeNode parentNode = getParentNode();
        if (parentNode == null) {
            return elderList;
        } else {
            elderList.add(parentNode);
            elderList.addAll(parentNode.getElders());
            return elderList;
        }
    }

    /**
     * 返回当前节点的晚辈集合【从这个节点到所有】
     */
    public List<TreeNode<T>> getJuniors() {
        List<TreeNode<T>> juniorList = new ArrayList<>();
        List<TreeNode<T>> childList = getChildList();
        if (childList == null) {
            return juniorList;
        } else {
            int childNumber = childList.size();
            for (int i = 0; i < childNumber; i++) {
                TreeNode junior = childList.get(i);
                juniorList.add(junior);
                juniorList.addAll(junior.getJuniors());
            }
            return juniorList;
        }
    }

    /**
     * 删除节点和它下面的晚辈
     */
    public void deleteNode() {
        TreeNode parentNode = getParentNode();
        int id = getSelfId();
        if (parentNode != null) {
            parentNode.deleteChildNode(id);
        }
    }

    /**
     * 删除当前节点的某个子节点
     */
    public void deleteChildNode(int childId) {
        List<TreeNode<T>> childList = getChildList();

        Iterator<TreeNode<T>> iterator = childList.iterator();
        while (iterator.hasNext()) {
            TreeNode<T> child = iterator.next();
            if (child.getSelfId() == childId) {
                iterator.remove();
                return;
            }
        }

        /*
        int childNumber = childList.size();
        for (int i = 0; i < childNumber; i++) {
            TreeNode child = childList.get(i);
            if (child.getSelfId() == childId) {
                childList.remove(i);
                return;
            }
        }
        */

    }

    /**
     * 根据selfId, 找到一颗树中某个节点
     */
    public TreeNode<T> findTreeNodeById(int id) {
        if (this.selfId == id)
            return this;
        if (childList.isEmpty() || childList == null) {
            return null;
        } else {
            int childNumber = childList.size();
            for (int i = 0; i < childNumber; i++) {
                TreeNode<T> child = childList.get(i);
                TreeNode<T> resultNode = child.findTreeNodeById(id);
                if (resultNode != null) {
                    return resultNode;
                }
            }
            return null;
        }
    }

    /**
     * 是不是叶子
     * @return true是叶子 false是节点
     */
    public boolean isLeaf() {
        if (childList == null) {
            return true;
        } else {
            if (childList.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 设置携带的元素
     * @param element
     */
    public void setElement(T element) {
        this.element = element;
        setSelfId(element.hashCode());
    }

    /**
     * 返回携带的元素
     * @return
     */
    public T getElement() {
        return element;
    }

    /**
     * 返回父节点
     * @return
     */
    public TreeNode<T> getParentNode() {
        return parentNode;
    }

    /**
     * 设置父节点
     * @param parentNode
     */
    private void setParentNode(TreeNode<T> parentNode) {
        if(parentNode != null) {
            this.parentNode = parentNode;
            this.parentId = parentNode.getSelfId();
        }
    }

    /**
     * 返回当前节点的孩子集合
     */
    public List<TreeNode<T>> getChildList() {
        return childList;
    }

    /**
     * 返回自己的ID
     * @return
     */
    public int getSelfId() {
        return selfId;
    }

    /**
     * 设置自己的ID
     * @param selfId
     */
    public void setSelfId(int selfId) {
        this.selfId = selfId;
    }

}
