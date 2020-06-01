package com.dzf.zxkj.app.model.report;


import com.dzf.zxkj.common.model.SuperVO;

public class AppFzChVo extends SuperVO {
	
	private String sl;//剩余数量
	
	private String je;//剩余金额
	
	private AppFzMx1[] fzmxvos1;

	public String getSl() {
		return sl;
	}

	public void setSl(String sl) {
		this.sl = sl;
	}

	public String getJe() {
		return je;
	}

	public void setJe(String je) {
		this.je = je;
	}

	public AppFzMx1[] getFzmxvos1() {
		return fzmxvos1;
	}

	public void setFzmxvos1(AppFzMx1[] fzmxvos1) {
		this.fzmxvos1 = fzmxvos1;
	}

	public static class AppFzMx1 extends SuperVO{
		//辅助名称
		private String name;
		
		private String code;
		
		//带科目的vo
		private AppFzmx2[] fzmxvos2;
		
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public AppFzmx2[] getFzmxvos2() {
			return fzmxvos2;
		}

		public void setFzmxvos2(AppFzmx2[] fzmxvos2) {
			this.fzmxvos2 = fzmxvos2;
		}

		@Override
		public String getPKFieldName() {
			return null;
		}

		@Override
		public String getParentPKFieldName() {
			return null;
		}

		@Override
		public String getTableName() {
			return null;
		}
	}
	
	

	//辅助详情
	public static class AppFzmx2 extends SuperVO{
		
		//科目名称
		private String name;
		
		private String sysl;//剩余数量
		
		private String syje;// 剩余金额
		
		private String sydj;// 剩余单价
		
		private AppFzKmMxVo[] fzmxvos3;

		public String getSysl() {
			return sysl;
		}

		public void setSysl(String sysl) {
			this.sysl = sysl;
		}

		public String getSyje() {
			return syje;
		}

		public void setSyje(String syje) {
			this.syje = syje;
		}

		public String getSydj() {
			return sydj;
		}

		public void setSydj(String sydj) {
			this.sydj = sydj;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public AppFzKmMxVo[] getFzmxvos3() {
			return fzmxvos3;
		}

		public void setFzmxvos3(AppFzKmMxVo[] fzmxvos3) {
			this.fzmxvos3 = fzmxvos3;
		}

		// 科目详情
		public static class AppFzKmMxVo extends SuperVO{
			
			private String rq;
			
			private String fx;
			
			private String sl;//数量
			
			private String dj;// 单价
			
			private String je;//金额

			public String getRq() {
				return rq;
			}

			public void setRq(String rq) {
				this.rq = rq;
			}

			public String getFx() {
				return fx;
			}

			public void setFx(String fx) {
				this.fx = fx;
			}

			public String getSl() {
				return sl;
			}

			public void setSl(String sl) {
				this.sl = sl;
			}

			public String getDj() {
				return dj;
			}

			public void setDj(String dj) {
				this.dj = dj;
			}

			public String getJe() {
				return je;
			}

			public void setJe(String je) {
				this.je = je;
			}

			@Override
			public String getPKFieldName() {
				return null;
			}

			@Override
			public String getParentPKFieldName() {
				return null;
			}

			@Override
			public String getTableName() {
				return null;
			}
			
		}

		@Override
		public String getPKFieldName() {
			return null;
		}

		@Override
		public String getParentPKFieldName() {
			return null;
		}

		@Override
		public String getTableName() {
			return null;
		}
		
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}
