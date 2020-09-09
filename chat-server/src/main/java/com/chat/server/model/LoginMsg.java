package com.chat.server.model;

import lombok.Data;

@Data
public class LoginMsg extends BaseMsg{

    private String username;
    private String pwd;
}
