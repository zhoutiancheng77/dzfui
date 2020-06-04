package com.dzf.zxkj.app.model.ticket;

public class FontText {
    
    private String text;
    
    private int wm_text_pos_w;
    
    private int wm_text_pos_h;
    
    private String wm_text_color;
    
    private Integer wm_text_size;
    
    private String wm_text_font;//字体  “宋体，Arial”

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getWm_text_pos_w() {
		return wm_text_pos_w;
	}

	public void setWm_text_pos_w(int wm_text_pos_w) {
		this.wm_text_pos_w = wm_text_pos_w;
	}

	public int getWm_text_pos_h() {
		return wm_text_pos_h;
	}

	public void setWm_text_pos_h(int wm_text_pos_h) {
		this.wm_text_pos_h = wm_text_pos_h;
	}

	public String getWm_text_color() {
        return wm_text_color;
    }

    public void setWm_text_color(String wm_text_color) {
        this.wm_text_color = wm_text_color;
    }

    public Integer getWm_text_size() {
        return wm_text_size;
    }

    public void setWm_text_size(Integer wm_text_size) {
        this.wm_text_size = wm_text_size;
    }

    public String getWm_text_font() {
        return wm_text_font;
    }

    public void setWm_text_font(String wm_text_font) {
        this.wm_text_font = wm_text_font;
    }

    public FontText(String text, int wm_text_pos_w,int wm_text_pos_h, String wm_text_color,
            Integer wm_text_size, String wm_text_font) {
        super();
        this.text = text;
        this.wm_text_pos_w = wm_text_pos_w;
        this.wm_text_pos_h = wm_text_pos_h;
        this.wm_text_color = wm_text_color;
        this.wm_text_size = wm_text_size;
        this.wm_text_font = wm_text_font;
    }
    
    
    
    public FontText(String text, int wm_text_pos_w, int wm_text_pos_h) {
		super();
		this.text = text;
		this.wm_text_pos_w = wm_text_pos_w;
		this.wm_text_pos_h = wm_text_pos_h;
		
		this.wm_text_color = "#444444";
		this.wm_text_size = 12;
		this.wm_text_font = "宋体";
	}
    
    public FontText(String text, int wm_text_pos_w, int wm_text_pos_h,int wm_text_size) {
		super();
		this.text = text;
		this.wm_text_pos_w = wm_text_pos_w;
		this.wm_text_pos_h = wm_text_pos_h;
		
		this.wm_text_color = "#444444";
		this.wm_text_size = wm_text_size;
		this.wm_text_font = "宋体";
	}
    
    
    

	public FontText(String text, int wm_text_pos_w, int wm_text_pos_h, Integer wm_text_size) {
		super();
		this.text = text;
		this.wm_text_pos_w = wm_text_pos_w;
		this.wm_text_pos_h = wm_text_pos_h;
		this.wm_text_size = wm_text_size;
		
		this.wm_text_color = "#444444";
		this.wm_text_font = "宋体";
	}

	public FontText(){}
    
}