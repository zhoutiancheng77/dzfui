package com.dzf.zxkj.base.tree;

import com.dzf.zxkj.base.model.SuperVO;

public interface INodeProvider {
    SuperVO getTreeNode(Object paramObject);

    Object getHandle(Object paramObject);

    Object getParentHandle(Object paramObject);

    SuperVO getOtherTreeNode();
}