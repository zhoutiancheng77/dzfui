package com.dzf.zxkj.base.tree;


import com.dzf.zxkj.base.model.SuperVO;

/**
 * 通过父子id获取
 */
public abstract class AccTreeCreateStrategyByID implements ITreeCreateStrategy {

    private SuperVO rootvo;

    public AccTreeCreateStrategyByID() {
        super();
    }

    public SuperVO getRootNode() {
        if (rootvo == null) {
            rootvo = getRootVO();
        }
        return rootvo;
    }

    public abstract SuperVO getRootVO();

    public int getInsertType() {
        return WHEN_LOSE_PARENT_INSERT_TO_ROOT;
    }

    public INodeFilter getNodeFileter() {

        return null;
    }

    public boolean isCodeTree() {

        return false;
    }

    public SuperVO createTreeNode(Object userObj) {
        return (SuperVO) userObj;

    }

    public String getCodeValue(Object userObj) {

        return null;
    }

    public String getCodeRule() {

        return null;
    }

    public String getCircularRule() {

        return null;
    }

    public SuperVO createDefaultTreeNodeForLoseNode(
            Object codeValue) {

        return null;
    }

    public SuperVO getOtherTreeNode() {

        return null;
    }

    public abstract Object getNodeId(Object obj);

    public abstract Object getParentNodeId(Object obj);

    public void setCodeRule(String codeRule) {

    }

}
