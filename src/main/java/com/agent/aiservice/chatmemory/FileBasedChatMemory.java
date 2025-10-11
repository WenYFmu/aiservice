package com.agent.aiservice.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.Builder;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Builder
public class FileBasedChatMemory implements ChatMemory {

    private final String BASE_DIR;

    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false);//为了不提供一个对象的结构。。。大概
        // 设置实例化策略
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }
    public FileBasedChatMemory(String baseDir) {
        BASE_DIR = baseDir;
        File file = new File(BASE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        //先读取历史记录拼接后保存
        List<Message> messageList = getOrCreateConversation(conversationId);
        messageList.addAll(messages);
        saveConversation(conversationId, messageList);
    }

    @Override
    public List<Message> get(String conversationId) {
        return getOrCreateConversation(conversationId);
    }

    @Override
    public void clear(String conversationId) {
        File conversationFile = getConversationFile(conversationId);
        if (conversationFile.exists()) {
            conversationFile.delete();
        }
    }

    private void saveConversation(String conversationId, List<Message> messages) {
        File conversationFile = getConversationFile(conversationId);
        Output output = null;
        try {
            output = new Output(new FileOutputStream(conversationFile));
            kryo.writeObject(output, messages);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }finally {
            if (output != null) {
                output.close();
            }
        }
    }


    /**
     * 根据conversationId从文件中读取记忆
     * @param conversationId
     * @return 消息记忆
     */
    private List<Message> getOrCreateConversation(String conversationId) {
        File conversationFile = getConversationFile(conversationId);
        List<Message> messages = new ArrayList<>();
        Input input = null;
        if(conversationFile.exists()) {
            try {
                input = new Input(new FileInputStream(conversationFile));
                messages = kryo.readObject(input, ArrayList.class);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }finally {
                if (input != null) {
                    input.close();
                }
            }
        }
        return messages;
    }

    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kory");
    }
}
