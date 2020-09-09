package com.chat.server.model;

import lombok.Data;

import java.util.Date;

@Data
public class BaseMsg {
    private Date date;
    private Integer type;
}
