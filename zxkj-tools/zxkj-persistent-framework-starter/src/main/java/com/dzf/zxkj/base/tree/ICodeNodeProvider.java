package com.dzf.zxkj.base.tree;

import com.dzf.zxkj.base.model.SuperVO;

public interface ICodeNodeProvider {
    SuperVO createTreeNode(Object paramObject);

    Object getCodeValue(Object paramObject);

    String getCodeRule();

    String getCircularRule();

    SuperVO createDefaultTreeNodeForLoseNode(Object paramObject);

    SuperVO getOtherTreeNode();
}