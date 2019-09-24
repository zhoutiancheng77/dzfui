package com.dzf.zxkj.platform.services.common;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.exception.DAOException;
import com.dzf.zxkj.common.exception.DZFWarpException;

public interface ISecurityService {

    void checkSecurityForSave(Class className, String primaryKey, String pk_corp, String logincorp, String cuserid)
            throws DZFWarpException;

    void checkSecurityForDelete(Class className, String primaryKey, String pk_corp, String logincorp, String cuserid)
            throws DZFWarpException;

    void checkSecurityForQuery(Class className, String pk_corp, String logincorp, String cuserid)
            throws DZFWarpException;

    void checkSecurityForOther(Class className, String primaryKey, String pk_corp, String logincorp, String cuserid)
            throws DZFWarpException;

    void checkSecurityForSave(Class className, String primaryKey, String pk_corp, String logincorp)
            throws DZFWarpException;

    void checkSecurityForDelete(Class className, String primaryKey, String pk_corp, String logincorp)
            throws DZFWarpException;

    void checkSecurityForQuery(Class className, String pk_corp, String logincorp) throws DZFWarpException;

    void checkSecurityForOther(Class className, String primaryKey, String pk_corp, String logincorp)
            throws DZFWarpException;

    void checkSecurityForSave(String pk_corp, String logincorp) throws DZFWarpException;

    void checkSecurityForDelete(String pk_corp, String logincorp) throws DZFWarpException;

    void checkSecurityForQuery(String pk_corp, String logincorp) throws DZFWarpException;

    void checkSecurityForOther(String pk_corp, String logincorp) throws DZFWarpException;

    void checkSecurityForSave(String pk_corp, String logincorp, String cuserid) throws DZFWarpException;

    void checkSecurityForDelete(String pk_corp, String logincorp, String cuserid) throws DZFWarpException;

    void checkSecurityForQuery(String pk_corp, String logincorp, String cuserid) throws DZFWarpException;

    void checkSecurityForOther(String pk_corp, String logincorp, String cuserid) throws DZFWarpException;

    boolean isExists(String pk_corp, SuperVO supervo) throws DAOException;
}
