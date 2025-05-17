package com.purgersaint.lizhiai;

//大模型类
public class llm{
    private String name;        //别名
    private String url;         //API路径
    private String key;         //密钥
    private String modelName;  //模型名称
    private double tem;         //温度值（严谨与想象，最大2）

    public llm(String n,String u,String k,String m,double t){
        this.name=n;
        this.url=u;
        this.key=k;
        this.modelName = m;
        this.tem = t;
    }
}
