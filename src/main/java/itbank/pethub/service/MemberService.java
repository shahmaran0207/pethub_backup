package itbank.pethub.service;

import itbank.pethub.aop.PasswordEncoder;
import itbank.pethub.dto.MemberDetails;
import itbank.pethub.model.MemberDAO;
import itbank.pethub.oauth2.service.OAuth2UserPrincipal;
import itbank.pethub.vo.CouponVO;
import itbank.pethub.vo.MemberVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MemberService implements UserDetailsService {

    @Autowired
    private MemberDAO dao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberVO memberData = dao.findByUserid(username);

        if (memberData != null) {
            return new MemberDetails(memberData);

        }

        return null;
    }

    public MemberVO login(MemberVO input) {
        String hash = input.getUserpw();
        return dao.selectOne(input);
    }

    @Transactional
    public MemberVO signUp(MemberVO input) {
        dao.insert(input);
        if (input.getAd() == 1){
            dao.insertAd(input);
            input.setAd(1);
        }
        return input;
    }

    @Transactional
    public int insertAdd(MemberVO input){
        MemberVO member = dao.selectOneNoAddress(input);
        member.setAddress_details(input.getAddress_details());
        member.setPrimary_address(input.getPrimary_address());
        member.setZip_code(input.getZip_code());

        return dao.insertAddress(member);
    }

    @Transactional
    public int update(MemberVO input) {
        if(updateAddress(input) == dao.update(input)){
            return 1;
        }
        return 0;
    }

    public void delete(MemberVO member) {
        dao.delete(member);
    }

    @Transactional(readOnly = true)
    public String findId(MemberVO input) {
        return dao.findId(input);
    }

    public String findPw(MemberVO input) {
        MemberVO member = dao.findPw(input);
        if(member != null){
            String newPw = UUID.randomUUID().toString().substring(0, 8);

            // 해쉬처리
            member.setUserpw(PasswordEncoder.encode(newPw));
            dao.newPw(member);
            return newPw;
        }
        return null;
    }

    public boolean isUserIdExists(String userid) {
        return dao.countByUserId(userid) > 0;
    }

    public List<CouponVO> couponFindbyId(int member_id) {
        return dao.couponFindbyId(member_id);
    }

    public int updateAddress(MemberVO user) {
        return dao.addressUpdate(user);
    }

    @Transactional
    public int updateNoProfile(MemberVO user) {
        if(dao.updateNoProfile(user) == updateAddress(user)){
            return 1;
        }
        return 0;
    }

    public int addSnsuser(MemberVO memberVO) {
        return dao.insertSns(memberVO);
    }

    public MemberVO Snslogin(MemberVO member) {

        return dao.selectSnsUser(member.getUserid());
    }

    public int updateSnsUser(MemberVO memberVO) {

        return dao.updateSns(memberVO);

    }


    public int insertSnsAdd(MemberVO member) {

        return dao.insertAddress(member);
    }

    public MemberVO SnsNoAddress(MemberVO member) {

        return dao.selectSnsOneNoAddress(member);
    }

    public MemberVO findUserbyUserId(MemberVO input) {
        return dao.findUserbyUserId(input);
    }
}
