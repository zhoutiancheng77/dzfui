package com.dzf.zxkj.common.tree;


import com.dzf.zxkj.common.model.SuperVO;

public interface ICodeNodeProvider {
    SuperVO createTreeNode(Object paramObject);

    Object getCodeValue(Object paramObject);

    String getCodeRule();

    String getCircularRule();

    SuperVO createDefaultTreeNodeForLoseNode(Object paramObject);

    SuperVO getOtherTreeNode();
}