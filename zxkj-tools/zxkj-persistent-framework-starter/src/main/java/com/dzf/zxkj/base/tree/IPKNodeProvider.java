package com.dzf.zxkj.base.tree;

import com.dzf.zxkj.base.model.SuperVO;

public interface IPKNodeProvider {
    SuperVO createTreeNode(Object paramObject);

    Object getNodeId(Object paramObject);

    Object getParentNodeId(Object paramObject);

    SuperVO getOtherTreeNode();
}