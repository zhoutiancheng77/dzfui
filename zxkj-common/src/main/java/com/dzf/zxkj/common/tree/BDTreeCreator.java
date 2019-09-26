package com.dzf.zxkj.common.tree;

import com.dzf.zxkj.common.model.SuperVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BDTreeCreator {

    public static SuperVO createTree(Object[] userObjs, ITreeCreateStrategy strategy) {
        SuperVO root = strategy.getRootNode();
        try {
            if (strategy.isCodeTree())
                TreeCreator.createCodeTree(root, userObjs, new CodeNodeProviderAdapter(strategy), strategy.getInsertType(), strategy.getNodeFileter());
            else
                TreeCreator.createPKTree(root, userObjs, new PKNodeProviderAdapter(strategy), strategy.getInsertType(), strategy.getNodeFileter());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return root;
    }

    private static class CodeNodeProviderAdapter implements ICodeNodeProvider {
        private ITreeCreateStrategy adaptee;

        public CodeNodeProviderAdapter(ITreeCreateStrategy adaptee) {
            super();
            if (!adaptee.isCodeTree())
                throw new IllegalArgumentException(
                        "adaptee must be a code tree provider");
            this.adaptee = adaptee;
        }

        public SuperVO createTreeNode(Object userObj) {
            return adaptee.createTreeNode(userObj);
        }

        public Object getCodeValue(Object userObj) {
            return adaptee.getCodeValue(userObj);
        }

        public String getCodeRule() {
            return adaptee.getCodeRule();
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

    }

    private static class PKNodeProviderAdapter implements IPKNodeProvider {
        private ITreeCreateStrategy adaptee;

        public PKNodeProviderAdapter(ITreeCreateStrategy adaptee) {
            super();
            if (adaptee.isCodeTree())
                throw new IllegalArgumentException(
                        "adaptee must be a pk tree provider");
            this.adaptee = adaptee;
        }

        public SuperVO createTreeNode(Object obj) {

            return adaptee.createTreeNode(obj);
        }

        public Object getNodeId(Object obj) {
            return adaptee.getNodeId(obj);
        }

        public Object getParentNodeId(Object obj) {
            return adaptee.getParentNodeId(obj);
        }

        public SuperVO getOtherTreeNode() {
            return null;
        }

    }
}
