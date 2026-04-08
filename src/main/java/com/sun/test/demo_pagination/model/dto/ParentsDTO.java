package com.sun.test.demo_pagination.model.dto;

import lombok.Data;

import java.util.Set;

@Data
public class ParentsDTO {


    private String parentsCode;
    private String status;
    private String start;
    private String end;
    private MemberDTO firstParents;
    private MemberDTO secondParents;
    private String isReleationPublicized;
    private String isNatural;
    private Set<MemberDTO> children;

}
