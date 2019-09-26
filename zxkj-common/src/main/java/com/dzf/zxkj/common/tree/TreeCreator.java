package com.dzf.zxkj.common.tree;


import com.dzf.zxkj.common.model.SuperVO;

import java.util.HashMap;

public class TreeCreator {
    private Object[] userObjs = null;
    private INodeProvider nodeProvider = null;
    private INodeFilter nodeFilter = null;
    private int insertType = 0;
    private HashMap hm_handleToTreeNode = null;
    public static final int WHEN_LOSE_PARENT_IGNORE = 0;
    public static final int WHEN_LOSE_PARENT_INSERT_TO_ROOT = 1;
    public static final int WHEN_LOSE_PARENT_INSERT_TO_OTHERNODE = 2;
    public static final int WHEN_LOSE_PARENT_INSERT_TO_ANCESTOR = 3;
    public static final int WHEN_LOSE_PARENT_AUTO_FILL = 4;

    private TreeCreator(Object[] userObjs, INodeProvider nodeProvider,
                        int insertType, INodeFilter nodeFilter) {
        this.userObjs = userObjs;
        this.nodeProvider = nodeProvider;
        this.insertType = insertType;
        this.nodeFilter = nodeFilter;
    }

    private TreeCreator(Object[] userObjs, INodeProvider nodeProvider,
                        HashMap hm_handleToTreeNode, int insertType, INodeFilter nodeFilter) {
        this.userObjs = userObjs;
        this.nodeProvider = nodeProvider;
        this.insertType = insertType;
        this.hm_handleToTreeNode = hm_handleToTreeNode;
        this.nodeFilter = nodeFilter;
    }

    public static final SuperVO getCodeTreeModel(
            SuperVO root, Object[] userObjs,
            ICodeNodeProvider codeNodeProvider, int insertType,
            INodeFilter nodeFilter) {
        createCodeTree(root, userObjs, codeNodeProvider, insertType, nodeFilter);
        return root;
    }

    public static final void createCodeTree(SuperVO root,
                                            Object[] userObjs, ICodeNodeProvider codeNodeProvider,
                                            int insertType, INodeFilter nodeFilter) {
        int count = userObjs == null ? 0 : userObjs.length;
        HashMap handleToTreeNode = new HashMap();
        for (int i = 0; i < count; i++) {
            Object key = codeNodeProvider.getCodeValue(userObjs[i]);
            Object value = codeNodeProvider.createTreeNode(userObjs[i]);
            insertRecordToHashMap(handleToTreeNode, key, value);
        }
        INodeProvider nodeProvider = new CodeNodeProviderImpl(root,
                codeNodeProvider, handleToTreeNode, insertType);

        TreeCreator treeCreator = new TreeCreator(userObjs, nodeProvider,
                handleToTreeNode, insertType, nodeFilter);

        treeCreator.buildTree(root);
    }

    public static final SuperVO getPKTreeModel(
            SuperVO root, Object[] userObjs,
            IPKNodeProvider pkNodeProvider, int insertType,
            INodeFilter nodeFilter) {
        createPKTree(root, userObjs, pkNodeProvider, insertType, nodeFilter);
        return root;
    }

    public static final void createPKTree(SuperVO root,
                                          Object[] userObjs, IPKNodeProvider pkNodeProvider, int insertType,
                                          INodeFilter nodeFilter) {
        INodeProvider nodeProvider = new PKNodeProviderImpl(pkNodeProvider);
        createTree(root, userObjs, nodeProvider, insertType, nodeFilter);
    }

    public static final void createTree(SuperVO root,
                                        Object[] userObjs, INodeProvider nodeProvider,
                                        INodeFilter nodeFilter) {
        createTree(root, userObjs, nodeProvider, 0, nodeFilter);
    }

    public static final void createTree(SuperVO root,
                                        Object[] userObjs, INodeProvider nodeProvider, int insertType,
                                        INodeFilter nodeFilter) {
        TreeCreator treeCreator = new TreeCreator(userObjs, nodeProvider,
                insertType, nodeFilter);

        treeCreator.buildTree(root);
    }

    private HashMap getHmHandleToTreeNode() {
        if (this.hm_handleToTreeNode == null) {
            this.hm_handleToTreeNode = new HashMap();
            int count = this.userObjs == null ? 0 : this.userObjs.length;
            for (int i = 0; i < count; i++) {
                Object key = this.nodeProvider.getHandle(this.userObjs[i]);
                Object value = this.nodeProvider.getTreeNode(this.userObjs[i]);
                insertRecordToHashMap(this.hm_handleToTreeNode, key, value);
            }
        }
        return this.hm_handleToTreeNode;
    }

    private static void insertRecordToHashMap(HashMap hm, Object key,
                                              Object value) {
        if (!hm.containsKey(key)) {
            hm.put(key, value);
        }
    }

    private void buildTree(SuperVO root) {
        try {
            int count = this.userObjs == null ? 0 : this.userObjs.length;
            SuperVO otherNode = null;
            for (int i = 0; i < count; i++) {
                if ((this.nodeFilter == null)
                        || (!this.nodeFilter.dontInsert(this.userObjs[i]))) {
                    Object handle = this.nodeProvider.getHandle(this.userObjs[i]);
                    if (handle == null) {
                        throw new Exception("User Object "
                                + this.userObjs[i]
                                + ", whose handle can not be null.");
                    }
                    SuperVO treeNode = (SuperVO) getHmHandleToTreeNode()
                            .get(handle);

                    SuperVO parentNode = null;
                    Object parentHandle = this.nodeProvider
                            .getParentHandle(this.userObjs[i]);
                    if (parentHandle == null)
                        parentNode = root;
                    else {
                        parentNode = (SuperVO) getHmHandleToTreeNode()
                                .get(parentHandle);
                    }

                    if (parentNode == null) {
                        if (this.insertType == WHEN_LOSE_PARENT_INSERT_TO_ROOT) {
                            parentNode = root;
                        } else if (this.insertType == WHEN_LOSE_PARENT_INSERT_TO_OTHERNODE) {
                            if (otherNode == null) {
                                otherNode = this.nodeProvider.getOtherTreeNode();
                                if (otherNode == null)
                                    otherNode = root.getClass().newInstance();
                            }
                            parentNode = otherNode;
                        } else {
                            if (this.insertType != WHEN_LOSE_PARENT_INSERT_TO_ANCESTOR) {
                                continue;
                            }
                            parentNode = root;
                        }

                    }
                    parentNode.addChildren(treeNode);

                }
            }
            //zpm注销
            //if (otherNode != null){
            //	root.addChildren(otherNode);
            //}
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}