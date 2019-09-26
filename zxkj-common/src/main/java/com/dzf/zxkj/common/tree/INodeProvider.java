package com.dzf.zxkj.common.tree;


import com.dzf.zxkj.common.model.SuperVO;

public interface INodeProvider {
    SuperVO getTreeNode(Object paramObject);

    Object getHandle(Object paramObject);

    Object getParentHandle(Object paramObject);

    SuperVO getOtherTreeNode();
}