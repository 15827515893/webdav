package com.chat.server.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMsg {
    private String userId;
    private String message;
    private String friendId;
    private String username;
    private String type;
}
