package com.sun.test.demo_pagination.service;

import com.sun.test.demo_pagination.model.dto.MemberDTO;

public interface UserService {

MemberDTO memberSynch(MemberDTO request);

MemberDTO doProcess(MemberDTO request);
}
