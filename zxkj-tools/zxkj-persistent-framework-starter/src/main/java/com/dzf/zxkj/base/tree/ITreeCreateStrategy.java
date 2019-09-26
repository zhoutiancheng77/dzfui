
package com.dzf.zxkj.base.tree;


import com.dzf.zxkj.base.model.SuperVO;

/**
 * @author dengjt
 * 此接口是树的构建方法提供接口，支持Pk树构建和编码树构建。构建对象不限，即任意Object
 */
public interface ITreeCreateStrategy extends ICodeNodeProvider, IPKNodeProvider {
    /**
     * 当指定的父节点丢失时，忽略该节点.这样,其所有孩子节点也将不出现在树上
     */
    public final static int WHEN_LOSE_PARENT_IGNORE = 0;
    /**
     * 当指定的父节点丢失时，插入到根节点上
     */
    public final static int WHEN_LOSE_PARENT_INSERT_TO_ROOT = 1;
    /**
     * 当指定的父节点丢失时，插入到“其他节点”上
     */
    public final static int WHEN_LOSE_PARENT_INSERT_TO_OTHERNODE = 2;
    /**
     * 当指定的父节点丢失时，插入到其存在的最近的祖先节点，该参数只对编码树起作用。对于其他树,将导致该节点挂到root节点上.
     */
    public final static int WHEN_LOSE_PARENT_INSERT_TO_ANCESTOR = 3;
    /**
     * 当指定的父节点丢失时，自动生成树节点来填充空缺,以便保持树的结构，该参数只对编码树起作用。对于其他树,将导致该节点挂到root节点上.
     */
    public final static int WHEN_LOSE_PARENT_AUTO_FILL = 4;

    /**
     * 返回一个根节点
     *
     * @return
     */
    public SuperVO getRootNode();

    /**
     * 建树的数据不完整时的处理策略
     *
     * @return
     */
    public int getInsertType();

    /**
     * @return 树节点过滤接口
     */
    public INodeFilter getNodeFileter();

    /**
     * @return 编码树还是ID树
     */
    public boolean isCodeTree();

}
