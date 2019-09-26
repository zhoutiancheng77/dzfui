package com.dzf.zxkj.base.tree;

import com.dzf.zxkj.base.model.SuperVO;

import java.util.HashMap;
import java.util.StringTokenizer;

class CodeNodeProviderImpl implements INodeProvider {
    private ICodeNodeProvider codeNodeProvider = null;
    private SuperVO root = null;
    private int[] codeSection = null;
    private int[] circleCodeSection = null;
    private int insertType = 0;
    private HashMap handleToTreeNode = null;

    public CodeNodeProviderImpl(SuperVO root,
                                ICodeNodeProvider codeNodeProviderImpl, HashMap handleToTreeNode,
                                int insertType) {
        this.root = root;
        this.codeNodeProvider = codeNodeProviderImpl;
        this.insertType = insertType;
        this.handleToTreeNode = handleToTreeNode;
        this.codeSection = parseCodeRule(this.codeNodeProvider.getCodeRule());
        this.circleCodeSection = parseCodeRule(this.codeNodeProvider
                .getCircularRule());
    }

    public SuperVO getTreeNode(Object userObj) {
        return this.codeNodeProvider.createTreeNode(userObj);
    }

    public Object getHandle(Object userObj) {
        return this.codeNodeProvider.getCodeValue(userObj);
    }

    public SuperVO getOtherTreeNode() {
        return this.codeNodeProvider.getOtherTreeNode();
    }

    public final Object getParentHandle(Object userObj) {
        Object codeValue = this.codeNodeProvider.getCodeValue(userObj);
        String parentCode = getParentCode(codeValue.toString());
        if ((this.insertType == 3) && (parentCode != null)) {
            Object parentNode = this.handleToTreeNode.get(parentCode);
            while ((parentNode == null) && (parentCode != null)
                    && (parentCode.length() > 0)) {
                parentCode = getParentCode(parentCode);
                parentNode = this.handleToTreeNode.get(parentCode);
            }
        } else if ((this.insertType == 4) && (parentCode != null)
                && (parentCode.length() > 0)) {
            SuperVO parentNode = (SuperVO) this.handleToTreeNode
                    .get(parentCode);
            if (parentNode == null) {
                parentNode = this.codeNodeProvider
                        .createDefaultTreeNodeForLoseNode(parentCode);
                if (parentNode != null) {
                    this.handleToTreeNode.put(parentCode, parentNode);
                    dealLoseNode(parentCode, parentNode);
                }
            }
        }
        return parentCode;
    }

    private void dealLoseNode(String codeValue, SuperVO node) {
        String parentCode = getParentCode(codeValue);
        if ((parentCode != null) && (parentCode.length() > 0)) {
            SuperVO parentNode = (SuperVO) this.handleToTreeNode
                    .get(parentCode);
            if (parentNode == null) {
                parentNode = this.codeNodeProvider
                        .createDefaultTreeNodeForLoseNode(parentCode);
                if (parentNode != null) {
                    this.handleToTreeNode.put(parentCode, parentNode);
                    parentNode.addChildren(node);
                    dealLoseNode(parentCode, parentNode);
                }
            } else {
                parentNode.addChildren(node);
            }
        } else {
            this.root.addChildren(node);
        }
    }

    private String getParentCode(String childCode) {
        String parentCode = null;
        int sublength = 0;
        int length = childCode.length();
        int codeLen = this.codeSection == null ? 0 : this.codeSection.length;
        if (codeLen > 0) {
            int index = 0;
            while ((length > 0) && (index < codeLen)) {
                length -= this.codeSection[index];
                if (length > 0) {
                    sublength += this.codeSection[index];
                }
                index++;
            }
        }
        int circleCodeLen = this.circleCodeSection == null ? 0
                : this.circleCodeSection.length;
        if (circleCodeLen > 0) {
            int index = 0;
            while ((length > 0) && (index < circleCodeLen)) {
                length -= this.circleCodeSection[index];
                if (length > 0) {
                    sublength += this.circleCodeSection[index];
                }
                index = (index + 1) % circleCodeLen;
            }
        }

        if (sublength > 0)
            parentCode = childCode.substring(0, sublength);
        return parentCode;
    }

    private int[] parseCodeRule(String codeRule) {
        if (codeRule == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(codeRule, " ,./\\", false);
        int count = st.countTokens();
        int[] codes = new int[count];
        int index = 0;
        try {
            while (st.hasMoreTokens())
                codes[(index++)] = Integer.parseInt(st.nextToken().trim());
        } catch (Exception e) {
            codes = null;
        }
        return codes;
    }
}