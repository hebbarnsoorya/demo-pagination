package com.sun.test.demo_pagination.service;

import com.sun.test.demo_pagination.model.Family;
import com.sun.test.demo_pagination.model.Member;
import com.sun.test.demo_pagination.model.Parents;
import com.sun.test.demo_pagination.model.dto.FamilyDTO;
import com.sun.test.demo_pagination.model.dto.MemberDTO;
import com.sun.test.demo_pagination.model.dto.ParentsDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public MemberDTO memberSynch(MemberDTO request) {
        return this.doProcess(request);
    }

    @Override
    public MemberDTO doProcess(MemberDTO request) {
        return nestedProcess(request, null, null);
    }

    private MemberDTO nestedProcess(MemberDTO request, Parents inheritedParents, Family inheritedFamily) {

        Family family = null;
        Parents parents = null;
        Parents parenting = null;
        Member currentMember = null;

        //STEP-001 - Family Info
        if(inheritedFamily != null){
            family = inheritedFamily;
        } else if (request.getFamily() != null) {

                //Save Family Info if not, and Get 'fCode'
            family =  fromDTO(request.getFamily());

        }

        //STEP-002 - Parents Info
        if(inheritedParents != null){
            parents = inheritedParents;
        } else if (request.getParents() != null) {

            //STEP-002a - Save FirstMember from Parents
            Member firstMember = saveMemberInfoWithFamily(fromDTO(request.getParents().getFirstParents()),family);
//TODO: NESTED CALL HERE for Parent's firstMember
            //STEP-002b - Save SecondMember from Parents
            Member secondMember = saveMemberInfoWithFamily(fromDTO(request.getParents().getSecondParents()),family);
//TODO: NESTED CALL HERE for  Parent's  secondMember

            //Save Parents Info (if not), and Get 'pCode'
            parents =  saveParentsWithMembers(fromDTO(request.getParents()) , firstMember, secondMember);

            //STEP-002c - Parents's - Save Children info
            //TODO: NESTED CALL HERE for Parent's Children
            if (request.getParents().getChildren() != null) {
                nestedProcess(request.getParents().getChildren().stream().findFirst().get(), parents, family);
            }
        }
        //STEP-002.1 - Save Current Member's Info
        currentMember = saveMemberInfoWithParents(fromDTO(request), parents);

        //STEP-003 - Parenting Info
        if (request.getParenting() != null) {
            ////MemberDTO secondMemberDTO = request.getParenting().getSecondParents();
            /////Member secondcurrentMember = fromDTO(nestedProcess(request.getParenting().getSecondParents(), null, null));
            ////TODO for nested CALL HERE for Parenting's secondcurrentMember's FAMILY's ALL Info
            Member secondcurrentMember =  saveMemberInfoWithFamily(fromDTO(request.getParenting().getSecondParents()),family);
            parenting =   saveParentsWithMembers(fromDTO(request.getParenting()), currentMember, secondcurrentMember);

            //STEP-004 - Save Children info
            //TODO: NESTED CALL HERE for Parenting's Children
            if (request.getParenting().getChildren() != null) {
                nestedProcess(request.getParenting().getChildren().stream().findFirst().get(), parenting, family);
            }
        }

        return toDTO(currentMember);
    }

    private Member saveMemberInfoWithFamily(Member mem, Family family) {
        Member savedMember = new Member();

        return savedMember;
    }

    private Member saveMemberInfoWithParents(Member mem, Parents parents) {
        Member savedMember = new Member();

        return savedMember;
    }

    private Parents saveParentsWithMembers(Parents pObject, Member firstMember, Member secondMember) {

        //Save Parents info and return Parents Entity
        Parents savedParents = new Parents();

        return savedParents;
    }
/// //Mappper:

    private Member fromDTO(MemberDTO dto){
        Member entity = new Member();
        return entity;
    }

    private MemberDTO toDTO(Member entity ){
        MemberDTO dto = new MemberDTO();
        return dto;
    }

    private Parents fromDTO(ParentsDTO dto){
        Parents entity = new Parents();
        return entity;
    }

    private ParentsDTO toDTO(Parents entity){
        ParentsDTO dto = new ParentsDTO();
        return dto;
    }

    private Family fromDTO(FamilyDTO dto){
        Family entity = new Family();
        return entity;
    }

    private FamilyDTO toDTO(Family entity){
        FamilyDTO dto = new FamilyDTO();
        return dto;
    }
}
