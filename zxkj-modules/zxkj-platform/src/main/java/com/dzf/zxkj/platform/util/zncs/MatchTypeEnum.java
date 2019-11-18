package com.dzf.zxkj.platform.util.zncs;

public enum MatchTypeEnum
{
  CORPNAME("CORPORATION", "公司名称", 0), 

  CORPCODE("CORPORATION", "公司编码", 1), 

  CUSTOMER("CUSTOMER", "客户", 2), 

  SUPPLIER("SUPPLIER", "供应商", 3), 

  INVENTORY("INVENTORY", "存货", 4);

  private final String key;
  private final String name;
  private final int value;

  private MatchTypeEnum(String key, String name, int value)
  {
    this.key = key;
    this.name = name;
    this.value = value;
  }

  public static MatchTypeEnum getTypeEnumByName(String name)
  {
    for (MatchTypeEnum item : values()) {
      if (item.getName().equals(name)) {
        return item;
      }
    }
    return null;
  }

  public static MatchTypeEnum getTypeEnumByValue(int value)
  {
    for (MatchTypeEnum item : values()) {
      if (item.getValue() == value) {
        return item;
      }
    }
    return null;
  }

  public String getKey() {
    return this.key;
  }

  public String getName() {
    return this.name;
  }

  public int getValue() {
    return this.value;
  }
}