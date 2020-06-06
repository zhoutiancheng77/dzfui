package com.dzf.admin.model.app.transfer.filetrans;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RetAppFileHandinVO implements Serializable {

    private RetAppFiletransHVO trans;

    private List<RetAppFiletransBVO> files;

}
