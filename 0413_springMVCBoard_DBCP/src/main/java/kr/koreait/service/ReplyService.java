package kr.koreait.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.ui.Model;

import kr.koreait.dao.MvcBoardDAO;
import kr.koreait.vo.MvcBoardVO;

public class ReplyService implements MvcBoardService {

	@Override
	public void execute(Model model) {
		System.out.println("ReplyService 클래스의 execute() 메소드 실행");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");

//		request 객체에서 넘어온 답변할 원본글의 글번호, 글그룹, 글레벨, 같은 글그룹에서 출력순서, 답글 작성자 이름, 답글 제목, 답글 내용을
//		받는다.
		int idx = Integer.parseInt(request.getParameter("idx"));
		int ref = Integer.parseInt(request.getParameter("ref"));
		int lev = Integer.parseInt(request.getParameter("lev"));
		int seq = Integer.parseInt(request.getParameter("seq"));
		String name = request.getParameter("name");
		String subject = request.getParameter("subject");
		String content = request.getParameter("content");
		
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:applicationCTX.xml");
//		답글 데이터를 MvcBoardVO 클래스 객체에 저장한다. => 답글은 질문글 바로 아래에 위치해야 하므로 lev와 seq는 1씩 증가시켜 저장한다.
		MvcBoardVO mvcBoardVO = ctx.getBean("mvcBoardVO", MvcBoardVO.class);
		mvcBoardVO.setIdx(idx);
		mvcBoardVO.setRef(ref);
		mvcBoardVO.setLev(lev + 1);
		mvcBoardVO.setSeq(seq + 1);
		mvcBoardVO.setName(name);
		mvcBoardVO.setSubject(subject);
		mvcBoardVO.setContent(content);

		MvcBoardDAO mvcBoardDAO = ctx.getBean("mvcBoardDAO", MvcBoardDAO.class);
//		답글이 삽입될 위치를 정하기 위해 조건에 만족하는 seq를 1씩 증가시키는 메소드를 실행한다.
		HashMap<String, Integer> hmap = new HashMap<String, Integer>();
		hmap.put("ref", mvcBoardVO.getRef());
		hmap.put("seq", mvcBoardVO.getSeq());
		mvcBoardDAO.replyIncrement(hmap);
		
//		답글을 저장하는 메소드를 실행한다.
		mvcBoardDAO.replyInsert(mvcBoardVO);
	}

}











