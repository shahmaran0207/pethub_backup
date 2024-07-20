package itbank.pethub.controller;

import itbank.pethub.service.BoardService;
import itbank.pethub.service.EmailService;
import itbank.pethub.vo.BoardVO;
import itbank.pethub.vo.MemberVO;
import itbank.pethub.vo.ReplyVO;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import itbank.pethub.vo.ContactForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService bs;
    private final EmailService es;

    @GetMapping("/help")
    public void help(Model model) {
        model.addAttribute("contactForm", new ContactForm());
    }

    // 전체 게시판
    @GetMapping("/list")
    public ModelAndView list(@RequestParam Map<String, Object> param) {
        ModelAndView mav = new ModelAndView();

        Map<String, Object> data = bs.getBoards(param);
        mav.addObject("map", data);

        // 공지사항 목록을 추가
        List<BoardVO> recentNotices = (List<BoardVO>) data.get("recentNotices");
        mav.addObject("recentNotices", recentNotices);
        mav.setViewName("board/list");

        return mav;
    }

    // 공지사항
    @GetMapping("/notice")
    public ModelAndView notice(@RequestParam Map<String, Object> param) {
        ModelAndView mav = new ModelAndView();

        mav.addObject("map", bs.getNotices(param));
        mav.setViewName("board/notice");

        return mav;
    }

    // 강아지 게시판
    @GetMapping("/dog")
    public ModelAndView dog(@RequestParam Map<String, Object> param) {
        ModelAndView mav = new ModelAndView();

        Map<String, Object> data = bs.getDogs(param);
        mav.addObject("map", data);

        List<BoardVO> recentNotices = (List<BoardVO>) data.get("recentNotices");
        mav.addObject("recentNotices", recentNotices);
        mav.setViewName("board/dog");

        return mav;
    }

    // 고양이 게시판
    @GetMapping("/cat")
    public ModelAndView cat(@RequestParam Map<String, Object> param) {
        ModelAndView mav = new ModelAndView();

        Map<String, Object> data = bs.getCats(param);
        mav.addObject("map", data);

        List<BoardVO> recentNotices = (List<BoardVO>) data.get("recentNotices");
        mav.addObject("recentNotices", recentNotices);
        mav.setViewName("board/cat");

        return mav;
    }

    // 새 게시판
    @GetMapping("/bird")
    public ModelAndView bird(@RequestParam Map<String, Object> param) {
        ModelAndView mav = new ModelAndView();

        Map<String, Object> data = bs.getBirds(param);
        mav.addObject("map", data);

        List<BoardVO> recentNotices = (List<BoardVO>) data.get("recentNotices");
        mav.addObject("recentNotices", recentNotices);
        mav.setViewName("board/bird");

        return mav;
    }

    // 기타 게시판
    @GetMapping("/etc")
    public ModelAndView etc(@RequestParam Map<String, Object> param) {
        ModelAndView mav = new ModelAndView();

        Map<String, Object> data = bs.getEtcs(param);
        mav.addObject("map", data);

        List<BoardVO> recentNotices = (List<BoardVO>) data.get("recentNotices");
        mav.addObject("recentNotices", recentNotices);
        mav.setViewName("board/etc");

        return mav;
    }

    // 자유 게시판
    @GetMapping("/free")
    public ModelAndView free(@RequestParam Map<String, Object> param) {
        ModelAndView mav = new ModelAndView();

        Map<String, Object> data = bs.getfrees(param);
        mav.addObject("map", data);

        List<BoardVO> recentNotices = (List<BoardVO>) data.get("recentNotices");
        mav.addObject("recentNotices", recentNotices);
        mav.setViewName("board/free");

        return mav;
    }

    // 글작성
    @GetMapping("/write")
    public void write() {}

    @PostMapping("/write")
    public ModelAndView addWrite(BoardVO input, HttpSession session) throws MessagingException {
        ModelAndView mav = new ModelAndView();
        MemberVO user = (MemberVO) session.getAttribute("user");
        input.setMember_id(user.getId());

        bs.addWrite(input);
        if(input.getType() == 1){
            es.sendNotice(input);
        }

        mav.setViewName("redirect:/board/list");
        return mav;
    }

    // 글 내용 보기
    @GetMapping("/view/{id}")
    public ModelAndView view(@PathVariable(name = "id") int id, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        bs.viewCount(id);

        BoardVO row = bs.getBoard(id);
        mav.addObject("row", row);

        MemberVO user = (MemberVO) session.getAttribute("user");
        mav.addObject("user", user);

        List<ReplyVO> reply = bs.getReplies(id);
        mav.addObject("replys", reply);

        mav.setViewName("board/view");

        return mav;
    }

    // 게시판 수정
    @GetMapping("/updateBd/{id}")
    public ModelAndView updateBd(@PathVariable int id) {
        ModelAndView mav = new ModelAndView();

        mav.addObject("row", bs.getBoard(id));
        mav.setViewName("board/write");

        return mav;
    }

    @PostMapping("/updateBd/{id}")
    public ModelAndView updateBd(@PathVariable int id, BoardVO input) {
        ModelAndView mav = new ModelAndView();
        input.setId(id);
        bs.upBoard(input);
        mav.setViewName("redirect:/board/view/" + id);

        return mav;
    }

    // 게시판 삭제
    @GetMapping("/deleteBd/{id}")
    public ModelAndView deleteBd(@PathVariable int id) {
        ModelAndView mav = new ModelAndView();
        bs.delBoard(id);
        mav.setViewName("redirect:/board/list");
        return mav;
    }

    // 댓글 추가
    @PostMapping("/addReply")
    public ModelAndView addReply(ReplyVO input, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        MemberVO user = (MemberVO) session.getAttribute("user");
        input.setMember_id(user.getId()); // 세션에서 사용자 ID를 가져와서 설정
        bs.addReply(input);
        mav.setViewName("redirect:/board/view/" + input.getBoard_id());
        return mav;
    }

    // 댓글 삭제
    @PostMapping("/deleteReply/{id}")
    public ModelAndView deleteReply(@PathVariable int id, HttpSession session, @RequestParam("board_id") int boardId) {
        ModelAndView mav = new ModelAndView();
        MemberVO user = (MemberVO) session.getAttribute("user");
        bs.deleteReply(id, user.getId());
        mav.setViewName("redirect:/board/view/" + boardId);
        return mav;
    }

    // 댓글 수정
    @GetMapping("/popUp/{id}") // replyId를 사용하도록 수정
    public ModelAndView updateReply(@PathVariable int id) { //
        ModelAndView mav = new ModelAndView();
        ReplyVO reply = bs.getReply(id);
        mav.addObject("replyId", reply.getId());
        mav.setViewName("board/popUp");
        return mav;
    }

    // 댓글 수정
    @PostMapping("/popUp/{id}")
    public String updateReply(@PathVariable("id") int id, @RequestParam("contents") String contents) {
        ReplyVO input = new ReplyVO();
        input.setId(id);
        input.setContents(contents);
        bs.updateReply(input);
        return "redirect:/board/view/" + id;
    }

    // 내가 쓴 글 조회
    @GetMapping("/wroteBoard")
    public ModelAndView wroteBoard(@RequestParam Map<String, Object> param, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        MemberVO user = (MemberVO) session.getAttribute("user");
        int memberId = user.getId();

        mav.addObject("map", bs.getWroteBoard(param, memberId));
        mav.setViewName("board/wroteBoard");

        return mav;
    }

    // 내가 쓴 댓글 조회
    @GetMapping("/wroteReply")
    public ModelAndView wroteReply(@RequestParam Map<String, Object> param, HttpSession session) {
        ModelAndView mav = new ModelAndView();
        MemberVO user = (MemberVO) session.getAttribute("user");

        int memberId = user.getId();

        mav.addObject("map", bs.getWroteReply(param,memberId));
        mav.setViewName("board/wroteReply");

        return mav;
    }
}
