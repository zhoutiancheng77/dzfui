package com.dzf.zxkj.common.tree;


import com.dzf.zxkj.common.model.SuperVO;

public interface IPKNodeProvider {
    SuperVO createTreeNode(Object paramObject);

    Object getNodeId(Object paramObject);

    Object getParentNodeId(Object paramObject);

    SuperVO getOtherTreeNode();
}