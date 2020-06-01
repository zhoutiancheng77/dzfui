package com.dzf.zxkj.app.utils;

import java.io.IOException;



public interface IDzfSerializable<T> {
void setSerializable(T svo, NetObjectOutputStream nos)throws IOException;
T getSerializable(NetObjectInputStream nos)throws IOException, ClassNotFoundException;
}
