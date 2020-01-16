package com.dzf.file.fastdfs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FastDfsConfig {
    @Value("${zxkj.fastdfs.connect_timeout}")
    public int connect_timeout;//连接tracker服务器超时时长
    @Value("${zxkj.fastdfs.network_timeout}")
    public int network_timeout;//network_timeout
    @Value("${zxkj.fastdfs.charset}")
    public String charset;//文件内容编码
    @Value("${zxkj.fastdfs.http.tracker_http_port}")
    public int tracker_http_port;// tracker服务器端口
    @Value("${zxkj.fastdfs.http.anti_steal_token}")
    public String anti_steal_token;//
    @Value("${zxkj.fastdfs.http.secret_key}")
    public String secret_key;//
    @Value("${zxkj.fastdfs.tracker_server}")
    public String tracker_server;//tracker服务器IP和端口（可以写多个）
}
