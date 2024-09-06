package kimp.chat.service.serviceImpl;

import kimp.chat.dao.daoImpl.ChatDao;
import kimp.chat.entity.Chat;
import kimp.chat.service.ChatService;

import org.springframework.stereotype.Service;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatDao chatDao;

    public ChatServiceImpl(ChatDao chatDao){
        this.chatDao = chatDao;
    }

    @Override
    public Chat createChat(String chatID, String content){

        return chatDao.insertChat(chatID, content);
    }

}
