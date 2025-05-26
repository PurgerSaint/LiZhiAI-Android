package com.purgersaint.lizhiai;


import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Relation;
import androidx.room.RoomDatabase;
import androidx.room.Transaction;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import org.threeten.bp.LocalDate;
import java.util.ArrayList;
import java.util.List;

//每次
@Entity(tableName = "chats",
        foreignKeys = @ForeignKey(entity = ChatMsg.class,
                parentColumns = "id",
                childColumns = "chatMsgId",
                onDelete = ForeignKey.CASCADE))
class Chat{
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long chatMsgId;      //外键，关联到ChatMsg
    public String model;       //使用的模型
    public String userText;    //用户发送的文字
    public String modelText;   //模型回答的结果

    public Chat(String m,String u,String mt){
        this.model = m;
        this.userText = u;
        this.modelText = mt;
    }

    public Chat(long id,String m,String u,String mt){
        this.id=id;
        this.model = m;
        this.userText = u;
        this.modelText = mt;
    }
}
@Dao
public interface ChatDao{
    @Insert
    long insertChatMsg(ChatMsg chatMsg);

    @Insert
    void insertChat(Chat chat);

    @Query("SELECT * FROM chat_messages ORDER BY time DESC")
    List<ChatMsg> getAllChatMsgs();

    @Query("SELECT * FROM chats WHERE chatMsgId = :chatMsgId")
    List<Chat> getChatsForMessage(long chatMsgId);

    @Transaction
    @Query("SELECT * FROM chat_messages ORDER BY time DESC")
    List<ChatMsgWithChats> getChatMsgsWithChats();
}
//每轮对话
@Entity(tableName = "chat_messages")
public class ChatMsg {
    @PrimaryKey(autoGenerate = true)
    public long id;                 //每轮对话的ID
    public String title;            //每轮对话的标题
    @Ignore
    public ArrayList<Chat> Msg;     //每轮对话
    @TypeConverters(DateConverter.class)
    public LocalDate time;          //对话时间

    public ChatMsg(){
        this.time = LocalDate.now();
        this.title = this.time.getMonth()+"月"+this.time.getDayOfMonth()+"日";
        this.Msg = new ArrayList<>();
    }

    public void add(Chat chatAll){
        this.Msg.add(chatAll);
    }

    public ChatMsg(long id, String title, LocalDate time) {
        this.id = id;
        this.title = title;
        this.time = time;
    }
}

//类型转换器
class DateConverter {
    @TypeConverter
    public static String fromLocalDate(LocalDate date) {
        return date == null ? null : date.toString();
    }

    @TypeConverter
    public static LocalDate toLocalDate(String dateString) {
        return dateString == null ? null : LocalDate.parse(dateString);
    }
}

//关系类
class ChatMsgWithChats {
    @Embedded
    public ChatMsg chatMsg;

    @Relation(
            parentColumn = "id",
            entityColumn = "chatMsgId"
    )
    public List<Chat> chats;
}

//数据库类
@Database(entities = {ChatMsg.class, Chat.class},
        version = 2,
        exportSchema = false)
@TypeConverters({DateConverter.class})
abstract class ChatDatabase extends RoomDatabase {
    public abstract ChatDao chatDao();
}