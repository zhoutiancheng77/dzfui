package com.dzf.zxkj.common.tree;


import com.dzf.zxkj.common.model.SuperVO;

/**
 * 通过编码规则获取
 */
public abstract class AccTreeCreateStrategy implements ITreeCreateStrategy {

    private String codeRule;

    private SuperVO rootvo;

    public AccTreeCreateStrategy(String codeRule) {
        super();
        this.codeRule = codeRule;
    }

    public abstract SuperVO getRootVO();

    public SuperVO getRootNode() {
        if (rootvo == null) {
            rootvo = getRootVO();
        }
        return rootvo;
    }

    public int getInsertType() {
        return WHEN_LOSE_PARENT_INSERT_TO_ROOT;
    }

    public INodeFilter getNodeFileter() {

        return null;
    }

    public boolean isCodeTree() {

        return true;
    }

    public SuperVO createTreeNode(Object userObj) {
        return (SuperVO) userObj;

    }

    //编码
    public abstract String getCodeValue(Object userObj);

    public String getCodeRule() {

        return codeRule;
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

    public Object getNodeId(Object obj) {

        return null;
    }

    public Object getParentNodeId(Object obj) {

        return null;
    }

    public void setCodeRule(String codeRule) {
        this.codeRule = codeRule;
    }

}
