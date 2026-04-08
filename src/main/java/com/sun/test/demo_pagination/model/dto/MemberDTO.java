package com.sun.test.demo_pagination.model.dto;

import lombok.Data;

import java.util.Set;

@Data
public class MemberDTO {

    private String familyCode;//To Use to save Member info when 'family' Obj as null
    private String parentsCode;//To Use to save Member info when 'parents' Obj as null
    private String memberCode;
    private String name;
    private String dob;
    private String mobile;
    private String gender;
    private int bOrder;

    private FamilyDTO family;
    private ParentsDTO parents;
    private ParentsDTO parenting;



}
