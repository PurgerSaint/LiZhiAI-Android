package com.purgersaint.lizhiai;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

//大模型类
@Entity
public class LLM{

    @PrimaryKey(autoGenerate = true)
    public int id;             //模型标识号

    public String name;        //别名
    public String url;         //API路径
    public String key;         //密钥
    public String modelName;   //模型名称
    public double tem;         //温度值（严谨与想象，最大2）

    public LLM(){}

    @Ignore
    public LLM(String n,String u,String k,String m,double t){
        this.name=n;
        this.url=u;
        this.key=k;
        this.modelName = m;
        this.tem = t;
    }

    @Override
    public boolean equals(Object o){
        //同一对象引用，必然相等
        if(this == o) return true;
        //不同类型或 o 为 null，不等
        if(o == null || getClass() != o.getClass()) return false;
        LLM llm = (LLM) o; // 类型转换
        // 比较所有相关的字段
        return id == llm.id &&
                Double.compare(llm.tem, tem) == 0 && //比较double类型要用Double.compare
                Objects.equals(name, llm.name) &&
                Objects.equals(url, llm.url) &&
                Objects.equals(key, llm.key) &&
                Objects.equals(modelName, llm.modelName);
    }

    @Override
    public int hashCode(){
        //使用 Objects.hash 生成哈希码，更安全和方便
        return Objects.hash(id,name,url,key,modelName,tem);
    }
}

//Dao接口
@Dao
interface LlmDao{
    @Insert
    long insert(LLM item);
    //返回类型为 long，可以获取新插入行的 rowId (即自增的id)

    @Insert
    void insertAll(List<LLM> list);

    @Query("SELECT * FROM LLM")
    List<LLM> getAll();

    @Update
    int update(LLM item);

    @Query("SELECT * FROM LLM WHERE id = :id")
    LLM getById(int id);

    @Delete
    void delete(LLM item);

    @Query("DELETE FROM LLM")
    void deleteAll();

    // 使用事务确保原子性
    @Transaction
    default void replaceAllData(List<LLM> newLlmList) {
        deleteAll();
        if (newLlmList != null && !newLlmList.isEmpty()) {
            insertAll(newLlmList);
        }
    }
}

//数据库类
@Database(entities = {LLM.class},version = 1)
abstract class LLMAppDatabase extends RoomDatabase{
    abstract LlmDao LLMDao();
}

//测试
class LLMData{
    private static final String TAG = "LLMData"; // 日志标签
    //使用 Collections.synchronizedList 来获取一个线程安全的 List 包装器
    public final List<LLM> LLMlist = Collections.synchronizedList(new ArrayList<>());
    public LLM user;    //用户选择的大模型
    private LLMAppDatabase LLMdb;
    private LlmDao llm_dao;
    // 创建一个单线程的 ExecutorService 来顺序执行所有数据库操作，避免并发问题
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    //标记是否初始化完成
    public final AtomicBoolean flag = new AtomicBoolean(false);

    //初始化
    LLMData(Context context,Runnable onInitialized){
        //创建后台线程操作
        databaseExecutor.execute(() -> {
            try{
                //建立数据库连接
                LLMdb = Room.databaseBuilder(context.getApplicationContext(),LLMAppDatabase.class,"llm_db").build();
                //创建数据库连接对象
                llm_dao = LLMdb.LLMDao();
                getFromdb(); // 调用一个统一的方法加载初始数据
                flag.set(true); // 标记初始化完成
                Log.d(TAG, "LLMData已初始化并已填充缓存。");
                new Handler(Looper.getMainLooper()).post(onInitialized); // 主线程回调
            }catch(Exception e){
                flag.set(false); // 初始化失败
                Log.e(TAG, "LLMData初始化失败。", e);
            }
        });
    }

    //从数据库中刷新整个缓存
    public void getFromdb(){
        if(llm_dao ==null){
            Log.w(TAG,"数据库还未初始化");
            return;
        }
        try{
            List<LLM> newData = llm_dao.getAll();
            LLMlist.clear();
            LLMlist.addAll(newData);
            Log.d(TAG, "缓存刷新成功,当前大小为: " + LLMlist.size());
        }catch(Exception e){
            Log.e(TAG, "从数据库中获取缓存出错.", e);
        }
    }

    //增加
    public void add(LLM model,Runnable onSuccess){
        //线程操作,避免数据不同步
        databaseExecutor.execute(() -> {
            if(!flag.get()||llm_dao ==null){
                Log.w(TAG,"数据库还未初始化");
                return;
            }
            //将数据加入到数据库中
            model.id = (int)llm_dao.insert(model);
            if(model.id>0){
                //加入缓存
                LLMlist.add(model);
                new Handler(Looper.getMainLooper()).post(onSuccess); // 主线程回调
            }else{
                Log.d(TAG,"添加失败");
            }
        });
    }

    //删除
    public void del(LLM model){
        //线程操作,避免数据不同步
        databaseExecutor.execute(() -> {
            if(LLMlist.isEmpty()) {
                Log.w(TAG,"数组为空,无法操作!");
                return;
            }
            if(!flag.get()||llm_dao ==null){
                Log.w(TAG,"数据库还未初始化");
                return;
            }
            for(LLM now:LLMlist){
                if(now.id == model.id){
                    //数据库中进行删除
                    llm_dao.delete(model);
                    //缓存中进行删除
                    LLMlist.remove(model);
                    return;
                }
            }
            Log.d(TAG,"找不到删除的元素!");
        });
    }

    //改
    public void change(LLM model,Runnable onSuccess){
        //线程操作,避免数据不同步
        databaseExecutor.execute(() -> {
            if(LLMlist.isEmpty()) {
                Log.w(TAG,"数组为空,无法操作!");
                return;
            }
            if(!flag.get()||llm_dao ==null){
                Log.w(TAG,"数据库还未初始化");
                return;
            }
            //修改数据库的内容
            int t = llm_dao.update(model);
            Log.d(TAG,"成功数量:"+t);
            new Handler(Looper.getMainLooper()).post(onSuccess);
        });
    }
}