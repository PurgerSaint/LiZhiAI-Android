package com.purgersaint.lizhiai;

//大模型类
public class llm{
    String name;        //别名
    String url;         //API路径
    String key;         //密钥
    String modelName;  //模型名称
    double tem;         //温度值（严谨与想象，最大2）

    public llm(String n,String u,String k,String m,double t){
        this.name=n;
        this.url=u;
        this.key=k;
        this.modelName = m;
        this.tem = t;
    }
}
