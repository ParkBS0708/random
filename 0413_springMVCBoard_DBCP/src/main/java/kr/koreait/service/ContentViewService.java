package kr.koreait.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.ui.Model;

import kr.koreait.dao.MvcBoardDAO;
import kr.koreait.vo.MvcBoardVO;

public class ContentViewService implements MvcBoardService {

	@Override
	public void execute(Model model) {
		System.out.println("ContentViewService 클래스의 execute() 메소드 실행");
		Map<String, Object> map = model.asMap();
		HttpServletRequest request = (HttpServletRequest) map.get("request");

//		request 객체로 넘어온 조회수를 증가시킨 글번호를 받는다.
		int idx = Integer.parseInt(request.getParameter("idx"));
		AbstractApplicationContext ctx = new GenericXmlApplicationContext("classpath:applicationCTX.xml");
		MvcBoardDAO mvcBoardDAO = ctx.getBean("mvcBoardDAO", MvcBoardDAO.class);

//		조회수를 증가시킨 글 1건을 얻어와서 MvcBoardVO 클래스 객체에 저장한다.
		MvcBoardVO mvcBoardVO = ctx.getBean("mvcBoardVO", MvcBoardVO.class);
		mvcBoardVO = mvcBoardDAO.selectByIdx(idx);
//		System.out.println(mvcBoardVO);

//		브라우저에 출력할 글, 작업 후 돌아갈 페이지 번호, 줄바꿈 구현에 사용할 "\r\n"를 Model 인터페이스 객체에 저장한다.
		model.addAttribute("vo", mvcBoardVO);
		model.addAttribute("currentPage", Integer.parseInt(request.getParameter("currentPage")));
		model.addAttribute("enter", "\r\n");
	}

}










