import com.dzf.zxkj.platform.model.tax.workbench.TaxRptCalCellBVO;
import com.dzf.zxkj.platform.model.tax.workbench.TaxRptCalCellVO;

public interface IZxkjTaxRptService {

    TaxRptCalCellVO getTaxRptCalCell(TaxRptCalCellBVO[] params);
}
