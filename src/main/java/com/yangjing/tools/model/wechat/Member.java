package com.yangjing.tools.model.wechat;

import lombok.Data;

import java.util.List;

@Data
public class Member {

    private String userid;

    private String name;

    private String email;

    private boolean to_invite;

    private List<Integer> department;
}
