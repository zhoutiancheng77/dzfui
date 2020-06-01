package com.dzf.zxkj.app.model.ticket;

import java.util.List;

/**
 * 转换vo
 * @author gaoxuan
 * @date 2017/5/6
 */
public class InvoiceResultBean extends InvoiceBaseVO {

    /**
     * Result : {"ReturnCode":"200","ReturnMessages":"返回信息","Invoice":{"InvoiceInfo":{"InvoiceType":"开票类型","RedInvoiceCode":"原发票代码","RedInvoiceNumber":"原发票号码","InvoiceNumber":"发票号码","InvoiceCode":"发票代码","MachineNumber":"机器编码","CheckCode":"校验码","TaxCode":"密码区","InvoiceDate":"开票日期","IssuerName":"开票人","PayeeName":"收款人","AuditorName":"复核人","InvoicetypeCode":"发票类型代码","Remark":"备注","DownloadLink":"版式文件下载地址"},"SalesUnitInfo":{"SalesUnitName":"销方单位名称","SalesUnitTaxId":"销方单位税号","SalesUnitAddress":"销方单位地址","SalesUnitPhone":"销方单位电话","SalesUnitBankName":"销方单位开户行名称","SalesUnitBankAcount":"销方单位开户行账户"},"PurchaserUnitInfo":{"PurchaserUnitName":"购买方名称","PurchaserUnitTaxID":"购买方税号","PurchaserUnitAddress":"购买方地址","PurchaserUnitPhone":"购买方电话","PurchaserUnitBankName":"购买方开户行名称","PurchaserUnitBankAccount":"购买方开户行账号"},"CommodityInfo":{"TotalAmount":"合计金额","TotalTaxAmount":"合计税额","TotalPriceTax":"价税合计","CommodityList":[{"CommodityName":"第一条商品名称","CommodityCode":"商品编码","CommodityQuantity":"商品数量","DiscountLineCorrespondsLineNumber":"折行对应行号","InvoiceLineProperties":"发票行性质","LineNumber":"行号","MeasurementUnit":"计量单位","SpecificationModel":"规格型号","PreferentialPolicyID":"优惠政策标识","TaxExemptType":"免税类型","TaxRate":"税率","Tax":"税额","UnitPrice":"单价","Amount":"金额","VATSpecialManagement":"增值税特殊管理"},{"CommodityName":"第二条商品名称","CommodityCode":"商品编码","CommodityQuantity":"商品数量","DiscountLineCorrespondsLineNumber":"折行对应行号","InvoiceLineProperties":"发票行性质","LineNumber":"行号","MeasurementUnit":"计量单位","SpecificationModel":"规格型号","PreferentialPolicyID":"优惠政策标识","TaxExemptType":"免税类型","TaxRate":"税率","Tax":"税额","UnitPrice":"单价","Amount":"金额","VATSpecialManagement":"增值税特殊管理"}]}}}
     */

    public ResultBean Result;

    public ResultBean getResult() {
        return Result;
    }

    public void setResult(ResultBean Result) {
        this.Result = Result;
    }

    public static class ResultBean {
        /**
         * ReturnCode : 200
         * ReturnMessages : 返回信息
         * Invoice : {"InvoiceInfo":{"InvoiceType":"开票类型","RedInvoiceCode":"原发票代码","RedInvoiceNumber":"原发票号码","InvoiceNumber":"发票号码","InvoiceCode":"发票代码","MachineNumber":"机器编码","CheckCode":"校验码","TaxCode":"密码区","InvoiceDate":"开票日期","IssuerName":"开票人","PayeeName":"收款人","AuditorName":"复核人","InvoicetypeCode":"发票类型代码","Remark":"备注","DownloadLink":"版式文件下载地址"},"SalesUnitInfo":{"SalesUnitName":"销方单位名称","SalesUnitTaxId":"销方单位税号","SalesUnitAddress":"销方单位地址","SalesUnitPhone":"销方单位电话","SalesUnitBankName":"销方单位开户行名称","SalesUnitBankAcount":"销方单位开户行账户"},"PurchaserUnitInfo":{"PurchaserUnitName":"购买方名称","PurchaserUnitTaxID":"购买方税号","PurchaserUnitAddress":"购买方地址","PurchaserUnitPhone":"购买方电话","PurchaserUnitBankName":"购买方开户行名称","PurchaserUnitBankAccount":"购买方开户行账号"},"CommodityInfo":{"TotalAmount":"合计金额","TotalTaxAmount":"合计税额","TotalPriceTax":"价税合计","CommodityList":[{"CommodityName":"第一条商品名称","CommodityCode":"商品编码","CommodityQuantity":"商品数量","DiscountLineCorrespondsLineNumber":"折行对应行号","InvoiceLineProperties":"发票行性质","LineNumber":"行号","MeasurementUnit":"计量单位","SpecificationModel":"规格型号","PreferentialPolicyID":"优惠政策标识","TaxExemptType":"免税类型","TaxRate":"税率","Tax":"税额","UnitPrice":"单价","Amount":"金额","VATSpecialManagement":"增值税特殊管理"},{"CommodityName":"第二条商品名称","CommodityCode":"商品编码","CommodityQuantity":"商品数量","DiscountLineCorrespondsLineNumber":"折行对应行号","InvoiceLineProperties":"发票行性质","LineNumber":"行号","MeasurementUnit":"计量单位","SpecificationModel":"规格型号","PreferentialPolicyID":"优惠政策标识","TaxExemptType":"免税类型","TaxRate":"税率","Tax":"税额","UnitPrice":"单价","Amount":"金额","VATSpecialManagement":"增值税特殊管理"}]}}
         */

        public String ReturnCode;
        public String ReturnMessages;
        public InvoiceBean Invoice;

        public String getReturnCode() {
            return ReturnCode;
        }

        public void setReturnCode(String ReturnCode) {
            this.ReturnCode = ReturnCode;
        }

        public String getReturnMessages() {
            return ReturnMessages;
        }

        public void setReturnMessages(String ReturnMessages) {
            this.ReturnMessages = ReturnMessages;
        }

        public InvoiceBean getInvoice() {
            return Invoice;
        }

        public void setInvoice(InvoiceBean Invoice) {
            this.Invoice = Invoice;
        }

        public static class InvoiceBean extends InvoiceBaseVO {
            /**
             * InvoiceInfo : {"InvoiceType":"开票类型","RedInvoiceCode":"原发票代码","RedInvoiceNumber":"原发票号码","InvoiceNumber":"发票号码","InvoiceCode":"发票代码","MachineNumber":"机器编码","CheckCode":"校验码","TaxCode":"密码区","InvoiceDate":"开票日期","IssuerName":"开票人","PayeeName":"收款人","AuditorName":"复核人","InvoicetypeCode":"发票类型代码","Remark":"备注","DownloadLink":"版式文件下载地址"}
             * SalesUnitInfo : {"SalesUnitName":"销方单位名称","SalesUnitTaxId":"销方单位税号","SalesUnitAddress":"销方单位地址","SalesUnitPhone":"销方单位电话","SalesUnitBankName":"销方单位开户行名称","SalesUnitBankAcount":"销方单位开户行账户"}
             * PurchaserUnitInfo : {"PurchaserUnitName":"购买方名称","PurchaserUnitTaxID":"购买方税号","PurchaserUnitAddress":"购买方地址","PurchaserUnitPhone":"购买方电话","PurchaserUnitBankName":"购买方开户行名称","PurchaserUnitBankAccount":"购买方开户行账号"}
             * CommodityInfo : {"TotalAmount":"合计金额","TotalTaxAmount":"合计税额","TotalPriceTax":"价税合计","CommodityList":[{"CommodityName":"第一条商品名称","CommodityCode":"商品编码","CommodityQuantity":"商品数量","DiscountLineCorrespondsLineNumber":"折行对应行号","InvoiceLineProperties":"发票行性质","LineNumber":"行号","MeasurementUnit":"计量单位","SpecificationModel":"规格型号","PreferentialPolicyID":"优惠政策标识","TaxExemptType":"免税类型","TaxRate":"税率","Tax":"税额","UnitPrice":"单价","Amount":"金额","VATSpecialManagement":"增值税特殊管理"},{"CommodityName":"第二条商品名称","CommodityCode":"商品编码","CommodityQuantity":"商品数量","DiscountLineCorrespondsLineNumber":"折行对应行号","InvoiceLineProperties":"发票行性质","LineNumber":"行号","MeasurementUnit":"计量单位","SpecificationModel":"规格型号","PreferentialPolicyID":"优惠政策标识","TaxExemptType":"免税类型","TaxRate":"税率","Tax":"税额","UnitPrice":"单价","Amount":"金额","VATSpecialManagement":"增值税特殊管理"}]}
             */

            public InvoiceInfoBean InvoiceInfo;
            public SalesUnitInfoBean SalesUnitInfo;
            public PurchaserUnitInfoBean PurchaserUnitInfo;
            public CommodityInfoBean CommodityInfo;

            public InvoiceInfoBean getInvoiceInfo() {
                return InvoiceInfo;
            }

            public void setInvoiceInfo(InvoiceInfoBean InvoiceInfo) {
                this.InvoiceInfo = InvoiceInfo;
            }

            public SalesUnitInfoBean getSalesUnitInfo() {
                return SalesUnitInfo;
            }

            public void setSalesUnitInfo(SalesUnitInfoBean SalesUnitInfo) {
                this.SalesUnitInfo = SalesUnitInfo;
            }

            public PurchaserUnitInfoBean getPurchaserUnitInfo() {
                return PurchaserUnitInfo;
            }

            public void setPurchaserUnitInfo(PurchaserUnitInfoBean PurchaserUnitInfo) {
                this.PurchaserUnitInfo = PurchaserUnitInfo;
            }

            public CommodityInfoBean getCommodityInfo() {
				return CommodityInfo;
			}

			public void setCommodityInfo(CommodityInfoBean commodityInfo) {
				CommodityInfo = commodityInfo;
			}




			public static class InvoiceInfoBean {
                /**
                 * InvoiceType : 开票类型
                 * RedInvoiceCode : 被红冲的正数发票代码
                 * RedInvoiceNumber : 被红冲的正数发票号码
                 * InvoiceNumber : 发票号码
                 * InvoiceCode : 发票代码
                 * MachineNumber : 税控设备编号
                 * CheckCode : 校验码
                 * TaxCode : 密码区
                 * InvoiceDate : 开票日期
                 * IssuerName : 开票人
                 * PayeeName : 收款人
                 * AuditorName : 复核人
                 * InvoicetypeCode : 发票类型代码
                 * Remark : 备注
                 * DownloadLink : 电子票版式文件下载地址
                 */

            	public String InvoiceType;
            	public String RedInvoiceCode;
            	public String RedInvoiceNumber;
            	public String InvoiceNumber;
            	public String InvoiceCode;
            	public String MachineNumber;
            	public String CheckCode;
            	public String TaxCode;
                public String InvoiceDate;
                public String IssuerName;
                public String PayeeName;
                public String AuditorName;
                public String InvoicetypeCode;
                public String Remark;
                public String DownloadLink;
                public String QRCode;//二维码信息

                public String getInvoiceType() {
                    return InvoiceType;
                }

                public void setInvoiceType(String InvoiceType) {
                    this.InvoiceType = InvoiceType;
                }

                public String getRedInvoiceCode() {
                    return RedInvoiceCode;
                }

                public void setRedInvoiceCode(String RedInvoiceCode) {
                    this.RedInvoiceCode = RedInvoiceCode;
                }

                public String getRedInvoiceNumber() {
                    return RedInvoiceNumber;
                }

                public void setRedInvoiceNumber(String RedInvoiceNumber) {
                    this.RedInvoiceNumber = RedInvoiceNumber;
                }

                public String getInvoiceNumber() {
                    return InvoiceNumber;
                }

                public void setInvoiceNumber(String InvoiceNumber) {
                    this.InvoiceNumber = InvoiceNumber;
                }

                public String getInvoiceCode() {
                    return InvoiceCode;
                }

                public void setInvoiceCode(String InvoiceCode) {
                    this.InvoiceCode = InvoiceCode;
                }

                public String getMachineNumber() {
                    return MachineNumber;
                }

                public void setMachineNumber(String MachineNumber) {
                    this.MachineNumber = MachineNumber;
                }

                public String getCheckCode() {
                    return CheckCode;
                }

                public void setCheckCode(String CheckCode) {
                    this.CheckCode = CheckCode;
                }

                public String getTaxCode() {
                    return TaxCode;
                }

                public void setTaxCode(String TaxCode) {
                    this.TaxCode = TaxCode;
                }

                public String getInvoiceDate() {
                    return InvoiceDate;
                }

                public void setInvoiceDate(String InvoiceDate) {
                    this.InvoiceDate = InvoiceDate;
                }

                public String getIssuerName() {
                    return IssuerName;
                }

                public void setIssuerName(String IssuerName) {
                    this.IssuerName = IssuerName;
                }

                public String getPayeeName() {
                    return PayeeName;
                }

                public void setPayeeName(String PayeeName) {
                    this.PayeeName = PayeeName;
                }

                public String getAuditorName() {
                    return AuditorName;
                }

                public void setAuditorName(String AuditorName) {
                    this.AuditorName = AuditorName;
                }

                public String getInvoicetypeCode() {
                    return InvoicetypeCode;
                }

                public void setInvoicetypeCode(String InvoicetypeCode) {
                    this.InvoicetypeCode = InvoicetypeCode;
                }

                public String getRemark() {
                    return Remark;
                }

                public void setRemark(String Remark) {
                    this.Remark = Remark;
                }

                public String getDownloadLink() {
                    return DownloadLink;
                }

                public void setDownloadLink(String DownloadLink) {
                    this.DownloadLink = DownloadLink;
                }
            }

            public static class SalesUnitInfoBean {
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

            public static class PurchaserUnitInfoBean {
                /**
                 * PurchaserUnitName : 购买方名称
                 * PurchaserUnitTaxID : 购买方税号
                 * PurchaserUnitAddress : 购买方地址
                 * PurchaserUnitPhone : 购买方电话
                 * PurchaserUnitBankName : 购买方开户行名称
                 * PurchaserUnitBankAccount : 购买方开户行账号
                 */

                public String PurchaserUnitName;
                public String PurchaserUnitTaxID;
                public String PurchaserUnitAddress;
                public String PurchaserUnitPhone;
                public String PurchaserUnitBankName;
                public String PurchaserUnitBankAccount;

                public String getPurchaserUnitName() {
                    return PurchaserUnitName;
                }

                public void setPurchaserUnitName(String PurchaserUnitName) {
                    this.PurchaserUnitName = PurchaserUnitName;
                }

                public String getPurchaserUnitTaxID() {
                    return PurchaserUnitTaxID;
                }

                public void setPurchaserUnitTaxID(String PurchaserUnitTaxID) {
                    this.PurchaserUnitTaxID = PurchaserUnitTaxID;
                }

                public String getPurchaserUnitAddress() {
                    return PurchaserUnitAddress;
                }

                public void setPurchaserUnitAddress(String PurchaserUnitAddress) {
                    this.PurchaserUnitAddress = PurchaserUnitAddress;
                }

                public String getPurchaserUnitPhone() {
                    return PurchaserUnitPhone;
                }

                public void setPurchaserUnitPhone(String PurchaserUnitPhone) {
                    this.PurchaserUnitPhone = PurchaserUnitPhone;
                }

                public String getPurchaserUnitBankName() {
                    return PurchaserUnitBankName;
                }

                public void setPurchaserUnitBankName(String PurchaserUnitBankName) {
                    this.PurchaserUnitBankName = PurchaserUnitBankName;
                }

                public String getPurchaserUnitBankAccount() {
                    return PurchaserUnitBankAccount;
                }

                public void setPurchaserUnitBankAccount(String PurchaserUnitBankAccount) {
                    this.PurchaserUnitBankAccount = PurchaserUnitBankAccount;
                }
            }

            public static class CommodityInfoBean {
                /**
                 * TotalAmount : 合计金额
                 * TotalTaxAmount : 合计税额
                 * TotalPriceTax : 价税合计
                 * CommodityList : [{"CommodityName":"第一条商品名称","CommodityCode":"商品编码","CommodityQuantity":"商品数量","DiscountLineCorrespondsLineNumber":"折行对应行号","InvoiceLineProperties":"发票行性质","LineNumber":"行号","MeasurementUnit":"计量单位","SpecificationModel":"规格型号","PreferentialPolicyID":"优惠政策标识","TaxExemptType":"免税类型","TaxRate":"税率","Tax":"税额","UnitPrice":"单价","Amount":"金额","VATSpecialManagement":"增值税特殊管理"},{"CommodityName":"第二条商品名称","CommodityCode":"商品编码","CommodityQuantity":"商品数量","DiscountLineCorrespondsLineNumber":"折行对应行号","InvoiceLineProperties":"发票行性质","LineNumber":"行号","MeasurementUnit":"计量单位","SpecificationModel":"规格型号","PreferentialPolicyID":"优惠政策标识","TaxExemptType":"免税类型","TaxRate":"税率","Tax":"税额","UnitPrice":"单价","Amount":"金额","VATSpecialManagement":"增值税特殊管理"}]
                 */

                public String TotalAmount;
                public String TotalTaxAmount;
                public String TotalPriceTax;
                public String TotalPriceTaxInWords;//转汉字
                public List<CommodityListBean> CommodityList;

                public String getTotalPriceTaxInWords() {
					return TotalPriceTaxInWords;
				}

				public void setTotalPriceTaxInWords(String totalPriceTaxInWords) {
					TotalPriceTaxInWords = totalPriceTaxInWords;
				}

				public String getTotalAmount() {
                    return TotalAmount;
                }

                public void setTotalAmount(String TotalAmount) {
                    this.TotalAmount = TotalAmount;
                }

                public String getTotalTaxAmount() {
                    return TotalTaxAmount;
                }

                public void setTotalTaxAmount(String TotalTaxAmount) {
                    this.TotalTaxAmount = TotalTaxAmount;
                }

                public String getTotalPriceTax() {
                    return TotalPriceTax;
                }

                public void setTotalPriceTax(String TotalPriceTax) {
                    this.TotalPriceTax = TotalPriceTax;
                }

                public List<CommodityListBean> getCommodityList() {
					return CommodityList;
				}

				public void setCommodityList(List<CommodityListBean> commodityList) {
					CommodityList = commodityList;
				}




				public static class CommodityListBean {
                    /**
                     * CommodityName : 第一条商品名称
                     * CommodityCode : 商品编码
                     * CommodityQuantity : 商品数量
                     * DiscountLineCorrespondsLineNumber : 折行对应行号
                     * InvoiceLineProperties : 发票行性质
                     * LineNumber : 行号
                     * MeasurementUnit : 计量单位
                     * SpecificationModel : 规格型号
                     * PreferentialPolicyID : 优惠政策标识
                     * TaxExemptType : 免税类型
                     * TaxRate : 税率
                     * Tax : 税额
                     * UnitPrice : 单价
                     * Amount : 金额
                     * VATSpecialManagement : 增值税特殊管理
                     */

                    public String CommodityName;
                    public String CommodityCode;
                    public String CommodityQuantity;
                    public String DiscountLineCorrespondsLineNumber;
                    public String InvoiceLineProperties;
                    public String LineNumber;
                    public String MeasurementUnit;
                    public String SpecificationModel;
                    public String PreferentialPolicyID;
                    public String TaxExemptType;
                    public String TaxRate;
                    public String Tax;
                    public String UnitPrice;
                    public String Amount;
                    public String VATSpecialManagement;

                    public String getCommodityName() {
                        return CommodityName;
                    }

                    public void setCommodityName(String CommodityName) {
                        this.CommodityName = CommodityName;
                    }

                    public String getCommodityCode() {
                        return CommodityCode;
                    }

                    public void setCommodityCode(String CommodityCode) {
                        this.CommodityCode = CommodityCode;
                    }

                    public String getCommodityQuantity() {
                        return CommodityQuantity;
                    }

                    public void setCommodityQuantity(String CommodityQuantity) {
                        this.CommodityQuantity = CommodityQuantity;
                    }

                    public String getDiscountLineCorrespondsLineNumber() {
                        return DiscountLineCorrespondsLineNumber;
                    }

                    public void setDiscountLineCorrespondsLineNumber(String DiscountLineCorrespondsLineNumber) {
                        this.DiscountLineCorrespondsLineNumber = DiscountLineCorrespondsLineNumber;
                    }

                    public String getInvoiceLineProperties() {
                        return InvoiceLineProperties;
                    }

                    public void setInvoiceLineProperties(String InvoiceLineProperties) {
                        this.InvoiceLineProperties = InvoiceLineProperties;
                    }

                    public String getLineNumber() {
                        return LineNumber;
                    }

                    public void setLineNumber(String LineNumber) {
                        this.LineNumber = LineNumber;
                    }

                    public String getMeasurementUnit() {
                        return MeasurementUnit;
                    }

                    public void setMeasurementUnit(String MeasurementUnit) {
                        this.MeasurementUnit = MeasurementUnit;
                    }

                    public String getSpecificationModel() {
                        return SpecificationModel;
                    }

                    public void setSpecificationModel(String SpecificationModel) {
                        this.SpecificationModel = SpecificationModel;
                    }

                    public String getPreferentialPolicyID() {
                        return PreferentialPolicyID;
                    }

                    public void setPreferentialPolicyID(String PreferentialPolicyID) {
                        this.PreferentialPolicyID = PreferentialPolicyID;
                    }

                    public String getTaxExemptType() {
                        return TaxExemptType;
                    }

                    public void setTaxExemptType(String TaxExemptType) {
                        this.TaxExemptType = TaxExemptType;
                    }

                    public String getTaxRate() {
                        return TaxRate;
                    }

                    public void setTaxRate(String TaxRate) {
                        this.TaxRate = TaxRate;
                    }

                    public String getTax() {
                        return Tax;
                    }

                    public void setTax(String Tax) {
                        this.Tax = Tax;
                    }

                    public String getUnitPrice() {
                        return UnitPrice;
                    }

                    public void setUnitPrice(String UnitPrice) {
                        this.UnitPrice = UnitPrice;
                    }

                    public String getAmount() {
                        return Amount;
                    }

                    public void setAmount(String Amount) {
                        this.Amount = Amount;
                    }

                    public String getVATSpecialManagement() {
                        return VATSpecialManagement;
                    }

                    public void setVATSpecialManagement(String VATSpecialManagement) {
                        this.VATSpecialManagement = VATSpecialManagement;
                    }
                }
            }
        }
    }
}
