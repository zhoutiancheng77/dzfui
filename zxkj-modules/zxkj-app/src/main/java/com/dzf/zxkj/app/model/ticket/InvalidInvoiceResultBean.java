package com.dzf.zxkj.app.model.ticket;

import java.util.List;

/**
 * 作废信息
 * @author zhangj
 *
 */
public class InvalidInvoiceResultBean extends InvoiceBaseVO {
	public ResultBean Result;

	public ResultBean getResult() {
		return Result;
	}

	public void setResult(ResultBean Result) {
		this.Result = Result;
	}

	public static class ResultBean extends InvoiceBaseVO {

		public String ReturnCode;
		public String ReturnMessages;
		public InvoiceBean Invoice;

		public static class InvoiceBean extends InvoiceBaseVO  {

			public InvalidInfoBean InvalidInfo;
			
			public SalesUnitInfoBean SalesUnitInfo;
			
			public InvalidInfoBean getInvalidInfo() {
				return InvalidInfo;
			}

			public void setInvalidInfo(InvalidInfoBean invalidInfo) {
				InvalidInfo = invalidInfo;
			}

			public SalesUnitInfoBean getSalesUnitInfo() {
				return SalesUnitInfo;
			}

			public void setSalesUnitInfo(SalesUnitInfoBean salesUnitInfo) {
				SalesUnitInfo = salesUnitInfo;
			}
			
			public static class InvalidInfoBean extends InvoiceBaseVO {
				
				public String InvalidCount;//

				public List<InvalidInfoList> InvalidInfoList;

				public String getInvalidCount() {
					return InvalidCount;
				}

				public void setInvalidCount(String invalidCount) {
					InvalidCount = invalidCount;
				}

				public List<InvalidInfoList> getInvalidInfoList() {
					return InvalidInfoList;
				}

				public void setInvalidInfoList(List<InvalidInfoList> invalidInfoList) {
					InvalidInfoList = invalidInfoList;
				}
				
				public static class InvalidInfoList extends InvoiceBaseVO {
					
					public String InvoiceType;// 开票类型",
					public String InvoiceNumber;// 发票号码",
					public String InvoiceCode;// 发票代码",
					public String InvoicetypeCode;// 发票类型代码"
					
					public String getInvoiceType() {
						return InvoiceType;
					}
					
					public void setInvoiceType(String invoiceType) {
						InvoiceType = invoiceType;
					}
					
					public String getInvoiceNumber() {
						return InvoiceNumber;
					}
					
					public void setInvoiceNumber(String invoiceNumber) {
						InvoiceNumber = invoiceNumber;
					}
					
					public String getInvoiceCode() {
						return InvoiceCode;
					}
					
					public void setInvoiceCode(String invoiceCode) {
						InvoiceCode = invoiceCode;
					}
					
					public String getInvoicetypeCode() {
						return InvoicetypeCode;
					}
					
					public void setInvoicetypeCode(String invoicetypeCode) {
						InvoicetypeCode = invoicetypeCode;
					}
					
				}
				
			}
			

			public static class SalesUnitInfoBean extends InvoiceBaseVO {
                /**
                 * SalesUnitName : 销方单位名称
                 * SalesUnitTaxId : 销方单位税号
                 * SalesUnitAddress : 销方单位地址
                 * SalesUnitPhone : 销方单位电话
                 * SalesUnitBankName : 销方单位开户行名称
                 * SalesUnitBankAcount : 销方单位开户行账户
                 */

                public String SalesUnitName;
                public String SalesUnitTaxId;
                public String SalesUnitAddress;
                public String SalesUnitPhone;
                public String SalesUnitBankName;
                public String SalesUnitBankAcount;

                public String getSalesUnitName() {
                    return SalesUnitName;
                }

                public void setSalesUnitName(String SalesUnitName) {
                    this.SalesUnitName = SalesUnitName;
                }

                public String getSalesUnitTaxId() {
                    return SalesUnitTaxId;
                }

                public void setSalesUnitTaxId(String SalesUnitTaxId) {
                    this.SalesUnitTaxId = SalesUnitTaxId;
                }

                public String getSalesUnitAddress() {
                    return SalesUnitAddress;
                }

                public void setSalesUnitAddress(String SalesUnitAddress) {
                    this.SalesUnitAddress = SalesUnitAddress;
                }

                public String getSalesUnitPhone() {
                    return SalesUnitPhone;
                }

                public void setSalesUnitPhone(String SalesUnitPhone) {
                    this.SalesUnitPhone = SalesUnitPhone;
                }

                public String getSalesUnitBankName() {
                    return SalesUnitBankName;
                }

                public void setSalesUnitBankName(String SalesUnitBankName) {
                    this.SalesUnitBankName = SalesUnitBankName;
                }

                public String getSalesUnitBankAcount() {
                    return SalesUnitBankAcount;
                }

                public void setSalesUnitBankAcount(String SalesUnitBankAcount) {
                    this.SalesUnitBankAcount = SalesUnitBankAcount;
                }
            }

		}

		public String getReturnCode() {
			return ReturnCode;
		}

		public void setReturnCode(String returnCode) {
			ReturnCode = returnCode;
		}

		public String getReturnMessages() {
			return ReturnMessages;
		}

		public void setReturnMessages(String returnMessages) {
			ReturnMessages = returnMessages;
		}

		public InvoiceBean getInvoice() {
			return Invoice;
		}

		public void setInvoice(InvoiceBean invoice) {
			Invoice = invoice;
		}

	}

}
