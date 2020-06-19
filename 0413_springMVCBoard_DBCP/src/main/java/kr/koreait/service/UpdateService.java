package kr.koreait.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.ui.Model;

import kr.koreait.dao.MvcBoardDAO;
import kr.koreait.vo.MvcBoardVO;

public class UpdateService implements MvcBoardService {

	@Override
	public void execute(Model model) {
		System.out.println("UpdateService 클래스의 execute() 메소드 실행");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");

//		request 객체로 넘어온 수정할 글번호와 데이터를 받는다.
		int idx = Integer.parseInt(request.getParameter("idx"));
		String subject = request.getParameter("subject");
		String content = request.getParameter("content");
		
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:applicationCTX.xml");
		MvcBoardDAO mvcBoardDAO = ctx.getBean("mvcBoardDAO", MvcBoardDAO.class);
		
//		글을 수정하는 메소드를 실행한다.
		mvcBoardDAO.update(idx, subject, content);
//		MvcBoardVO mvcBoardVO = ctx.getBean("mvcBoardVO", MvcBoardVO.class);
//		mvcBoardVO.setIdx(idx);
//		mvcBoardVO.setSubject(subject);
//		mvcBoardVO.setContent(content);
//		mvcBoardDAO.update(mvcBoardVO);

//		글 수정 작업 후 돌아갈 페이지 번호를 Model 인터페이스 객체에 저장한다.
		model.addAttribute("currentPage", Integer.parseInt(request.getParameter("currentPage")));
	}

}








